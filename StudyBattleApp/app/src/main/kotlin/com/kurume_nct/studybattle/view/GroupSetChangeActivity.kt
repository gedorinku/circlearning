package com.kurume_nct.studybattle.view

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kurume_nct.studybattle.viewModel.GroupSetChangeViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.bug.SelectMainPeopleFragment
import com.kurume_nct.studybattle.databinding.ActivityGroupSetChangeBinding
import com.kurume_nct.studybattle.listFragment.GroupListFragment
import com.kurume_nct.studybattle.model.UnitPersonal

class GroupSetChangeActivity : AppCompatActivity(), GroupSetChangeViewModel.Callback {

    private lateinit var binding: ActivityGroupSetChangeBinding
    lateinit var unitPersonal: UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        unitPersonal = application as UnitPersonal

        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_set_change)
        binding.groupSetView = GroupSetChangeViewModel(this, this)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search_list, SelectMainPeopleFragment().newInstance(3))
                .commit()

    }

    override fun onChange() {
        finish()
    }

    override fun onGoodbye() {
        //Dialog
        unitPersonal.deleteFile()
        finish()
    }
}
