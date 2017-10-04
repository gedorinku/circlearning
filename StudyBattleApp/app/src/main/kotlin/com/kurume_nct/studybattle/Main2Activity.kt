package com.kurume_nct.studybattle

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.kurume_nct.studybattle.adapter.MainPagerAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.Group
import com.kurume_nct.studybattle.model.UnitPersonal
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
    private lateinit var iconUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        unitPer = application as UnitPersonal
        //userName = unitPer.myInfomation.userName

        getUserInformation()

        Log.d(unitPer.nowGroup.name, unitPer.myInfomation.userName)

    }

    private fun getUserInformation() {
        Log.d("getUserInfo", "")
        val client = ServerClient(unitPer.authenticationKey)
        client
                .verifyAuthentication(unitPer.authenticationKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("userの情報を取得", "")
                    unitPer.myInfomation = it
                    onToolBar()

                    client
                            .getImageById(unitPer.myInfomation.icon!!.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                iconUrl = it.url
                                unitPer.userIcon = Uri.parse(iconUrl)
                                getMyGroup()

                            }, {
                                Toast.makeText(this, "Userの情報取得(画像)に失敗しました", Toast.LENGTH_SHORT).show()
                            })

                }, {
                    Toast.makeText(this, "Userの情報取得に失敗しました", Toast.LENGTH_SHORT).show()
                })
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
                            viewSetup(it)
                        }
                    }
                }, {
                    Log.d("Groupの情報を取得するのに失敗", "")
                    Toast.makeText(this, "アプリを立ち上げなおしてください", Toast.LENGTH_SHORT).show()
                })
    }

    fun viewSetup(userIcon: Bitmap) {

        onTabLayout()
        onNavigationDrawer(userIcon)
        Log.d(unitPer.myInfomation.id.toString(), "ユーザーID")

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //if (data == null) return

        when (resultCode) {
            REQUEST_CREATE_GROUP -> {
                Log.d("hoge","hoge")
                getMyGroup()
            }
        }

    }*/

    private fun getIconBitmap(): Single<Bitmap> = Single.fromCallable {
        BitmapFactory.decodeStream(URL(unitPer.userIcon.toString()).openStream())
    }

    private fun onToolBar() {
        val fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateProblemActivity::class.java))
        }
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = unitPer.myInfomation.userName
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.to_item -> {
                    startActivity(Intent(this, ItemInfoActivity::class.java))
                }
                R.id.to_ranking -> {
                    startActivity(Intent(this, RankingActivity::class.java))
                }
                R.id.to_change_member -> {
                    startActivity(Intent(this, GroupSetChangeActivity::class.java))
                }
                R.id.to_setting_group -> {
                    Toast.makeText(this, "未実装の機能です。本選までお楽しみに！", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }
    }

    private fun onTabLayout() {

        val viewPaper: ViewPager = findViewById(R.id.pager) as ViewPager
        val tabLayout: TabLayout = findViewById(R.id.tabs) as TabLayout

        (0 until tabLayout.tabCount).forEach {
            tabLayout.addTab(tabLayout.newTab())
        }

        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPaper.adapter = pagerAdapter
        viewPaper.offscreenPageLimit = pagerAdapter.count
        tabLayout.setupWithViewPager(viewPaper)

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
                    false
                })
                .build()

        //Create the List
        val result = DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    var intent = Intent(this, Main2Activity::class.java)
                    if (position == unitPer.myGroupList.size) {
                        intent = Intent(this, CreateGroupActivity::class.java)
                        startActivity(intent)
                    } else {
                        unitPer.nowGroup = unitPer.myGroupList[position]
                        onTabLayout()
                    }
                    false
                }
                .build()
        //Create the Item of list
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

    override fun onStart() {
        super.onStart()
        onTabLayout()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("change", unitPer.nowGroup.name)
    }
}


