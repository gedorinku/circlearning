package com.kurume_nct.studybattle.listFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.adapter.ProblemListAdapter
import com.kurume_nct.studybattle.databinding.FragmentProblemListBinding
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.view.*

class MainListFragment : Fragment() {

    lateinit var binding: FragmentProblemListBinding
    var tabId: Int = 0
    lateinit var problemList: MutableList<Problem>
    //lateinit var problems : Problems
    lateinit var mContext: Context

    lateinit var listAdapter: ProblemListAdapter
    fun newInstance(id: Int): MainListFragment {
        val fragment = MainListFragment()
        val args = Bundle()
        args.putInt("id", id)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabId = arguments.getInt("id")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_list,container,false)
        binding = FragmentProblemListBinding.inflate(inflater, container, false)
        problemList = mutableListOf(Problem(0, "hoge", 0, "hoge"))
        listAdapter = ProblemListAdapter(context, problemList,
                { position: Int ->
                    val intent: Intent
                    when (tabId) {
                        resources.getInteger(R.integer.HAVE_PRO) -> {
                            if (position == (listAdapter.itemCount - 1)) {
                                //server
                                //changeList()
                                Toast.makeText(mContext, "新しい問題を取得中...📚", Toast.LENGTH_LONG).show()
                            } else {
                                intent = Intent(context, CameraModeActivity::class.java)
                                intent.putExtra("id", problemList[position].id)
                                startActivity(intent)
                            }
                        }
                        resources.getInteger(R.integer.ANSWER_YET) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("id", problemList[position].id)
                            intent.putExtra("fin", 1)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.ANSWER_FIN) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("id", problemList[position].id)
                            intent.putExtra("fin", 2)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_COLLECT_YET) -> {
                            intent = Intent(context, MadeCollectYetActivity::class.java)
                            intent.putExtra("id", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_JUDGE_YET) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("fin", 0)
                            intent.putExtra("id", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_FIN) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("fin", 2)
                            intent.putExtra("id", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.SUGGEST_YET) -> {
                            intent = Intent(context, PersonalAnswerActivity::class.java)
                            intent.putExtra("fin", false)
                            intent.putExtra("id", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.SUGGEST_FIN) -> {
                            intent = Intent(context, PersonalAnswerActivity::class.java)
                            intent.putExtra("fin", true)
                            intent.putExtra("id", problemList[position].id)
                            startActivity(intent)
                        }
                        else -> {
                            intent = Intent(context, ItemInfoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        changeList()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    fun changeList() {
        listAdapter.notifyItemRangeRemoved(0, problemList.size)
        problemList.clear()
        when (tabId) {
            resources.getInteger(R.integer.HAVE_PRO) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目", text = "時間"))
                }
                if (1 < 3) {
                    problemList.add(Problem(title = "　＋　新しい問題を追加で取得する"))
                }
            }
            resources.getInteger(R.integer.ANSWER_YET) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "全員が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.ANSWER_FIN) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.MADE_COLLECT_YET) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.MADE_JUDGE_YET) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.MADE_FIN) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.SUGGEST_YET) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
            resources.getInteger(R.integer.SUGGEST_FIN) -> {
                (1..3).forEach {
                    problemList.add(Problem(title = "自分が持っている" + it + "問目"))
                }
            }
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        listAdapter.notifyItemRangeInserted(0, problemList.size)
    }

    fun finish() {
        fragmentManager.beginTransaction().remove(this).commit()
    }

}
