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
import com.kurume_nct.studybattle.databinding.FragmentAnswerListBinding
import com.kurume_nct.studybattle.model.EveryAns
import com.kurume_nct.studybattle.view.AnswerActivity
import com.kurume_nct.studybattle.view.PersonalAnswerActivity
import com.kurume_nct.studybattle.view.ScoringActivity
import java.text.FieldPosition


class AnswerFragment : Fragment() {

    private var mColumnCount = 3
    private lateinit var mContext: Context
    private lateinit var listAdapter: AnswerRecyclerViewAdapter
    private var answerList: MutableList<EveryAns> = mutableListOf()
    private var fin: Int
    lateinit var binding: FragmentAnswerListBinding
    private val CHECK_ANS = 0
    private val YET_ANS = 1
    private val FIN_ANS = 2
    private var ansCount = 20

    init {
        fin = 0
    }

    fun newInstance(fin: Int): AnswerFragment {
        val fragment = AnswerFragment()
        val args = Bundle()
        args.putInt("fin", fin)//true -> all finished problem
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("i'm ", javaClass.name)
        Log.d("oshushi", "oshushi")
        fin = arguments.getInt("fin")
        binding = FragmentAnswerListBinding.inflate(inflater, container, false)
        (0 until ansCount).forEach {
            when (fin) {
                CHECK_ANS -> {
                    answerList.add(EveryAns(id = it, name = "hunachi" + "の解答"))
                }
                YET_ANS -> {
                    answerList.add(EveryAns(id = it, name = "hunachi" + "の解答", fin = true))
                }
                FIN_ANS -> {
                    answerList.add(EveryAns(id = it, name = "hunachi" + "の解答", fin = true))
                }
            }
        }
        listAdapter = AnswerRecyclerViewAdapter(context, answerList, { position: Int ->
            when (fin) {
                CHECK_ANS -> {
                    val intent = Intent(context, ScoringActivity()::class.java)
                    intent.putExtra("position", position)
                    startActivityForResult(intent, position)
                }
                YET_ANS -> {
                    val intent = Intent(context, PersonalAnswerActivity()::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("fin", false)
                    startActivity(intent)
                }
                FIN_ANS -> {
                    val intent = Intent(context, PersonalAnswerActivity()::class.java)
                    intent.putExtra("position", position)
                    intent.putExtra("fin", true)
                    startActivity(intent)
                }
            }
        })
        binding.answersList.adapter = listAdapter
        binding.answersList.layoutManager = GridLayoutManager(binding.answersList.context, mColumnCount)
        return binding.root
    }

    private fun changeImage(position: Int, cor: Boolean) {
        answerList[position].collect = cor
        answerList[position].fin = true
        listAdapter.notifyItemChanged(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (resultCode) {
            5 -> {
                changeImage(requestCode, data.getBooleanExtra("Result", false))
            }
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as AnswerActivity
    }

}
