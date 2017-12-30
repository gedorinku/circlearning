package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.widget.*

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.listFragment.DurationFragment
import com.kurume_nct.studybattle.viewModel.CreateProblemViewModel
import org.joda.time.Duration
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File


class CreateProblemActivity : AppCompatActivity(), CreateProblemViewModel.Callback, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityCreateProblemBinding
    private lateinit var usersObject: UsersObject
    private var isProblem: Boolean
    private lateinit var dialog: AlertDialog
    private val PERMISSION_CAMERA_CODE = 1
    private lateinit var duration: Duration

    init {
        isProblem = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_problem)
        binding.viewModel = CreateProblemViewModel(this, this)

        usersObject = application as UsersObject
        binding.viewModel.creatorName = "Made by " + usersObject.user.displayName

        binding.termHourForOne.isEnabled = false

        supportFragmentManager.beginTransaction()
                .replace(R.id.directions_container, DurationFragment().newInstance())
                .commit()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            dialog.cancel()
            onClickableButtons()
            return
        }
        binding.viewModel.onActivityResult(requestCode, resultCode, data)
    }

    override fun getCreateData(title: String) {
        //send data📩
        val thxAlert = AlertDialog.Builder(this)
        val thxView = this.layoutInflater.inflate(R.layout.dialog_thx, null)
        thxAlert.setOnDismissListener {
            finish()
        }
        thxView.setOnClickListener {
            finish()
        }
        thxAlert.setView(thxView)
        val alert = thxAlert.create()
        alert.apply {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alert.show()
    }

    override fun onDateDialog() {
        DurationFragment().newInstance().show(supportFragmentManager, "tag")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val today = DateTime.now()
        val settingDate = DateTime(year, month + 1, dayOfMonth, today.hourOfDay, today.minuteOfHour, today.secondOfMinute) + Duration.standardMinutes(10)
        duration = Duration(today, settingDate)
        val gup = duration.standardMinutes
        Log.d(gup.toString() + "時間", settingDate.toString())
        binding.termHourForOne.isEnabled = true

        binding.viewModel.let {
            it.day =
                    year.toString() + "年" + (month + 1).toString() + "月" + dayOfMonth.toString() + "日"
            it.termForOne =
                    (gup / 60 / maxOf(usersObject.nowGroup.members.size - 1, 1)).toString() + "時間" +
                            ((gup / maxOf(usersObject.nowGroup.members.size - 1, 1)) % 60).toString() + "分" + it.termExtra
        }
        Log.d(binding.viewModel.day, "change")
    }

    override fun alertDialog(problem: Boolean) {
        onNotClickableButtons()
        isProblem = problem
        Log.d(isProblem.toString(), " dialogs.")
        dialogSetting()
    }

    private fun dialogSetting() {
        val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_camera_strage_choose, null, false
        )
        dialogView.run {
            cameraButton.setOnClickListener {
                cameraBeforeCheck()
            }
            strageButton.setOnClickListener {
                onGetImage(camera = false, problem = isProblem)
            }
        }
        dialog = AlertDialog.Builder(this)
                .setView(dialogView.root)
                .create()
        dialog.setOnDismissListener {
            onClickableButtons()
        }

        dialog.show()
    }

    private fun cameraBeforeCheck() {
        val permission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        )
        when (permission) {
            PackageManager.PERMISSION_GRANTED -> {
                onGetImage(camera = true, problem = isProblem)
            }
            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        PERMISSION_CAMERA_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onGetImage(camera = true, problem = isProblem)
                } else {
                    cameraBeforeCheck()
                }
            }
        }
    }

    /*ありえん所に拡張関数*/
    fun Boolean.toInt() = if (this) 1 else 0

    private fun onGetImage(camera: Boolean, problem: Boolean) {
        if (!camera) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            startActivityForResult(intent, problem.toInt())
        } else {
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
            if (problem) {
                binding.viewModel.problemUri = uri
            } else {
                binding.viewModel.answerUri = uri
            }
            startActivityForResult(intent, problem.toInt())
        }
    }

    override fun onClickableButtons() {
        binding.run {
            problemImage.isClickable = true
            answerImage.isClickable = true
            button7.isClickable = true
            button6.isClickable = true
        }
        dialog.cancel()
    }

    override fun onNotClickableButtons() {
        binding.run {
            problemImage.isClickable = false
            answerImage.isClickable = false
            button7.isClickable = false
            button6.isClickable = false
        }
    }

    override fun getDuration() = duration

    override fun getGroupId() = usersObject.nowGroup.id

    override fun userInformation() = usersObject.user

    override fun getKey() = usersObject.authenticationKey

}
