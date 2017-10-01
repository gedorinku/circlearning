package com.kurume_nct.studybattle

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.adapter.JoinPeropleAdapter
import com.kurume_nct.studybattle.databinding.FragmentJoinperopleListBinding
import com.kurume_nct.studybattle.model.People
import com.kurume_nct.studybattle.tools.ResIDToUriClass

class SelectPeopleFragment(val callback: Callback) : Fragment() {

    private lateinit var binding: FragmentJoinperopleListBinding
    private lateinit var list: MutableList<People>
    private lateinit var listAdapter: JoinPeropleAdapter

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

        binding = FragmentJoinperopleListBinding.inflate(inflater,container,false)
        onListReset()
        listAdapter = JoinPeropleAdapter(list,{
            position -> onDeletePeople(position)
            callback.selectChenge(list[position])
        })
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        return binding.root
    }

    private fun onListReset(){
        val uri = ResIDToUriClass().convertUrlFromDrawableResId(context, R.drawable.icon_cut)!!
        list = mutableListOf(People(uri, selected = true))
        (0..5).forEach { list.add(People(uri, selected = true)) }
    }

    fun onAddPeople(position: Int, people: People){
        list.add(people)
        listAdapter.notifyItemRangeInserted(list.size - 1, 1)
    }

    private fun onDeletePeople(position: Int){
        list.removeAt(position)
        listAdapter.notifyItemRemoved(position)
    }

    interface Callback{
        fun selectChenge(people: People)
    }

}
