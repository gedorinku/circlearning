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
import com.kurume_nct.studybattle.databinding.FragmentMadeMainBinding

class MadeMainFragment : Fragment(), MainListFragment.Callback {

    lateinit var mContent: Main2Activity
    lateinit var binding: FragmentMadeMainBinding
    var refreshCounter = 0

    fun newInstance() = MadeMainFragment()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        binding = FragmentMadeMainBinding.inflate(inflater, container, false)

        val fragmentCollectYet = MainListFragment
                .newInstance(resources.getInteger(R.integer.MADE_COLLECT_YET), this)

        val fragmentJudgeYet = MainListFragment
                .newInstance(resources.getInteger(R.integer.MADE_JUDGE_YET), this)

        val fragmentFin = MainListFragment
                .newInstance(resources.getInteger(R.integer.MADE_FIN), this)

        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_collect_yet, fragmentCollectYet)
                .commit()
        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_yet, fragmentJudgeYet)
                .commit()
        mContent.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list_made_fin, fragmentFin)
                .commit()

        binding.swipeRefreshFragmentMade.setOnRefreshListener {
            fragmentCollectYet.onRefreshList()
            fragmentJudgeYet.onRefreshList()
            fragmentFin.onRefreshList()
        }

        binding.swipeRefreshFragmentMade.setColorSchemeResources(R.color.md_yellow_400, R.color.md_blue_A700, R.color.md_red_A700)

        return binding.root
    }

    override fun onStopSwipeRefresh() {
        refreshCounter++
        if (binding.swipeRefreshFragmentMade.isRefreshing && refreshCounter >= 3) {
            binding.swipeRefreshFragmentMade.isRefreshing = false
            refreshCounter = 0
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContent = context as Main2Activity
    }
}
