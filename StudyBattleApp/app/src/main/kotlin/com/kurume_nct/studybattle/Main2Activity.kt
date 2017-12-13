package com.kurume_nct.studybattle

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.kurume_nct.studybattle.adapter.MainPagerAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Group
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.view.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL


class Main2Activity : AppCompatActivity() {

    private lateinit var usersObject: UsersObject
    private val REQUEST_PERMISSION_STRAGE = 1
    private lateinit var progressDialog: ProgressDialog
    private var toolbar: Toolbar? = null
    private lateinit var fab: View
    private var viewPaper: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private lateinit var mainPagerAdapter: MainPagerAdapter
    private lateinit var userIcon: Uri
    private var stopButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        viewPaper = findViewById(R.id.pager) as ViewPager
        tabLayout = findViewById(R.id.tabs) as TabLayout

        usersObject = application as UsersObject

        toolbar?.inflateMenu(R.menu.toolbar_menu)

        stopButton = true
        listenPermission()
        getUserInformation()

        Log.d(usersObject.nowGroup.name, usersObject.user.userName)

    }

    private fun getUserInformation() {
        stopButton = true
        progressDialog = ProgressDialogTool(this).makeDialog()
        if (!progressDialog.isShowing) progressDialog.show()
        Log.d("getUserInfo", "")
        val client = ServerClient(usersObject.authenticationKey)
        client
                .verifyAuthentication(usersObject.authenticationKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("userの情報を取得", "")
                    usersObject.user = it
                    userIcon = Uri.parse(it.icon!!.url)
                    getMyGroup()
                }, {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    stopButton = false
                    Toast.makeText(this, "Loginしなおしてください", Toast.LENGTH_SHORT).show()
                    usersObject.deleteFile()
                    Log.d(usersObject.authenticationKey, "メイン")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                })
    }

    private fun listenPermission() {
        val permissionCheckWrite = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCheckRead = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED ||
                permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_STRAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_STRAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Strageの", "permissionをget")
                } else {
                    Toast.makeText(this, "Permissionをください", Toast.LENGTH_SHORT).show()
                    listenPermission()
                }
        }
    }

    private fun getMyGroup() {
        Log.d("getMyGroup", "")

        val groups = mutableListOf<Group>()
        val client = ServerClient(usersObject.authenticationKey)
        client
                .getJoinedGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    for (i in it) {
                        groups.add(i)
                        Log.d("list", "group追加")
                    }
                    usersObject.myGroupList = groups
                    Log.d(usersObject.myGroupCount.toString(), "個あります")
                    if (usersObject.myGroupCount == 0) {
                        //join or create group
                        startActivity(Intent(this, CreateGroupActivity::class.java))
                    } else {
                        usersObject.nowGroup = usersObject.myGroupList[0]
                        getIconBitmap()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { bitmap ->
                                    val xSize = bitmap.width
                                    val ySize = bitmap.height
                                    var userIcon = bitmap
                                    if (xSize > ySize)
                                        userIcon = Bitmap.createBitmap(userIcon, (xSize - ySize) / 2, 0, ySize, ySize, null, false)
                                    else if (ySize > xSize)
                                        userIcon = Bitmap.createBitmap(userIcon, 0, (ySize - xSize) / 2, xSize, xSize, null, false)

                                    viewSetup(userIcon)
                                }
                    }
                }, {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Log.d("Groupの情報を取得するのに失敗", "")
                    Toast.makeText(this, "Groupの情報がありません", Toast.LENGTH_SHORT).show()
                    usersObject.deleteFile()
                    if (usersObject.authenticationKey == "0") startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                })
    }

    fun viewSetup(userIcon: Bitmap) {
        if (progressDialog.isShowing) progressDialog.dismiss()
        initOnTabLayout()
        onNavigationDrawer(userIcon)
        onToolBar()
        stopButton = false
        Log.d(usersObject.user.id.toString(), "ユーザーID")
    }

    private fun getIconBitmap(): Single<Bitmap> = Single.fromCallable {
        Log.d("bitMap", "に変換中・・・。")
        BitmapFactory.decodeStream(URL(userIcon.toString()).openStream())
    }


    private fun onToolBar() {

        fab.setOnClickListener {
            if (!stopButton)
                startActivity(Intent(this, CreateProblemActivity::class.java))
        }

        if (toolbar == null) toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar?.title = usersObject.nowGroup.name

        toolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.to_item -> {
                    if (!stopButton)
                        startActivity(Intent(this, ItemInfoActivity::class.java))
                }
                R.id.to_ranking -> {
                    if (!stopButton)
                        startActivity(Intent(this, RankingActivity::class.java))
                }
                R.id.to_change_member -> {
                    val intent = Intent(this, GroupSetChangeActivity::class.java)
                    if (!stopButton) startActivityForResult(intent, 0)
                }
            }
            false
        }
    }

    private fun onTabLayout() {
        mainPagerAdapter.onRefreshFragments()
    }

    private fun initOnTabLayout() {
        if (viewPaper == null) viewPaper = findViewById(R.id.pager) as ViewPager
        if (tabLayout == null) tabLayout = findViewById(R.id.tabs) as TabLayout
        (0 until tabLayout?.tabCount!!).forEach {
            tabLayout?.addTab(tabLayout?.newTab()!!)
        }
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

        val pagerAdapter = mainPagerAdapter
        viewPaper?.adapter = pagerAdapter
        viewPaper?.offscreenPageLimit = pagerAdapter.count
        tabLayout?.setupWithViewPager(viewPaper)
        Log.d(tabLayout?.clipChildren.toString(), "")

        //Create the Tabs
        (0 until tabLayout?.tabCount!!).forEach {
            val tab = tabLayout?.getTabAt(it)
            when (it) {
                0 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_0, null)
                1 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_1, null)
                2 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_2, null)
                3 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_3, null)
            }
        }
    }

    private fun onNavigationDrawer(userIcon: Bitmap) {
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        usersObject.myGroupList.add(Group())
        // Create the AccountHeader
        val acountCount: Long = 0
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_A700)
                .addProfiles(
                        ProfileDrawerItem()
                                .withName(usersObject.user.displayName)
                                .withEmail(usersObject.user.userName)
                                .withIcon(userIcon)
                                .withIdentifier(acountCount)
                )
                .withOnAccountHeaderListener({ view, profile, currentProfile ->
                    false
                })
                .build()

        //Create the List
        val result = DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    val intent: Intent
                    //positionが1indexなので注意。
                    if (position == usersObject.myGroupList.size) {
                        intent = Intent(this, CreateGroupActivity::class.java)
                        if (!stopButton) startActivity(intent)
                    } else {
                        if (!stopButton) {
                            usersObject.nowGroup = usersObject.myGroupList[position - 1]
                            onToolBar()
                            onTabLayout()
                        } else {
                            Toast.makeText(this, "処理中です。少し待ってもう一度お試しください。", Toast.LENGTH_SHORT).show()
                        }
                    }
                    false
                }
                .build()
        //Create the HunachiItem of list
        Log.d(usersObject.myGroupCount.toString(), "すし")
        (0 until usersObject.myGroupCount).forEach {
            result
                    .addItem(PrimaryDrawerItem()
                            .withIdentifier(usersObject.myGroupList[it].id.toLong())
                            .withName(usersObject.myGroupList[it].name)
                            .withIcon(GoogleMaterial.Icon.gmd_people))
        }
        result
                .addItem(PrimaryDrawerItem()
                        .withName("新しくグループを作る")
                        .withIcon(GoogleMaterial.Icon.gmd_add))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> getMyGroup()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("change", usersObject.nowGroup.name)
    }
}


