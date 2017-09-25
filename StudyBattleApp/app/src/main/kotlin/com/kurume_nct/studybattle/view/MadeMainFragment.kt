package com.kurume_nct.studybattle.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.ListFragment.MainListFragment
import com.kurume_nct.studybattle.Main2Activity
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentMadeMainBinding

class MadeMainFragment : Fragment() {

    lateinit var mContent : Main2Activity
    lateinit var binding : FragmentMadeMainBinding

    fun newInstance() = MadeMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMadeMainBinding.inflate(inflater,container,false)

        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_collect_yet,MainListFragment()
                        .newInstance(resources.getInteger(R.integer.MADE_COLLECT_YET)))
                .commit()
        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_yet, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.MADE_JUDGE_YET)))
                .commit()
        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_fin, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.MADE_FIN)))
                .commit()
        return binding.root
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContent = context as Main2Activity
    }
}
