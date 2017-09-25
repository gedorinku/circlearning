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
import com.kurume_nct.studybattle.databinding.FragmentSuggestMainBinding

class SuggestMainFragment : Fragment() {

    lateinit var mContext: Main2Activity
    lateinit var binding: FragmentSuggestMainBinding

    fun newinstance() = SuggestMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSuggestMainBinding.inflate(inflater, container, false)
        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_suggested, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.SUGGEST_FIN)))
                .commit()
        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_suggested_yet, MainListFragment()
                        .newInstance(resources.getInteger(R.integer.SUGGEST_YET)))
                .commit()
        return inflater!!.inflate(R.layout.fragment_suggest_main, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }
}
