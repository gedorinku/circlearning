package com.kurume_nct.studybattle.ListFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.`object`.Problem
import com.kurume_nct.studybattle.adapter.ProblemListAdapter
import com.kurume_nct.studybattle.databinding.FragmentProblemListBinding
import com.kurume_nct.studybattle.view.LoginActivity
import com.kurume_nct.studybattle.view.RegistrationActivity

class MainListFragment : Fragment() {

    lateinit var binding : FragmentProblemListBinding
    var tabId : Int = 0
    lateinit var problemList : MutableList<Problem>
    //lateinit var problems : Problems
    lateinit var mContext : Context

    lateinit var listAdapter : ProblemListAdapter
    fun newInstance(id: Int) : MainListFragment{
        val fragment = MainListFragment()
        val args = Bundle()
        args.putInt("id",id)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabId = arguments.getInt("id")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_list,container,false)
        binding = FragmentProblemListBinding.inflate(inflater, container, false)
        problemList = mutableListOf(Problem(0, "hoge", 0, "hoge"))
        listAdapter = ProblemListAdapter(context, problemList) {
            var intent = Intent(context, LoginActivity::class.java)
            when (tabId) {
                0, 1 -> intent = Intent(context, RegistrationActivity::class.java)
                2, 3 -> intent = Intent(context, LoginActivity::class.java)
            }
            startActivity(intent)
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context) as RecyclerView.LayoutManager?
        //setList()
        changeList(tabId)
        return binding.root
    }



    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    fun changeList(id : Int){
        listAdapter.notifyItemRangeRemoved(0,problemList.size)
        problemList.clear()
        when (tabId) {
            resources.getInteger(R.integer.HAVE_PRO) -> {
                (0..10).forEach {
                    problemList.add(Problem("自分が持っている" + it + "問目", "の問題"))
                }
            }
            resources.getInteger(R.integer.ANSWER_YET) -> {
                (0..10).forEach {
                    problemList.add(Problem("全員が持っている" + it + "問目", "の問題"))
                }
            }
            resources.getInteger(R.integer.ANSWER_FIN) -> {
                (0..10).forEach {
                    problemList.add(Problem("自分が持っている" + it + "問目", "の問題だよ"))
                }
            }
            resources.getInteger(R.integer.MADE_COLLECT_YET) -> {
                (0..10).forEach {
                    problemList.add(Problem("自分が持っている" + it + "問目", "の問題だよ"))
                }
            }
            resources.getInteger(R.integer.MADE_JUDGE_YET) -> {
                (0..10).forEach {
                    problemList.add(Problem("全員が持っている" + it + "問目", "の問題"))
                }
            }
            resources.getInteger(R.integer.MADE_FIN) -> {
                (0..10).forEach {
                    problemList.add(Problem("全員が持っている" + it + "問目", "の問題"))
                }
            }
            resources.getInteger(R.integer.SUGGEST_YET) -> {
                (0..10).forEach {
                    problemList.add(Problem("全員が持っている" + it + "問目", "の問題"))
                }
            }
            resources.getInteger(R.integer.SUGGEST_FIN) -> {
                (0..10).forEach {
                    problemList.add(Problem("全員が持っている" + it + "問目", "の問題"))
                }
            }
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        listAdapter.notifyItemRangeInserted(0, problemList.size)
        // Log.d(problemList.size.toString(), tabId.toString())
    }

    fun finish(){
        fragmentManager.beginTransaction().remove(this).commit()
    }

}
