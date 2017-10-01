package com.kurume_nct.studybattle

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*

import com.kurume_nct.studybattle.adapter.MainPagerAdapter
import com.kurume_nct.studybattle.model.Person_
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem


class Main2Activity : AppCompatActivity() {

    private var userName = "Kotlin"
    private lateinit var unitPer : UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //userName = intent.getStringExtra("userName") ?: userName
        unitPer = application as UnitPersonal
        userName = unitPer.userName
        Log.d(userName,unitPer.userName)
        onTabLayout()
        onNavigationDrower()
        onToolBar()
    }


    fun onToolBar(){
        val fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateProblemActivity::class.java))
        }
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = userName
        toolbar.inflateMenu(R.menu.toolbar_menu)
        toolbar.setOnMenuItemClickListener {
            item ->
            when(item.itemId){
                R.id.to_item -> startActivity(Intent(this, ItemInfoActivity::class.java))
                R.id.to_ranking -> startActivity(Intent(this, RankingActivity::class.java))
            }
            false
        }
    }

    fun onTabLayout(){

        val viewPaper : ViewPager = findViewById(R.id.pager) as ViewPager
        val tabLayout : TabLayout = findViewById(R.id.tabs) as TabLayout

        (0 until tabLayout.tabCount).forEach{
            tabLayout.addTab(tabLayout.newTab())
        }

        val pagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPaper.adapter = pagerAdapter
        viewPaper.offscreenPageLimit = pagerAdapter.count
        tabLayout.setupWithViewPager(viewPaper)

        //Create the Tabs
        (0 until tabLayout.tabCount).forEach {
            val tab = tabLayout.getTabAt(it)
            when(it) {
                0 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_0,null)
                1 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_1,null)
                2 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_2,null)
                3 -> tab?.customView =
                        LayoutInflater.from(this).inflate(R.layout.tab_custom_3,null)
            }
        }
    }

    fun onNavigationDrower(){
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val groupID : Int = intent.getIntExtra("groupID",0)
        val list : MutableList<Person_> = mutableListOf(Person_(id = 0))
        list.add(Person_(id = list.size))
        // Create the AccountHeader
        var acountCount : Long = 0
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_A700)
                .addProfiles(
                        ProfileDrawerItem()
                                .withName(userName)
                                .withEmail("GroupID is " + groupID.toString())
                                .withIcon(R.drawable.icon_gost)
                                .withIdentifier(acountCount)
                )
                .withOnAccountHeaderListener(AccountHeader.OnAccountHeaderListener { view, profile, currentProfile -> false })
                .build()
        //Add the Account
        acountCount++
        headerResult.addProfiles(
                ProfileDrawerItem()
                        .withName("hunachi-bata")
                        .withEmail("GroupID is " + groupID.toString())
                        .withIcon(R.drawable.icon)
                        .withIdentifier(acountCount)
        )

        //Create the List
        val result = DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener {
                    view, position, drawerItem ->
                    var intent = Intent(this,Main2Activity::class.java)
                    if(position == list.size + 1){
                        intent.putExtra("groupID",position)
                        //intent.putExtra("userName",userName)
                        intent = Intent(this,RegistrationActivity::class.java)
                        startActivity(intent)
                        finish()
                        //Still i have to update Main2Activity
                    }else{
                        intent.putExtra("groupID",position)
                        //intent.putExtra("userName",userName)
                        startActivity(intent)
                        finish()
                    }
                    false
                }
                .build()
        //Create the Item of list
        for(a in list){
            result.addItem(PrimaryDrawerItem().withIdentifier(a.id.toLong()).withName(a.name).withIcon(GoogleMaterial.Icon.gmd_people))
        }
      //  for ((name, id) in list) result.addItem(PrimaryDrawerItem().withIdentifier(id.toLong()).withName(name).withIcon(GoogleMaterial.Icon.gmd_people))
        result.addItem(PrimaryDrawerItem().withIdentifier(list.size.toLong()).withName("新しくグループを作る").withIcon(GoogleMaterial.Icon.gmd_add))
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("change","?")
    }
}


