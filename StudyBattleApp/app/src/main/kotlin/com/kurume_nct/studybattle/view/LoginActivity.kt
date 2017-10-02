package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityLoginBinding
import com.kurume_nct.studybattle.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity(), LoginViewModel.Callback {

    private lateinit var binding : ActivityLoginBinding
    lateinit var unitPer : UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.userLogin = LoginViewModel(this,this)
        unitPer = application as UnitPersonal

        //skip
        if(!unitPer.newUser){
            toMain2Activity(unitPer.userName)
        }

    }

    fun toMain2Activity(name: String){
        val intent = Intent(this,Main2Activity::class.java)
        //add photo and userName
        if(unitPer.newUser) {
            unitPer.userName = name
            unitPer.writeFile()
            //server
        }
        startActivity(intent)
        finish()
    }

    override fun onLogin(displayName: String, authentication: String) {
        //unitPer.autheticationKey = authentication
        toMain2Activity(displayName)
    }

    override fun toRegisterActivity() {
        val intent = Intent(this,RegistrationActivity::class.java)
        startActivity(intent)
    }
}
