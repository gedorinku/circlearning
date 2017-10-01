package com.kurume_nct.studybattle

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ActivityCreateGroupBinding

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        supportFragmentManager.beginTransaction()
                .replace(R.id.select_people_conteiner, SelectMainPeopleFragment().newInstance(0))
                .commit()
    }
}
