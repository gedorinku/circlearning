package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityRegistrationBinding
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity(), RegistrationViewModel.Callback {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var unitPer: UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.userEntity = RegistrationViewModel(this, this)
        unitPer = application as UnitPersonal

        //skip
        if (unitPer.authenticationKey != "0") {
            onLogin()
        }

    }

    override fun toLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.userEntity.onActivityResult(resultCode, resultCode, data)
    }

    override fun stopButton() {
        binding.button3.isClickable = false
        binding.button4.isClickable = false
    }

    override fun ableButton(){
        binding.button3.isClickable = true
        binding.button4.isClickable = true
    }

}
