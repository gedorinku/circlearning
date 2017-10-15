package com.kurume_nct.studybattle.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.listFragment.MainListFragment
import com.kurume_nct.studybattle.view.AnswerMainFragment
import com.kurume_nct.studybattle.view.MadeMainFragment
import com.kurume_nct.studybattle.view.ProbemMainFragment
import com.kurume_nct.studybattle.view.SubmittedMainFragment

/**
 * Created by hanah on 9/18/2017.
 */
class MainPagerAdapter(private val mf: FragmentManager) : FragmentPagerAdapter(mf) {

    private val mFragment = ArrayList<Fragment>()

    init {
        mFragment.add(ProbemMainFragment.newInstance())
        mFragment.add(AnswerMainFragment().newInstance())
        mFragment.add(MadeMainFragment().newInstance())
        mFragment.add(SubmittedMainFragment().newInstance())
    }

    override fun getItem(position: Int): Fragment {
        //transration de layout to connect do.
        Log.d("tab", "hoho")
        val transration = mf.beginTransaction()
        val fragment = mFragment[position]
        transration.add(R.id.pager, fragment)
        return fragment
    }

    fun addFragment(fragment: Fragment) {
        mFragment.add(fragment)
    }

    fun onRefreshFragments() {
        mFragment.forEach {
            when (it) {
                is ProbemMainFragment -> {
                    it.onRefresh()
                }
                is AnswerMainFragment -> {
                    it.onRefresh()
                }
                is MadeMainFragment -> {
                    it.onRefresh()
                }
                is SubmittedMainFragment -> {
                    it.onRefresh()
                }
            }
        }
    }

    override fun getCount(): Int = mFragment.size

}