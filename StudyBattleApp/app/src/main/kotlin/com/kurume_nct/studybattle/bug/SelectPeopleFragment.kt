package com.kurume_nct.studybattle.bug

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.adapter.JoinPeopleAdapter
import com.kurume_nct.studybattle.databinding.FragmentJoinperopleListBinding
import com.kurume_nct.studybattle.model.JoinPeople
import com.kurume_nct.studybattle.model.User
import com.kurume_nct.studybattle.tools.ToolClass

class SelectPeopleFragment(val callback: Callback) : Fragment() {

    private lateinit var binding: FragmentJoinperopleListBinding
    private lateinit var list: MutableList<User>
    private lateinit var listAdapter: JoinPeopleAdapter

    companion object {
        fun newInstance(callback: Callback): SelectPeopleFragment {
            val fragment = SelectPeopleFragment(callback)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentJoinperopleListBinding.inflate(inflater, container, false)
        //onListReset()
        list = mutableListOf()
        listAdapter = JoinPeopleAdapter(activity, list, { position ->
            onDeletePeople(position)
            Log.d("Click", position.toString())
            //callback.selectChange(list[position - 1])
        })
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        return binding.root
    }

    /*private fun onListReset() {
        *//*val uri = ToolClass().convertUrlFromDrawableResId(context, R.drawable.icon_cut)!!
        val joinPeople = JoinPeople()*//*
        *//*joinPeople.iconUri = uri
        joinPeople.selected = true
        list = mutableListOf(joinPeople)
        (0..5).forEach { list.add(joinPeople) }*//*
    }*/

    fun onAddPeople(position: Int, people: User) {
        Log.d("onAddPeople", position.toString())
        list.add(0, people)
        listAdapter.notifyItemInserted(0)
    }

    private fun onDeletePeople(position: Int) {
        Log.d("onDeletePeople", position.toString())
        list.removeAt(position)
        listAdapter.notifyItemRemoved(position)
    }

    interface Callback {
        fun selectChange(people: User)
    }

}
