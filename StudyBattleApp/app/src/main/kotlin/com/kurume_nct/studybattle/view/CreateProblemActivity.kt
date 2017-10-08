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
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.widget.*

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.listFragment.DurationFragment
import com.kurume_nct.studybattle.viewModel.CreateProblemViewModel
import org.joda.time.Duration
import android.support.v4.app.ActivityCompat


class CreateProblemActivity : AppCompatActivity(), CreateProblemViewModel.Callback, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityCreateProblemBinding
    private lateinit var unitPer: UnitPersonal
    private var nameEnable: Boolean
    private var prob: Int
    private lateinit var decideDate: MutableList<Int>
    private lateinit var dialog: AlertDialog
    private val PERMISSION_CAMERA_CODE = 1

    init {
        nameEnable = false
        prob = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_problem)
        binding.createView = CreateProblemViewModel(this, this)

        unitPer = application as UnitPersonal
        binding.createView.creatorName = "Made by " + unitPer.myInfomation.displayName

        //dialogSetting()

        supportFragmentManager.beginTransaction()
                .replace(R.id.directions_container, DurationFragment().newInstance())
                .commit()

        DataSetting()
    }

    private fun DataSetting() {
        val date = DurationFragment().onGetInitDate()
        decideDate = date
        binding.createView.let {
            it.day = date[0].toString() + "年" + date[1].toString() + "月" + date[2].toString() + "日"
        }
    }


    override fun checkNameEnable(enable: Boolean) {
        nameEnable = enable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            dialog.cancel()
            onClickableButtons()
            return
        }
        binding.createView.onActivityResult(requestCode, resultCode, data)
    }

    override fun getCreateData(title: String) {
        //send data📩
        val thxAlert = AlertDialog.Builder(this)
        val thxView = this.layoutInflater.inflate(R.layout.dialog_thx, null)
        thxAlert.setOnDismissListener {
            finish()
        }
        thxAlert.setOnKeyListener { dialog, keyCode, event ->
            finish()
            false
        }
        thxAlert.setView(thxView)
        val alert = thxAlert.create()
        alert.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    override fun onDateDialog() {
        DurationFragment().newInstance().show(supportFragmentManager, "tag")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        decideDate = mutableListOf(year, month, dayOfMonth)
        binding.createView.let {
            it.day = year.toString() + "年" + (month + 1).toString() + "月" + dayOfMonth.toString() + "日"
        }
        Log.d(binding.createView.day, "change")
    }

    override fun alertDialog(pro: Int) {
        onNotClickableButtons()
        prob = pro
        Log.d(prob.toString(), " dialogs.")
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
                binding.createView.onGetImage(0, prob)
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
                binding.createView.onGetImage(1, prob)
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
                    binding.createView.onGetImage(1, prob)
                } else {
                    cameraBeforeCheck()
                }
            }
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

    override fun getDuration(): Duration {
        val date = DurationFragment().onGetToday()
        var dayCount = 0
        if (date[0] < decideDate[0]) {
            dayCount = 30 * (date[1] - decideDate[1] + 12) - date[2] + decideDate[2]
        } else if (date[1] < decideDate[1]) {
            dayCount = 30 * (decideDate[1] - date[1]) - date[2] + decideDate[2]
        } else {
            dayCount = decideDate[2] - date[2]
        }
        return Duration.standardDays(dayCount.toLong())
    }

    override fun getGroupId() = unitPer.nowGroup.id

    override fun userInformation() = unitPer.myInfomation

    override fun getKey() = unitPer.authenticationKey

}
