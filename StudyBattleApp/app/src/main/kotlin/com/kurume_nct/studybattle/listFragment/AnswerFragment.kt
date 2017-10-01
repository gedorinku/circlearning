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
    private var fin: Boolean
    lateinit var binding: FragmentAnswerListBinding

    init {
        fin = false
    }

    fun newInstance(fin: Boolean): AnswerFragment {
        val fragment = AnswerFragment()
        val args = Bundle()
        args.putBoolean("fin", fin)//true -> all finished problem
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("oshushi", "oshushi")
        fin = arguments.getBoolean("fin")
        binding = FragmentAnswerListBinding.inflate(inflater, container, false)
        (0..20).forEach {
            answerList.add(EveryAns(id = it, collect = (it % 2 == 0)))
        }
        listAdapter = AnswerRecyclerViewAdapter(context, answerList,{
            position: Int ->
            val intent = Intent(context, ScoringActivity()::class.java)
            intent.putExtra("position", position)
            startActivityForResult(intent, position)
        })
        binding.answersList.adapter = listAdapter
        binding.answersList.layoutManager = GridLayoutManager(binding.answersList.context, mColumnCount)
        return binding.root
    }

    private fun changeImage(position: Int, cor : Boolean){
        answerList[position].collect = cor
        listAdapter.notifyItemChanged(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            5 ->{
                changeImage(requestCode, data.getBooleanExtra("Result", false))
            }
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context as AnswerActivity
    }

}
