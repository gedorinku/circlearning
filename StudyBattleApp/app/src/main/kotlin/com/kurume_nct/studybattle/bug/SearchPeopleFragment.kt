package com.kurume_nct.studybattle.bug

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
import com.kurume_nct.studybattle.model.JoinPeople
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.model.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

/**
 * Created by hanah on 10/2/2017.
 */
class SearchPeopleFragment(val callback: Callback) : Fragment() {
    private lateinit var binding: FragmentChoosePeoplelistBinding
    private val list = mutableListOf<User>()
    private lateinit var listAdapter: JoinPeopleAdapter
    private lateinit var unitPer: UnitPersonal
    private var searching = false

    companion object {
        fun newInstance(callback: Callback): SearchPeopleFragment {
            val fragment = SearchPeopleFragment(callback)
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
            onDeletePeople(position)
            Log.d("Clickc", position.toString())
            callback.chooseChange(list[position])
        })

        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)

        return binding.root
    }

    fun onListReset(str: String) {
        if (searching) return
        Log.d("edit", "入力")
        searching = true
        if (str.isNotBlank()) {
            val size = list.size
            listAdapter.notifyItemRangeRemoved(0, size)
            Log.d("hoge", "list")
            val client = ServerClient(unitPer.authenticationKey)
            client
                    .searchUsers(str)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        Log.d("userの数は" + it.size.toString(), str)
                        val size = list.size
                        list.clear()
                        listAdapter.notifyItemRangeRemoved(0, size)
                        list.addAll(it)
                        listAdapter.notifyItemRangeInserted(0, list.size)
                        searching = false
                    }
        } else {
            searching = false
        }
    }

    /*fun parsePeople(user: User): JoinPeople {
        val joinPerson = JoinPeople()
        joinPerson.id = user.id
        joinPerson.selected = false
        joinPerson.name = user.displayName + "(" + user.userName + ")"
        return joinPerson
    }*/

    /*fun onAddPeople(user: MutableList<User>) {
        val people = mutableListOf<JoinPeople>()
        people.addAll(user.map { parsePeople(it) })

    }*/

    private fun onDeletePeople(position: Int) {
        Log.d("onDeletePeople", position.toString())
        list.removeAt(position)
        listAdapter.notifyItemRangeRemoved(position, 1)
    }


    interface Callback {
        fun chooseChange(people: User)
    }

}