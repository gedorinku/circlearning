package com.kurume_nct.studybattle.bug

import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentSelectPeopleBinding
import com.kurume_nct.studybattle.model.JoinPeople
import android.text.Editable
import com.kurume_nct.studybattle.R.id.editText



/**
 * Created by hanah on 10/2/2017.
 */
class SelectMainPeopleFragment : Fragment(), SelectPeopleFragment.Callback, ChoosePeopleFragment.Callback {

    private lateinit var binding: FragmentSelectPeopleBinding
    private lateinit var chooseFragment : ChoosePeopleFragment
    private lateinit var selectFragment : SelectPeopleFragment

    fun newInstance(callId: Int): SelectMainPeopleFragment {
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
        /*activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_select_list, selectFragment)
                .commit()*/

        /*binding.editText7.setOnEditorActionListener { v, actionId, event ->
            var action = false
            if(event != null && actionId == EditorInfo.IME_ACTION_DONE){
                chooseFragment.onListReset(binding.selectPeopleUnit.searchText)
                action = true
            }
            action
        }*/
        binding.editText7.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //chooseFragment.onListReset(binding.selectPeopleUnit.searchText)
            }
            override fun afterTextChanged(s: Editable?) {
                chooseFragment.onListReset(binding.selectPeopleUnit.searchText)
            }
        })

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