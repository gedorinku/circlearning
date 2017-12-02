package com.kurume_nct.studybattle.model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.net.Uri
import com.kurume_nct.studybattle.BR

/**
* 🍣 Created by hanah on 9/22/2017.
*/
data class Direction(var num : String = "6")

data class RankingUser(
        var score: String = "",
        var displayName: String = "",
        var userName: String = "",
        var medal: Int = 0,
        var icon: Uri?
)