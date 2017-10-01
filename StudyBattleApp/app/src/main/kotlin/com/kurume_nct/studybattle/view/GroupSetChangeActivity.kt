package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.GroupSetChangeViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityGroupSetChangeBinding
import com.kurume_nct.studybattle.listFragment.GroupListFragment
import com.kurume_nct.studybattle.listFragment.MainListFragment

class GroupSetChangeActivity : AppCompatActivity(), GroupSetChangeViewModel.Callback {

    private lateinit var binding: ActivityGroupSetChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_set_change)
        binding.groupSetView = GroupSetChangeViewModel(this, this)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search_list, GroupListFragment().newInstance(3))
                .commit()
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_select_list, GroupListFragment().newInstance(4))
                .commit()
    }

    override fun onChange() {
        finish()
    }

    override fun onGoodbye() {
        //Dialog
        finish()
    }
}
