package com.kurume_nct.studybattle.listFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.adapter.ProblemListAdapter
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.FragmentProblemListBinding
import com.kurume_nct.studybattle.model.Problem
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.mergeAll
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers

class MainListFragment(val callback: Callback) : Fragment() {

    lateinit var binding: FragmentProblemListBinding
    var tabId: Int = 0
    private val problemList = mutableListOf<Problem>()
    lateinit var mContext: Context
    private lateinit var client: ServerClient
    private lateinit var unitPersonal: UnitPersonal

    lateinit var listAdapter: ProblemListAdapter

    companion object {
        fun newInstance(id: Int, callback: Callback): MainListFragment {
            val fragment = MainListFragment(callback)
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }


    interface Callback {
        fun onStopSwipeRefresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabId = arguments.getInt("id")
        unitPersonal = activity.application as UnitPersonal
        onRefreshList()
    }

    fun onRefreshList() {
        client = ServerClient(unitPersonal.authenticationKey)
        val groupId = unitPersonal.nowGroup.id

        when (tabId) {
            resources.getInteger(R.integer.HAVE_PROBLEM) ->
                client.getAssignedProblems(groupId)
                        .firstOrError()

            resources.getInteger(R.integer.ANSWER_YET) ->
                //TODO
                Single.just(emptyList())

            resources.getInteger(R.integer.ANSWER_FIN) ->
                //TODO
                Single.just(emptyList())

            resources.getInteger(R.integer.MADE_COLLECT_YET) ->
                client.getMyCollectingProblems(groupId)
                        .firstOrError()

            resources.getInteger(R.integer.MADE_JUDGE_YET) ->
                client.getMyJudgingProblems(groupId)
                        .firstOrError()

            resources.getInteger(R.integer.MADE_FIN) ->
                client.getMyJudgedProblems(groupId)
                        .firstOrError()

            resources.getInteger(R.integer.SUGGEST_YET) ->
                client.getUnjudgedMySolutions(groupId)
                        .flatMap { it.toObservable() }
                        .map { client.getProblem(it.problemId) }
                        .mergeAll()
                        .toList()

            resources.getInteger(R.integer.SUGGEST_FIN) ->
                client.getJudgedMySolutions(groupId)
                        .flatMap { it.toObservable() }
                        .map { client.getProblem(it.problemId) }
                        .mergeAll()
                        .toList()

            else -> throw IllegalArgumentException(tabId.toString()) as Throwable
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    if (it.isNotEmpty()) {
                        val listSize = problemList.size
                        problemList.clear()
                        listAdapter.notifyItemRangeRemoved(0, listSize)
                        problemList.addAll(0, it)
                        if (tabId == 0) {
                            problemList.add(Problem(title = "　＋　新しい問題を追加で取得する"))
                        }
                        listAdapter.notifyItemRangeInserted(0, it.size)
                        Log.d(it.size.toString(), "isNotEmpty" + unitPersonal.nowGroup.id.toString())
                        callback.onStopSwipeRefresh()
                    } else {
                        callback.onStopSwipeRefresh()
                        Log.d(it.toString(), "空")
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problem_list,container,false)
        binding = FragmentProblemListBinding.inflate(inflater, container, false)
        listAdapter = ProblemListAdapter(context, problemList,
                { position: Int ->
                    val intent: Intent
                    when (tabId) {
                        resources.getInteger(R.integer.HAVE_PROBLEM) -> {
                            if (position == (listAdapter.itemCount - 1)) {
                                Toast.makeText(mContext, "新しい問題を取得中...📚", Toast.LENGTH_LONG).show()
                                assignedProblem()
                            } else {
                                intent = Intent(context, CameraModeActivity::class.java)
                                intent.putExtra("problemId", problemList[position].id)
                                startActivity(intent)
                            }
                        }
                        resources.getInteger(R.integer.ANSWER_YET) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("problemId", problemList[position].id)
                            intent.putExtra("fin", 1)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.ANSWER_FIN) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("problemId", problemList[position].id)
                            intent.putExtra("fin", 2)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_COLLECT_YET) -> {
                            intent = Intent(context, MadeCollectYetActivity::class.java)
                            intent.putExtra("problemId", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_JUDGE_YET) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("fin", 0)
                            intent.putExtra("problemId", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.MADE_FIN) -> {
                            intent = Intent(context, AnswerActivity::class.java)
                            intent.putExtra("fin", 2)
                            intent.putExtra("problemId", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.SUGGEST_YET) -> {
                            intent = Intent(context, PersonalAnswerActivity::class.java)
                            intent.putExtra("fin", false)
                            intent.putExtra("problemId", problemList[position].id)
                            startActivity(intent)
                        }
                        resources.getInteger(R.integer.SUGGEST_FIN) -> {
                            intent = Intent(context, PersonalAnswerActivity::class.java)
                            intent.putExtra("fin", true)
                            intent.putExtra("problemId", problemList[position].id)
                            startActivity(intent)
                        }
                        else -> {
                            intent = Intent(context, ItemInfoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })
        if (tabId == 0) {
            problemList.add(Problem(title = "　＋　新しい問題を追加で取得する"))
        }
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(binding.list.context)
        return binding.root
    }

    fun assignedProblem() {
        val client = ServerClient(unitPersonal.authenticationKey)
        client
                .requestNewProblem(unitPersonal.nowGroup)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    problemList.add(0, it.problem)
                    listAdapter.notifyItemRangeInserted(0, 1)
                }, {
                    Toast.makeText(activity, "もらうことのできる\n新しい問題がありませんでした", Toast.LENGTH_SHORT).show()
                    Log.d("error", "requestNewProblem")
                })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context!!
    }

    fun finish() {
        fragmentManager.beginTransaction().remove(this).commit()
    }

}
