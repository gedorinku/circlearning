package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UnitPersonal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/22/2017.
 */
class ScoingViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var radioClickedId = (R.id.radio_correct)

    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(view: ImageView, url: String) {
            if (url.isNotBlank()) {
                Glide.with(view).load(Uri.parse(url)).into(view)
            } else {
                Glide.with(view).load(R.drawable.no_image).into(view)
            }
        }
    }

    @Bindable
    var problemUrl = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemUrl)
        }

    @Bindable
    var answerUrl = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.answerUrl)
        }

    @Bindable
    var solver = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.solver)
        }

    @Bindable
    var writer = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.writer)
        }

    fun onClickRadioButton(radioGroup: RadioGroup, id: Int) {
        when (id) {
            R.id.radio_correct -> {

            }
            R.id.radio_mistake -> {

            }
        }
    }

    fun onClickProblemImage(view: View) {

    }

    fun onClickAnswerImage(view: View) {

    }

    fun onClickFinishButton(view: View) {

    }

    fun onCreate() {
        val unitPer = context.applicationContext as UnitPersonal
        val client = ServerClient(unitPer.authenticationKey)
        client
                .getSolution(callback.getSolutionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                },{

                })
    }

    interface Callback {

        fun getProblem(): Pair<String, String>

        fun getSolutionId(): Int

    }

}