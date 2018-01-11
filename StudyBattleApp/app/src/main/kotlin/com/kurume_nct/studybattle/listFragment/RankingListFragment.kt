package com.kurume_nct.studybattle.listFragment

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.R

import com.kurume_nct.studybattle.adapter.PictureListAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.GroupListBinding
import com.kurume_nct.studybattle.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RankingListFragment : Fragment() {

    private lateinit var binding: GroupListBinding
    val grouplist = mutableListOf<RankingUser>()
    lateinit var listAdapter: PictureListAdapter
    private lateinit var usersObject: UsersObject
    private var activityId = 0

    fun newInstance(id: Int): RankingListFragment {
        val fragment = RankingListFragment()
        val args = Bundle()
        args.putInt("actId", id)
        fragment.arguments = args
        return fragment
    }

    lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        usersObject =  context.applicationContext as UsersObject
        binding = GroupListBinding.inflate(inflater, container, false)
        listAdapter = PictureListAdapter(context, grouplist)
        binding.groupList2.adapter = listAdapter
        binding.groupList2.layoutManager = LinearLayoutManager(binding.groupList2.context)
        addListInstance()
        return binding.root
    }

    fun addListInstance() {
        grouplist.clear()
        val client = ServerClient(usersObject.authenticationKey)
        client
                .getRanking(usersObject.nowGroup.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    it.ranking.forEachIndexed { index, pair ->
                        grouplist.add(RankingUser(
                                score = pair.second.toString(),
                                displayName = pair.first.displayName,
                                userName = pair.first.userName,
                                medal = index,
                                icon = if(pair.first.icon?.url != null) Uri.parse(pair.first.icon?.url) else null
                        ))
                        Log.d(pair.first.userName, pair.first.displayName + "ランキング")
                    }
                    listAdapter.notifyItemRangeInserted(0, grouplist.size)
                },{
                    it.printStackTrace()
                   grouplist.add(RankingUser(
                           displayName = "まだランキングが存在しないよ",
                           icon = null
                   ))
                    listAdapter.notifyItemRangeInserted(0, grouplist.size)
                })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


}
