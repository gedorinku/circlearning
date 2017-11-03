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

class AnswerMainFragment : Fragment(), MainListFragment.Callback {

    lateinit var mContext: Main2Activity
    lateinit var binding: FragmentAnswerMainBinding
    var refreshCounter = 0
    private var fragmentYet: MainListFragment? = null
    private var fragmentFin: MainListFragment? = null

    fun newInstance(): AnswerMainFragment = AnswerMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        binding = FragmentAnswerMainBinding.inflate(inflater, container, false)

        fragmentYet = MainListFragment
                .newInstance(resources.getInteger(R.integer.ANSWER_YET), this)

        fragmentFin = MainListFragment
                .newInstance(resources.getInteger(R.integer.ANSWER_FIN), this)

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_ans_yet, fragmentYet)
                .commit()

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_ans_fin, fragmentFin)
                .commit()

        binding.swipeRefreshFragmentAnswer.setOnRefreshListener {
            onRefresh()
        }

        binding.swipeRefreshFragmentAnswer.setColorSchemeResources(R.color.md_blue_800)

        return binding.root
    }

    fun onRefresh(){
        if(fragmentYet != null)fragmentYet!!.onRefreshList()
        if(fragmentFin != null)fragmentFin!!.onRefreshList()
    }

    override fun onStopSwipeRefresh() {
        refreshCounter++
        if (binding.swipeRefreshFragmentAnswer.isRefreshing && refreshCounter >= 2) {
            binding.swipeRefreshFragmentAnswer.isRefreshing = false
            refreshCounter = 0
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }

}
