package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.listFragment.SelectMainPeopleFragment
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityCreateGroupBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.viewModel.CreateGroupViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CreateGroupActivity : AppCompatActivity(), CreateGroupViewModel.Callback {

    private lateinit var binding: ActivityCreateGroupBinding
    private lateinit var unitPer: UnitPersonal
    private lateinit var fragment: SelectMainPeopleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        binding.viewModel = CreateGroupViewModel(this, this)
        unitPer = application as UnitPersonal

        fragment = SelectMainPeopleFragment().newInstance(true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.select_people_conteiner, fragment)
                .commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun makeGroup() {
        binding.apply {
            button10.isClickable = false
            viewModel.createGroup()
        }
    }

    override fun getFragment() = fragment

    override fun onSuccess() {
        val intent = Intent(this, Main2Activity::class.java)
        Toast.makeText(this, "グループの作成に成功", Toast.LENGTH_SHORT).show()
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onError() {
        binding.button10.isClickable = true
        Toast.makeText(this, "グループ名を変えてもう一度試してみてください", Toast.LENGTH_SHORT).show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}
