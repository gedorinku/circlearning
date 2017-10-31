package com.kurume_nct.studybattle.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.adapter.JoinPeopleAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.FragmentChoosePeoplelistBinding
import com.kurume_nct.studybattle.listFragment.SelectMainPeopleFragment
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@SuppressLint("ValidFragment")
/**
 * Created by hanah on 10/2/2017.
 */
class SearchPeopleFragment(val callback: Callback, val context: SelectMainPeopleFragment) : Fragment() {
    private lateinit var binding: FragmentChoosePeoplelistBinding
    private val list = mutableListOf<User>()
    private lateinit var listAdapter: JoinPeopleAdapter
    private lateinit var unitPer: UnitPersonal
    private var searching = false

    companion object {
        fun newInstance(callback: Callback, context: SelectMainPeopleFragment): SearchPeopleFragment {
            val fragment = SearchPeopleFragment(callback, context)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        unitPer = activity.application as UnitPersonal

        binding = FragmentChoosePeoplelistBinding.inflate(inflater, container, false)
        //onListReset()

        listAdapter = JoinPeopleAdapter(activity, list, { position ->
            Log.d(list[position].displayName, position.toString())
            callback.chooseChange(list[position])
            onDeletePeople(position)
        })

        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context) as RecyclerView.LayoutManager?

        return binding.root
    }

    fun onListReset(str: String) {
        if (searching) return
        searching = true
        if (str.isNotBlank()) {
            val size = list.size
            listAdapter.notifyItemRangeRemoved(0, size)
            val client = ServerClient(unitPer.authenticationKey)
            client
                    .searchUsers(str)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        Log.d("userの数" + it.size.toString(), str)
                        val size = list.size
                        list.clear()
                        listAdapter.notifyItemRangeRemoved(0, size)
                        listFilter(it.toMutableList())
                        listAdapter.notifyItemRangeInserted(0, list.size)
                        searching = false
                    }
        } else {
            searching = false
        }
    }

    private lateinit var filterSet: MutableSet<User>
    private fun listFilter(newList: MutableList<User>){
        filterSet = context.getPeopleList().toMutableSet()
        filterSet.addAll(unitPer.nowGroup.members.toMutableSet())
        filterSet.add(unitPer.myInfomation)
        val size = filterSet.size
        filterSet.addAll(newList)
        list.addAll(filterSet.toMutableList().subList(size, filterSet.size))
    }

    private fun onDeletePeople(position: Int) {
        Log.d("onDeletePeople", position.toString())
        list.removeAt(position)
        listAdapter.notifyItemRangeRemoved(position, 1)
    }


    interface Callback {
        fun chooseChange(people: User)
    }

}