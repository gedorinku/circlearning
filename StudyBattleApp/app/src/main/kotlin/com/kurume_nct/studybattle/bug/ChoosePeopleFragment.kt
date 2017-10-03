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
import com.kurume_nct.studybattle.databinding.FragmentChoosePeoplelistBinding
import com.kurume_nct.studybattle.model.JoinPeople
import com.kurume_nct.studybattle.tools.ToolClass

/**
 * Created by hanah on 10/2/2017.
 */
class ChoosePeopleFragment(val callback: Callback): Fragment(){
    private lateinit var binding: FragmentChoosePeoplelistBinding
    private lateinit var list: MutableList<JoinPeople>
    private lateinit var listAdapter: JoinPeopleAdapter

    companion object {
        fun newInstance(callback: Callback): ChoosePeopleFragment {
            val fragment = ChoosePeopleFragment(callback)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentChoosePeoplelistBinding.inflate(inflater,container,false)
        onListReset()
        listAdapter = JoinPeopleAdapter(list,{
            position -> onDeletePeople(position)
            Log.d("Clickc",position.toString())
            callback.chooseChange(list[position])
        })
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        return binding.root
    }

    private fun onListReset(){
        val uri = ToolClass().convertUrlFromDrawableResId(context, R.drawable.glad)!!
        val joinPeople = JoinPeople()
        joinPeople.iconUri = uri
        list = mutableListOf(joinPeople)
        (0..5).forEach { list.add(joinPeople) }
    }

    fun onAddPeople(position: Int, peaple: JoinPeople){
        Log.d("onAddPeople",position.toString())
        list.add(peaple)
        listAdapter.notifyItemRangeInserted(list.size - 1, 1)
    }

    private fun onDeletePeople(position: Int){
        Log.d("onDeletePeople",position.toString())
        list.removeAt(position)
        listAdapter.notifyItemRangeRemoved(position, 1)
    }


    interface Callback{
        fun chooseChange(people: JoinPeople)
    }

}