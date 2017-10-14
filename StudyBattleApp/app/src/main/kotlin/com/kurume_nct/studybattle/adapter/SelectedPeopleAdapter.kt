package com.kurume_nct.studybattle.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.databinding.ItemSelectedPeopleBinding
import com.kurume_nct.studybattle.model.User

/**
 * Created by hanah on 10/13/2017.
 */
class SelectedPeopleAdapter(val context: Context, var list: MutableList<User>, val callback: (Int) -> Unit)
    : RecyclerView.Adapter<SelectedPeopleAdapter.ViewHolder>() {

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.PeopleUnit, list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_selected_people, parent, false)
        val holder = ViewHolder(view)
        holder.binding.getOutListButton.setOnClickListener {
            callback(holder.adapterPosition)
        }
        return holder
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ItemSelectedPeopleBinding = DataBindingUtil.bind(view)
    }
}