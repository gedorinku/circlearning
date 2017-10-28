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
import com.kurume_nct.studybattle.databinding.FragmentSubmittedMainBinding

class SubmittedMainFragment : Fragment(), MainListFragment.Callback {

    lateinit var mContext: Main2Activity
    lateinit var binding: FragmentSubmittedMainBinding
    var refreshCounter = 0
    lateinit var fragmentYet: MainListFragment
    lateinit var fragmentFin: MainListFragment

    fun newInstance() = SubmittedMainFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)

        fragmentFin = MainListFragment
                .newInstance(resources.getInteger(R.integer.SUBMIT_FIN), this)

        fragmentYet = MainListFragment
                .newInstance(resources.getInteger(R.integer.SUBMIT_YET), this)

        binding = FragmentSubmittedMainBinding.inflate(inflater, container, false)

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_suggested, fragmentFin)
                .commit()
        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_suggested_yet, fragmentYet)
                .commit()

        binding.swipeRefreshFragmentSubmit.setOnRefreshListener {
            onRefresh()
        }

        //binding.swipeRefreshFragmentSubmit.setColorSchemeResources(R.color.md_red_700, R.color.md_yellow_700)
        binding.swipeRefreshFragmentSubmit.setColorSchemeResources(R.color.md_blue_800)

        return binding.root
    }

    fun onRefresh(){
        fragmentYet.onRefreshList()
        fragmentFin.onRefreshList()
    }

    override fun onStopSwipeRefresh() {
        refreshCounter++
        if (binding.swipeRefreshFragmentSubmit.isRefreshing && refreshCounter >= 2) {
            binding.swipeRefreshFragmentSubmit.isRefreshing = false
            refreshCounter = 0
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }
}
