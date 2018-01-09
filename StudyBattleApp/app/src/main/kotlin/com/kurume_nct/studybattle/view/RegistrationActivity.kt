package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.databinding.ActivityRegistrationBinding
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.viewModel.RegistrationViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File

/**
 * A login screen that offers login via email/password.
 */
class RegistrationActivity : AppCompatActivity(), RegistrationViewModel.Callback {

    lateinit var binding: ActivityRegistrationBinding
    lateinit var usersObject: UsersObject
    lateinit var dialog: AlertDialog
    private val STORAGE_CODE = 1
    private val CAMERA_STORAGE_CODE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        binding.viewModel = RegistrationViewModel(this, this)
        usersObject = application as UsersObject

        dialogSetting()

        //skip not beginner
        if (usersObject.authenticationKey != "0") {
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
        Log.d("ほげほげ", resultCode.toString()+ " " + requestCode.toString())
        if(data == null){
            enableButton()
            Log.d("ほげほげ", resultCode.toString() )
            return
        }
        binding.viewModel.onActivityResult(requestCode, resultCode, data)
    }

    override fun stopButton() {
        binding.button3.isClickable = false
        binding.button4.isClickable = false
    }

    override fun enableButton() {
        binding.button3.isClickable = true
        binding.button4.isClickable = true
    }

    override fun alertDialog() {
        stopButton()
        dialog.show()
    }

    private fun dialogSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        dialogView.run {
            cameraButton.setOnClickListener {
                cameraBeforeCheck()
                dialog.dismiss()
            }
            strageButton.setOnClickListener {
                storageBeforeCheck()
                dialog.dismiss()
            }
        }
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()
    }

    private fun storageBeforeCheck() {
        val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        when (permission) {
            PackageManager.PERMISSION_GRANTED -> {
                //storage
                storageStart()
            }
            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_CODE
                )
            }
        }
    }

    private fun cameraBeforeCheck() {
        val yet = arrayListOf(false, false)
        val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        )
        if (permission == PackageManager.PERMISSION_DENIED) {
            yet[0] = true
        }
        val permission2 = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission2 == PackageManager.PERMISSION_DENIED) {
            yet[1] = true
        }
        if (yet.contains(true)) {
            when {
                yet.all { it } -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA),
                        CAMERA_STORAGE_CODE
                )
                yet[0] -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        CAMERA_STORAGE_CODE
                )
                else -> ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CAMERA_STORAGE_CODE
                )
            }
        } else {
            //camera
            cameraStart()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    cameraStart()
                } else {
                    //back to deselect
                    enableButton()
                    Toast.makeText(this, "カメラを起動するためには許可が必要です", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storageStart()
                } else {
                    //back to deselect
                    enableButton()
                    Toast.makeText(this, "写真を参照するには許可が必要です", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cameraStart() {
        //camera
        val cameraFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ""
        )
        cameraFolder.mkdir()
        val fileName = DateTimeFormat.forPattern("ddHHmmss").print(DateTime.now())
        val path = cameraFolder.path + "/" + fileName + ".jpg"
        val uri = FileProvider
                .getUriForFile(this, application.packageName + ".provider", File(path))
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        binding.viewModel.imageUri = uri
        startActivityForResult(intent, 114)
    }

    private fun storageStart() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, 0)
    }
}
