package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.tools.ResIDToUriClass

/**
 * Created by hanah on 10/1/2017.
 */
class CreateGroupViewModel(val context: Context, val callback: Callback): BaseObservable() {

    interface Callback{

    }
}