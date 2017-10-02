package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.databinding.DialogCameraStrageChooseBinding
import com.kurume_nct.studybattle.listFragment.DirectionFragment
import com.kurume_nct.studybattle.viewModel.CreateProblemViewModel

class CreateProblemActivity : AppCompatActivity(), CreateProblemViewModel.Callback, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityCreateProblemBinding
    private lateinit var unitPer: UnitPersonal
    private var nameEnable: Boolean
    private lateinit var alertBuilder: AlertDialog.Builder
    private var prob: Int
    private var direction : Long = 6
    private lateinit var list : ExpandableListView

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
        binding.createView.creatorName = "Made by " + unitPer.userName

        dialogSetting()

        supportFragmentManager.beginTransaction()
                .replace(R.id.directions_container, DirectionFragment().newInstance())
                .commit()

        DatSetting()
    }

    fun DatSetting(){
        val date = DirectionFragment().onGetInitDate()
        binding.createView.let {
            it.day = date[0].toString() + "年" + date[1].toString() + "月" + date[2].toString() + "日"
        }
    }


    fun dialogSetting(){
        /*val dialogView: DialogCameraStrageChooseBinding = DataBindingUtil.setContentView(this ,R.layout.dialog_camera_strage_choose)
        dialogView.run {
            cameraButton.setOnClickListener {
                binding.createView.onGetImage(0, prob)
            }
            strageButton.setOnClickListener {
                binding.createView.onGetImage(1, prob)
            }
        }

        alertBuilder = AlertDialog.Builder(this)
                .setView(dialogView.root)*/

        alertBuilder = AlertDialog.Builder(this)
                .setTitle("画像を取得する方法を選んでください")
                .setPositiveButton("フォルダ📁", { dialog, which ->
                    binding.createView.onGetImage(0, prob)
                })
                .setNegativeButton("カメラ📷", { dialog, which ->
                    binding.createView.onGetImage(1, prob)
                })
    }

    override fun checkNameEnable(enable: Boolean) {
        nameEnable = enable
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.createView.onActivityResult(requestCode, resultCode, data)
    }

    override fun getCreateData(title: String, problemUri: Uri, answerUri: Uri) {
        //send data📩
        val thxAlert = AlertDialog.Builder(this)
        val thxView = this.layoutInflater.inflate(R.layout.thx_dialog, null)
        thxAlert.setOnDismissListener {
            finish()
        }
        thxAlert.setView(thxView)
        val alert = thxAlert.create()
        alert.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.show()
    }

    override fun onDateDialog() {
        DirectionFragment().newInstance().show(supportFragmentManager,"tag")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.createView.let {
            it.day = year.toString() + "年" + (month + 1).toString() + "月" + dayOfMonth.toString() + "日"
        }
        Log.d(binding.createView.day,"change")
    }

    override fun alertDialog(pro: Int) {
        prob = pro
        val alert =  alertBuilder.create()
        alert.show()
    }
}
