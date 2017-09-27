package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kurume_nct.studybattle.model.Person_
import com.kurume_nct.studybattle.adapter.PictureListAdapter
import com.kurume_nct.studybattle.databinding.GroupListBinding


class GroupListFragment : Fragment() {

    private lateinit var binding: GroupListBinding
    lateinit var grouplist: MutableList<Person_>
    lateinit var listAdapter: PictureListAdapter
    private var activityId = 0


    fun newInstance(id: Int): GroupListFragment {
        val fragment = GroupListFragment()
        val args = Bundle()
        args.putInt("actId", id)
        fragment.arguments = args
        return fragment
    }

    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activityId = arguments.getInt("actId")
        binding = GroupListBinding.inflate(inflater, container, false)
        grouplist = mutableListOf(Person_())
        grouplist.add(Person_("pro"))

        listAdapter = PictureListAdapter(context, grouplist) {
            //item
        }
        binding.groupList2.adapter = listAdapter
        binding.groupList2.layoutManager = LinearLayoutManager(binding.groupList2.context)
        addListInstance()
        return binding.root
    }

    fun addListInstance(){
        when (activityId) {
            0 -> (0..10).forEach { grouplist.add(Person_(score = it.toString() + "点")) }
        }
        listAdapter.notifyItemRangeInserted(0,grouplist.size)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun changeList() {
        //activity側からの変更もちゃんと受け止める
    }

    override fun onDetach() {
        super.onDetach()
    }
}// Required empty public constructor
