package com.kurume_nct.studybattle.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentJoinperopleBinding

import com.kurume_nct.studybattle.model.Group
import com.kurume_nct.studybattle.model.People
import java.util.concurrent.Callable

class JoinPeropleAdapter(private val list: MutableList<People>, val callback: (Int) -> Unit) : RecyclerView.Adapter<JoinPeropleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_joinperople, parent, false)
        val holder = ViewHolder(view)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.peopleUnit.run {
            iUri = list[position].uri
            name = list[position].name
        }
        if(!list[position].selected){
            holder.binding.peopleDeleteButton.visibility = View.GONE
        }
        if(!list[position].selected){
            //already selected
            holder.itemView.setOnClickListener {
                callback(position)
            }
        }else{
            //yet selected
            holder.binding.peopleDeleteButton.setOnClickListener {
                callback(position)
            }
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: FragmentJoinperopleBinding = DataBindingUtil.bind(mView)
    }
}
