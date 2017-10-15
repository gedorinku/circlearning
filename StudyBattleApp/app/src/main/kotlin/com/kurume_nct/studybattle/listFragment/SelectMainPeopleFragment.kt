package com.kurume_nct.studybattle.listFragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentSelectPeopleBinding
import android.text.Editable
import com.kurume_nct.studybattle.view.SelectPeopleFragment
import com.kurume_nct.studybattle.model.User
import com.kurume_nct.studybattle.view.SearchPeopleFragment


/**
 * Created by hanah on 10/2/2017.
 */
class SelectMainPeopleFragment : Fragment(), SelectPeopleFragment.Callback, SearchPeopleFragment.Callback {

    private lateinit var binding: FragmentSelectPeopleBinding
    private lateinit var searchFragment: SearchPeopleFragment
    private lateinit var selectFragment: SelectPeopleFragment

    fun newInstance(callId: Int): SelectMainPeopleFragment {
        val fragment = SelectMainPeopleFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSelectPeopleBinding.inflate(inflater, container, false)
        binding.selectPeopleUnit = SelectMainPeopleViewModel(context)

        searchFragment = SearchPeopleFragment.newInstance(this)
        selectFragment = SelectPeopleFragment.newInstance(this)

        activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_search_list, searchFragment)
                .commit()

        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_select_list, selectFragment)
                .commit()

        binding.editText7.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                searchFragment.onListReset(binding.selectPeopleUnit.searchText)
            }
        })

        return binding.root
    }

    fun getPeopleList(): MutableList<User> = selectFragment.getPeopleList()

    override fun selectChange(people: User) {
        selectFragment.onAddPeople(0, people)
    }

    override fun chooseChange(people: User) {
        selectFragment.onAddPeople(0, people)
    }
}