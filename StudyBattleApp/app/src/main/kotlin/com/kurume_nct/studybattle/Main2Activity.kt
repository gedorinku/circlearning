package com.kurume_nct.studybattle

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.adapter.MainPagerAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Group
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.CustomViewActivity
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.view.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URL


class Main2Activity : AppCompatActivity() {

    private lateinit var unitPer: UnitPersonal
    private val REQUEST_CREATE_GROUP = 9
    private val REQUEST_PERMISSION_STRAGE = 1
    private lateinit var progressDialog: ProgressDialog
    private lateinit var toolbar: Toolbar
    private lateinit var fab: View
    private lateinit var viewPaper: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var mainPagerAdapter: MainPagerAdapter
    private lateinit var userIcon: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        fab = findViewById(R.id.fab)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        viewPaper = findViewById(R.id.pager) as ViewPager
        tabLayout = findViewById(R.id.tabs) as TabLayout

        unitPer = application as UnitPersonal
        progressDialog = ProgressDialogTool(this).makeDialog()


        toolbar.inflateMenu(R.menu.toolbar_menu)

        listenPermission()
        getUserInformation()

        Log.d(unitPer.nowGroup.name, unitPer.myInfomation.userName)

    }

    private fun getUserInformation() {
        progressDialog.show()
        Log.d("getUserInfo", "")
        val client = ServerClient(unitPer.authenticationKey)
        client
                .verifyAuthentication(unitPer.authenticationKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("userの情報を取得", "")
                    unitPer.myInfomation = it
                    userIcon = Uri.parse(it.icon!!.url)
                    getMyGroup()
                }, {
                    progressDialog.dismiss()
                    //TODO　アプリ再起動
                    Toast.makeText(this, "Userの情報取得に失敗しました." + "アプリを再起動します", Toast.LENGTH_SHORT).show()
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
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getJoinedGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    for (i in it) {
                        groups.add(i)
                        Log.d("list", "group追加")
                    }
                    unitPer.myGroupList = groups
                    unitPer.myGroupCount = unitPer.myGroupList.size
                    Log.d(unitPer.myGroupCount.toString(), "個あります")
                    if (unitPer.myGroupCount == 0) {
                        //join or create group
                        startActivity(Intent(this, CreateGroupActivity::class.java))
                    } else {
                        unitPer.nowGroup = unitPer.myGroupList[0]
                        getIconBitmap()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { it ->
                                    val xSize = it.width
                                    val ySize = it.height
                                    if (xSize > 2000 && ySize > 2000)
                                        viewSetup(Bitmap.createBitmap(it, (xSize - 2000) / 2, (ySize - 2000) / 2, 2000, 2000, null, false))
                                }
                    }
                }, {
                    progressDialog.dismiss()
                    Log.d("Groupの情報を取得するのに失敗", "")
                    Toast.makeText(this, "アプリを立ち上げなおしてください", Toast.LENGTH_SHORT).show()
                })
    }

    fun viewSetup(userIcon: Bitmap) {
        progressDialog.dismiss()
        initOnTabLayout()
        onNavigationDrawer(userIcon)
        onToolBar()
        Log.d(unitPer.myInfomation.id.toString(), "ユーザーID")

    }

    private fun getIconBitmap(): Single<Bitmap> = Single.fromCallable {
        val connection = URL(userIcon.toString()).openConnection()
        val input = connection.run {
            doInput = true
            connect()
            getInputStream()
        }
        BitmapFactory.decodeStream(input)
    }


    private fun onToolBar() {

        fab.setOnClickListener {
            startActivity(Intent(this, CreateProblemActivity::class.java))
        }

        toolbar.title = unitPer.nowGroup.name

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.to_item -> {
                    startActivity(Intent(this, ItemInfoActivity::class.java))
                }
                R.id.to_ranking -> {
                    startActivity(Intent(this, RankingActivity::class.java))
                }
                R.id.to_change_member -> {
                    val intent = Intent(this, GroupSetChangeActivity::class.java)
                    startActivityForResult(intent, 0)
                }
                R.id.to_setting_group -> {
                    startActivity(Intent(this, CustomViewActivity::class.java))
                    Toast.makeText(this, "未実装の機能です。本選までお楽しみに！", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
    }

    private fun onTabLayout() {
        mainPagerAdapter.onRefreshFragments()
    }

    private fun initOnTabLayout() {
        (0 until tabLayout.tabCount).forEach {
            tabLayout.addTab(tabLayout.newTab())
        }
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

        val pagerAdapter = mainPagerAdapter
        viewPaper.adapter = pagerAdapter
        viewPaper.offscreenPageLimit = pagerAdapter.count
        tabLayout.setupWithViewPager(viewPaper)
        Log.d(tabLayout.clipChildren.toString(), "")

        //Create the Tabs
        (0 until tabLayout.tabCount).forEach {
            val tab = tabLayout.getTabAt(it)
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

        unitPer.myGroupList.add(Group())
        // Create the AccountHeader
        val acountCount: Long = 0
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_A700)
                .addProfiles(
                        ProfileDrawerItem()
                                .withName(unitPer.myInfomation.displayName)
                                .withEmail(unitPer.myInfomation.userName)
                                .withIcon(userIcon)
                                .withIdentifier(acountCount)
                )
                .withOnAccountHeaderListener(AccountHeader.OnAccountHeaderListener { view, profile, currentProfile ->
                    //TODO プロフィール設定画面に飛ぶ
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
                    if (position == unitPer.myGroupList.size) {
                        intent = Intent(this, CreateGroupActivity::class.java)
                        startActivity(intent)
                    } else {
                        unitPer.nowGroup = unitPer.myGroupList[position - 1]
                        onToolBar()
                        onTabLayout()
                    }
                    false
                }
                .build()
        //Create the HunachiItem of list
        Log.d(unitPer.myGroupCount.toString(), "すし")
        (0 until unitPer.myGroupCount).forEach {
            result
                    .addItem(PrimaryDrawerItem()
                            .withIdentifier(unitPer.myGroupList[it].id.toLong())
                            .withName(unitPer.myGroupList[it].name)
                            .withIcon(GoogleMaterial.Icon.gmd_people))
        }
        result
                .addItem(PrimaryDrawerItem()
                        .withName("新しくグループを作る")
                        .withIcon(GoogleMaterial.Icon.gmd_add))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            getMyGroup()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("change", unitPer.nowGroup.name)
    }
}


