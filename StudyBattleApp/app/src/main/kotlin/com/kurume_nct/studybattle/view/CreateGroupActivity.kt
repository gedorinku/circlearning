package com.kurume_nct.studybattle.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.listFragment.SelectMainPeopleFragment
import com.kurume_nct.studybattle.databinding.ActivityCreateGroupBinding
import com.kurume_nct.studybattle.model.UsersObject
import com.kurume_nct.studybattle.viewModel.CreateGroupViewModel

class CreateGroupActivity : AppCompatActivity(), CreateGroupViewModel.Callback {

    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)

        val fragment = SelectMainPeopleFragment().newInstance(true)
        supportFragmentManager.beginTransaction()
                .replace(R.id.select_people_conteiner, fragment)
                .commit()

        binding.viewModel = CreateGroupViewModel(this, this, fragment = fragment)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun makeGroup() {
        binding.apply {
            onMakeGroupButton.isClickable = false
            viewModel.createGroup()
        }
    }

    override fun onSuccess() {
        val intent = Intent(this, Main2Activity::class.java)
        Toast.makeText(this, "グループの作成に成功", Toast.LENGTH_SHORT).show()
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    override fun onError() {
        binding.onMakeGroupButton.isClickable = true
        Toast.makeText(this, "グループ名を変えてもう一度試してみてください", Toast.LENGTH_SHORT).show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }
}
