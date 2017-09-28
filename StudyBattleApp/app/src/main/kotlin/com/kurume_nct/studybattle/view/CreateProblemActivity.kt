package com.kurume_nct.studybattle.view

import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.databinding.ActivityCreateProblemBinding
import com.kurume_nct.studybattle.viewModel.CreateProblemViewModel
import io.reactivex.Single

class CreateProblemActivity : AppCompatActivity(), CreateProblemViewModel.Callback {

    lateinit var binding: ActivityCreateProblemBinding
    lateinit var unitPer: UnitPersonal
    private var nameEnable: Boolean
    private lateinit var alertBuilder: AlertDialog.Builder
    private var prob: Int

    init {
        nameEnable = false
        prob = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_problem)
        binding.createView = CreateProblemViewModel(this, this)
        unitPer = application as UnitPersonal
        binding.createView.creatorName = "Made by " + unitPer.userName

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
        Log.d("checkBox is ", enable.toString())
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
        thxAlert.create().show()
    }

    override fun alertDialog(pro: Int) {
        prob = pro
        alertBuilder.create().show()
    }
}
