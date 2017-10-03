package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.bug.SelectMainPeopleFragment
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityCreateGroupBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.CreateGroupViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CreateGroupActivity : AppCompatActivity(), CreateGroupViewModel.Callback {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var unitPer: UnitPersonal
    private val REQUEST_CREATE_GROUP = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.createGroupUnit = CreateGroupViewModel(this, this)
        unitPer = application as UnitPersonal

        supportFragmentManager.beginTransaction()
                .replace(R.id.select_people_conteiner, SelectMainPeopleFragment().newInstance(0))
                .commit()

    }

    private fun createGroup() {
        val client = ServerClient(unitPer.authenticationKey)
        client
                .createGroup(binding.createGroupUnit.groupName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    unitPer.myGroupList.add(it)
                    val intent = Intent(this, Main2Activity::class.java)
                    startActivity(intent)
                    finish()
                }, {
                    it.printStackTrace()
                    binding.button10.isClickable = true
                    Toast.makeText(this, "グループの作成に失敗", Toast.LENGTH_SHORT).show()
                    Log.d("グループの作成に失敗", "")
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun makeGroup() {
        binding.button10.isClickable = false
        createGroup()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
