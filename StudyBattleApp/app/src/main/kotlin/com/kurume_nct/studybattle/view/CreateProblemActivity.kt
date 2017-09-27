package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.content.DialogInterface
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
    private lateinit var alertBuilder: AlertDialog.Builder
    private var photo : Int

    init {
        nameEnable = false
        photo = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_problem)
        binding.createView = CreateProblemViewModel(this,this)
        unitPer = application as UnitPersonal
        binding.createView.creatorName = "Made by " + unitPer.userName
    }

    fun setAlert(){
        alertBuilder = AlertDialog.Builder(this)
                .setMessage("")
                .setTitle("")
                .setPositiveButton("",DialogInterface
                        .OnClickListener { dialog, which ->
                            photo = 1
                        })
                .setNegativeButton("",DialogInterface
                        .OnClickListener { dialog, which ->
                            photo = 2
                        })
    }

    override fun checkNameEnable(enable: Boolean) {
        Log.d("checkBox is ", enable.toString())
        nameEnable = enable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.createView.onActivityResult(requestCode,resultCode,data)
    }

    override fun getProblemPhoto() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAnswerPhoto() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun alertDialog(): Int {
        photo = -1
        alertBuilder.create()
        return photo
    }
}
