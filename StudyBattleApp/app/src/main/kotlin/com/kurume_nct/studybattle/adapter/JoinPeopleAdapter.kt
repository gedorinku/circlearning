package com.kurume_nct.studybattle.adapter

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.FragmentJoinperopleBinding

import com.kurume_nct.studybattle.model.JoinPeople
import com.kurume_nct.studybattle.model.User

class JoinPeopleAdapter(context: Context, private val list: MutableList<User>, val callback: (Int) -> Unit) :
        RecyclerView.Adapter<JoinPeopleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent
                .context)
                .inflate(R.layout.fragment_joinperople, parent, false)
        val holder = ViewHolder(view)
        view.tag = holder
        holder.itemView.setOnClickListener {
            callback(holder.adapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.PeopleUnit, list[position])
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: ViewDataBinding = DataBindingUtil.bind(mView)
    }

}
