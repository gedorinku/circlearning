package com.kurume_nct.studybattle

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.databinding.FragmentSelectPeopleBinding
import com.kurume_nct.studybattle.listFragment.GroupListFragment
import com.kurume_nct.studybattle.model.JoinPeople

/**
 * Created by hanah on 10/2/2017.
 */
class SelectMainPeopleFragment : Fragment(), SelectPeopleFragment.Callback, ChoosePeopleFragment.Callback {

    private lateinit var binding: FragmentSelectPeopleBinding
    private lateinit var chooseFragment : ChoosePeopleFragment
    private lateinit var selectFragment : SelectPeopleFragment

    fun newInstance(callId: Int): SelectMainPeopleFragment{
        val fragment = SelectMainPeopleFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSelectPeopleBinding.inflate(inflater, container, false)
        binding.selectPeopleUnit = SelectMainPeopleViewModel(context)

        chooseFragment = ChoosePeopleFragment.newInstance(this)
        selectFragment = SelectPeopleFragment.newInstance(this)
        activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search_list,chooseFragment)
                .commit()
        activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_select_list, selectFragment)
                .commit()

        return binding.root
    }

    override fun selectChange(people: JoinPeople){
        Log.d(people.selected.toString(), people.name)
        people.selected = false
        chooseFragment.onAddPeople(0,people)
    }

    override fun chooseChange(people: JoinPeople){
        people.selected = true
        selectFragment.onAddPeople(0,people)
    }
}