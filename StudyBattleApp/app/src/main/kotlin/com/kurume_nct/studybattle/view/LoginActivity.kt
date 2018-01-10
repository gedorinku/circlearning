package com.kurume_nct.studybattle.view

import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.databinding.ActivityLoginBinding
import com.kurume_nct.studybattle.tools.ProgressDialogTool
import com.kurume_nct.studybattle.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity(), LoginViewModel.Callback {

    private lateinit var binding: ActivityLoginBinding
    lateinit var usersObject: UsersObject
    lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        usersObject = application as UsersObject
        progress = ProgressDialogTool(this).makeDialog()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = LoginViewModel(this, this)

        //skip
        if (usersObject.authenticationKey != "0") {
            toMain2Activity()
        }
    }

    private fun toMain2Activity() {
        val intent = Intent(this, Main2Activity::class.java)
        if (usersObject.authenticationKey != "0") {
            Log.d(usersObject.authenticationKey, "ログイン")
            startActivity(intent)
            finish()
        }
    }

    override fun onLogin(authentication: String) {
        usersObject.authenticationKey = authentication
        usersObject.writeFile()
        toMain2Activity()
    }

    override fun toRegisterActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun stopButton() {
        progress.show()
        binding.apply {
            button2.isClickable = false
            button5.isClickable = false
        }
    }

    override fun clickableButton() {
        progress.dismiss()
        binding.apply {
            button2.isClickable = true
            button5.isClickable = true
        }
    }

}
