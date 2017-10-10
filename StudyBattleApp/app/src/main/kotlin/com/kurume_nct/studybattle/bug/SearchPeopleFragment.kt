package com.kurume_nct.studybattle.bug

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.AndroidException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.adapter.JoinPeopleAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.FragmentChoosePeoplelistBinding
import com.kurume_nct.studybattle.model.JoinPeople
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.tools.ToolClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/2/2017.
 */
class SearchPeopleFragment(val callback: Callback) : Fragment() {
    private lateinit var binding: FragmentChoosePeoplelistBinding
    private lateinit var list: MutableList<JoinPeople>
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        unitPer = activity.application as UnitPersonal

        binding = FragmentChoosePeoplelistBinding.inflate(inflater, container, false)
        //onListReset()

        list = mutableListOf()

        listAdapter = JoinPeopleAdapter(list, { position ->
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
            list = mutableListOf()
            listAdapter.notifyItemRangeRemoved(0, size)
            Log.d("hoge","list")
            val client = ServerClient(unitPer.authenticationKey)
            client
                    .searchUsers(str)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val userList = it
                        Log.d("userの数は" + userList.size.toString(), str)
                        if(userList.isEmpty()){
                            searching = false
                        }
                        it.map {
                            //もしかしたらiconの処理は消すかもしれない（時間がかかるかもなので）
                            val joinPerson = JoinPeople()
                            joinPerson.name = it.displayName + "(" + it.userName + ")"
                            joinPerson.selected = false
                            joinPerson.id = it.id
                            list.add(joinPerson)
                            listAdapter.notifyItemRangeInserted(userList.size - 1, 1)

                            /*client
                                    .getImageById(userList[num].icon!!.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        joinPerson.iconUri = Uri.parse(it.url)
                                        if (userList.size - 1 == num) {
                                            searching = false
                                        }
                                    }*/
                        }
                    }, {
                        Log.d("User探しに失敗", "")
                    })
        }else{
            searching = false
        }
    }

    fun onAddPeople(position: Int, peaple: JoinPeople) {
        Log.d("onAddPeople", position.toString())
        list.add(peaple)
        listAdapter.notifyItemRangeInserted(list.size - 1, 1)
    }

    private fun onDeletePeople(position: Int) {
        Log.d("onDeletePeople", position.toString())
        list.removeAt(position)
        listAdapter.notifyItemRangeRemoved(position, 1)
    }


    interface Callback {
        fun chooseChange(people: JoinPeople)
    }

}