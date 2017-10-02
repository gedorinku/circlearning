package com.kurume_nct.studybattle.adapter

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentJoinperopleBinding

import com.kurume_nct.studybattle.model.JoinPeople

class JoinPeopleAdapter(private val list: MutableList<JoinPeople>, val callback: (Int) -> Unit) : RecyclerView.Adapter<JoinPeopleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_joinperople, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.PeopleUnit, list[position])
        if(!list[position].selected){
            holder.binding.peopleDeleteButton.visibility = View.GONE
        }
        if(!list[position].selected){
            //already selected
            holder.binding.joinPeopleLayout.setOnClickListener {
                callback(position)
            }
        }
        holder.binding.peopleDeleteButton.setOnClickListener {
            callback(position)
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: FragmentJoinperopleBinding = DataBindingUtil.bind(mView)
    }

    companion object {
        @BindingAdapter("loadIcon")
        @JvmStatic
        fun setIcon(view: ImageView, uri: Uri){
            Glide.with(view).load(uri).into(view)
        }
    }
}
