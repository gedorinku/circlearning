package com.kurume_nct.studybattle.listFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.adapter.AnswerRecyclerViewAdapter
import com.kurume_nct.studybattle.client.Server
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.databinding.FragmentAnswerListBinding
import com.kurume_nct.studybattle.model.ListSolution
import com.kurume_nct.studybattle.model.Solution
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.AnswerActivity
import com.kurume_nct.studybattle.view.FinalScoringActivity
import com.kurume_nct.studybattle.view.PersonalAnswerActivity
import com.kurume_nct.studybattle.view.ScoringActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.FieldPosition


class AnswerFragment : Fragment() {

    private var mColumnCount = 3
    private lateinit var mContext: Context
    private lateinit var listAdapter: AnswerRecyclerViewAdapter
    private val solutionList: MutableList<ListSolution> = mutableListOf()
    private var fin: Int = 0
    lateinit var binding: FragmentAnswerListBinding
    lateinit var unitPer: UnitPersonal
    lateinit var client: ServerClient
    private var problemId = 0
    private val CHECK_ANS = 0
    private val YET_ANS = 1
    private val YET_FINAL_ANS = 2
    private val FIN_ANS = 3
    private val CHECK_ANS_FALSE = 5

    fun newInstance(fin: Int, problemId: Int): AnswerFragment {
        val fragment = AnswerFragment()
        val args = Bundle()
        args.putInt("fin", fin)//true -> all finished problem
        args.putInt("problemId", problemId)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)

        client = ServerClient(unitPer.authenticationKey)
        unitPer = activity.application as UnitPersonal
        problemId = arguments.getInt("problemId")
        fin = arguments.getInt("fin")

        getProblemData()

        binding = FragmentAnswerListBinding.inflate(inflater, container, false)
        listAdapter = AnswerRecyclerViewAdapter(context, solutionList, { position: Int ->
            when (fin) {
                CHECK_ANS -> {
                    val intent = Intent(context, ScoringActivity::class.java)
                    intent.putExtra("solutionId", solutionList[position].solution.id)
                    intent.putExtra("position", position)
                    startActivityForResult(intent, position)
                }
                YET_ANS -> {
                    val intent = Intent(context, PersonalAnswerActivity::class.java)
                    intent.putExtra("solutionId", solutionList[position].solution.id)
                    intent.putExtra("fin", false)
                    startActivity(intent)
                }
                YET_FINAL_ANS -> {
                    val intent = Intent(context, FinalScoringActivity::class.java)
                    intent.putExtra("solutionId", solutionList[position].solution.id)
                    startActivityForResult(intent, position)
                }
                FIN_ANS -> {
                    val intent = Intent(context, PersonalAnswerActivity::class.java)
                    intent.putExtra("solutionId", solutionList[position].solution.id)
                    intent.putExtra("fin", true)
                    startActivity(intent)
                }
            }
        })
        binding.answersList.adapter = listAdapter
        binding.answersList.layoutManager = GridLayoutManager(binding.answersList.context, mColumnCount)
        return binding.root
    }

    private fun getProblemData() {
        client
                .getProblem(problemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it.solutions.forEach {
                        solutionList.add(ListSolution(it, ""))
                        client
                                .getUser(it.authorId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    solutionList[solutionList.size - 1].name = it.displayName
                                }
                    }
                }
    }

    private fun changeImage(position: Int, cor: Boolean) {
        //TODO judgeChange
        listAdapter.notifyItemChanged(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (resultCode) {
            CHECK_ANS_FALSE -> {
                changeImage(requestCode, data.getBooleanExtra("Result", false))
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as AnswerActivity
    }

}
