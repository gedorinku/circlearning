package com.kurume_nct.studybattle.viewModel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.BindingAdapter
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kurume_nct.studybattle.BR
import com.kurume_nct.studybattle.R
import com.kurume_nct.studybattle.client.ServerClient
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.ImageViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by hanah on 10/22/2017.
 */
class ScoringViewModel(val context: Context, val callback: Callback) : BaseObservable() {

    private var solution = 0

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
    var problemName = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.problemName)
        }

    @Bindable
    var radioCorrect = false
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.radioCorrect)
        }

    @Bindable
    var radioMiss = false
        get
        set(value) {
            field = value
            notifyPropertyChanged(BR.radioMiss)
        }

    fun onClickProblemImage(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", problemUrl)
        context.startActivity(intent)
    }

    fun onClickAnswerImage(view: View) {
        val intent = Intent(context, ImageViewActivity::class.java)
        intent.putExtra("url", answerUrl)
        context.startActivity(intent)
    }

    fun onClickFinishButton(view: View) {
        if (!radioCorrect && !radioMiss) callback.onFinish()
        else sendData(radioCorrect)
    }

    fun setInit(): ServerClient {
        val unitPer = context.applicationContext as UnitPersonal
        return ServerClient(unitPer.authenticationKey)
    }

    fun onCreate() {
        setInit()
                .getSolution(callback.getSolution())
                .flatMap {
                    solution = it.id
                    if (it.judged) {
                        if (radioCorrect) radioCorrect = true
                        else radioMiss = true
                    }
                    if(it.imageCount > 0)
                    setInit()
                            .getImageById(it.imageIds[0])
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                answerUrl = it.url
                            }
                    setInit()
                            .getProblem(it.problemId)
                            .flatMap {
                                problemName = it.title
                                setInit()
                                        .getImageById(it.imageIds[0])
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                problemUrl = it.url
                            },{
                                it.printStackTrace()
                            })
                    setInit()
                            .getUser(it.authorId)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    solver = it.displayName + "(" + it.userName + ")"
                },{
                    it.printStackTrace()
                })
    }

    fun sendData(correct: Boolean) {
        setInit()
                .judgeSolution(solution, correct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    callback.onFinish()
                }
    }

    interface Callback {
        fun getProblem(): Pair<String, String> //title url
        fun getSolution(): Int
        fun onFinish()
    }

}