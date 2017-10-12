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
import com.kurume_nct.studybattle.databinding.FragmentProbemMainBinding


class ProbemMainFragment : Fragment(), MainListFragment.Callback {

    private lateinit var mContext: Main2Activity
    private lateinit var binding: FragmentProbemMainBinding
    private lateinit var fragment: MainListFragment

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)

        binding = FragmentProbemMainBinding.inflate(inflater, container, false)

        fragment = MainListFragment
                .newInstance(resources.getInteger(R.integer.HAVE_PROBLEM), this)

        mContext.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_list, fragment)
                .commit()

        binding.swipeRefreshFragmentHave.setOnRefreshListener {
            fragment.onRefreshList()
        }

        binding.swipeRefreshFragmentHave.setColorSchemeResources(R.color.md_red_A700)

        return binding.root
    }

    override fun onStopSwipeRefresh() {
        if (binding.swipeRefreshFragmentHave.isRefreshing)
            binding.swipeRefreshFragmentHave.isRefreshing = false
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as Main2Activity
    }

    companion object {
        fun newInstance(): ProbemMainFragment {
            val fragment = ProbemMainFragment()
            return fragment
        }
    }
}
