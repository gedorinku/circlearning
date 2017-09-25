package com.kurume_nct.studybattle.view

import android.content.Context
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.ListFragment.MainListFragment
import com.kurume_nct.studybattle.Main2Activity

import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentAnswerMainBinding
import java.util.*

class AnswerMainFragment : Fragment() {

    lateinit var mContext : Main2Activity
    lateinit var binding : FragmentAnswerMainBinding

    fun newInstanse() : AnswerMainFragment = AnswerMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentAnswerMainBinding.inflate(inflater,container,false)

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_yet, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.ANSWER_YET)))
                .commit()

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_fin, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.ANSWER_FIN)))
                .commit()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }

}
