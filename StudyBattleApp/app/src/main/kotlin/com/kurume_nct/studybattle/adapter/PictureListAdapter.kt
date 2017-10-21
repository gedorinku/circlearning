package com.kurume_nct.studybattle.adapter

import android.content.Context
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
import com.kurume_nct.studybattle.databinding.GroupObjectBinding
import com.kurume_nct.studybattle.model.Person_
import com.kurume_nct.studybattle.model.Ranking
import com.kurume_nct.studybattle.model.RankingUser
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import retrofit2.http.Url
import javax.microedition.khronos.opengles.GL

/**
 * Created by hanah on 9/22/2017.
 */
class PictureListAdapter(val context: Context, val list: MutableList<RankingUser>)
    : RecyclerView.Adapter<PictureListAdapter.GroupListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.group_object, parent, false)
        val holder = GroupListHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: GroupListHolder, position: Int) {
        holder.binding.setVariable(BR.Ranking, list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class GroupListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: GroupObjectBinding = DataBindingUtil.bind(view)
    }

    companion object {
        @BindingAdapter("loadMedal")
        @JvmStatic
        fun setMedal(view: ImageView, rank: Int) {
            var resouce: Int? = null
            when (rank) {
                0 -> resouce = R.drawable.medal1
                1 -> resouce = R.drawable.medal2
                2 -> resouce = R.drawable.medal3
            }
            Glide.with(view).load(resouce).into(view)
        }

        @BindingAdapter("loadIconRanking")
        @JvmStatic
        fun setIcon(view: ImageView, uri: Uri) {
            Glide.with(view).load(uri).into(view)
        }
    }


}