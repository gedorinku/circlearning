package com.kurume_nct.studybattle.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.kurume_nct.studybattle.Main2Activity
import com.kurume_nct.studybattle.viewModel.GroupSetChangeViewModel

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.listFragment.SelectMainPeopleFragment
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.ActivityGroupSetChangeBinding
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

class GroupSetChangeActivity : AppCompatActivity(), GroupSetChangeViewModel.Callback {

    private lateinit var binding: ActivityGroupSetChangeBinding
    lateinit var unitPersonal: UnitPersonal
    lateinit var fragment: SelectMainPeopleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("i'm ", javaClass.name)

        fragment = SelectMainPeopleFragment().newInstance(false)

        unitPersonal = application as UnitPersonal

        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_set_change)
        binding.groupSetView = GroupSetChangeViewModel(this, this)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search_list, fragment)
                .commit()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onChange() {
        fragment.getPeopleList().forEach {
            ServerClient(unitPersonal.authenticationKey)
                    .attachToGroup(unitPersonal.nowGroup, it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {}
            Log.d(it.displayName,"追加された")
        }
        finish()
    }

    override fun onGoodbye() {
        confirmDialog()
    }

    private fun getOutGroup(){
        Toast.makeText(this, "Groupから退出中・・・", Toast.LENGTH_SHORT).show()
        ServerClient(unitPersonal.authenticationKey)
                .leaveGroup(unitPersonal.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    setResult(0)
                    finish()
                }
    }

    private fun confirmDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("本当に今のグループから抜けてもいいですか？")
        builder.setMessage("このグループ内でのデータが完全に消える可能性があります")
        builder.setNegativeButton("いいえ", null)
        builder.setPositiveButton("はい", DialogInterface.OnClickListener {
            dialog, which -> getOutGroup()
        })
        builder.create().show()
    }

}
