package com.kurume_nct.studybattle.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.listFragment.MainListFragment
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentAnswerMainBinding

class AnswerMainFragment : Fragment() {

    lateinit var mContext : Main2Activity
    lateinit var binding : FragmentAnswerMainBinding

    fun newInstance() : AnswerMainFragment = AnswerMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        binding = FragmentAnswerMainBinding.inflate(inflater,container,false)

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_ans_yet, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.ANSWER_YET)))
                .commit()

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_ans_fin, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.ANSWER_FIN)))
                .commit()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }

}
