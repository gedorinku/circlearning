package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.`object`.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.viewModel.CreateProblemViewModel

class CreateProblemActivity : AppCompatActivity(), CreateProblemViewModel.Callback {

    lateinit var binding : ActivityCreateProblemBinding
    lateinit var unitPer : UnitPersonal
    private var nameEnable : Boolean

    init {
        nameEnable = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_problem)
        binding.createView = CreateProblemViewModel(this,this)
        unitPer = application as UnitPersonal
        binding.createView.creatorName = "Made by " + unitPer.userName
    }

    override fun checkNameEnable(enable: Boolean) {
        Log.d("checkBox is ", enable.toString())
        nameEnable = enable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.createView.onActivityResult(requestCode,resultCode,data)
    }

}
