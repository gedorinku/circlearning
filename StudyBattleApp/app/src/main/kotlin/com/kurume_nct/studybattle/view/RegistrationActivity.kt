package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Icon
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kurume_nct.studybattle.Main2Activity
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityRegistrationBinding
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity(), RegistrationViewModel.Callback {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var unitPer : UnitPersonal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.userEntity = RegistrationViewModel(this, this)

        unitPer = application as UnitPersonal


        if (!unitPer.newUser) {
            onLogin(unitPer.userName, "", unitPer.userIcon)
        }

    }

    override fun toLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onLogin(name: String, password: String, icon: Uri) {
        if (unitPer.newUser) {
            unitPer.userName = name
            unitPer.userIcon = icon
            unitPer.writeFile()
            //server
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.userEntity.onActivityResult(resultCode, resultCode, data)
    }

}
