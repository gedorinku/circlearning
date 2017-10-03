package com.kurume_nct.studybattle.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by gedorinku on 2017/09/23.
 */
data class LoginResult(val authenticationKey: String = "")

data class User(val id: Int = 0, val userName: String = "", val displayName: String = "", val icon: Image? = null)

data class Group(val id: Int = 0, val name: String = "", val owner: User = User())

data class Item(var bomb: Int = 0, var card: Int = 0, var shield: Int = 0, var magicHand: Int = 0)

data class Image(
        val id: Int = 0,
        val url: String = "",
        val fileName: String = "")

//TODO これやめたい
data class IDResponse(val id: Int)

data class Problem(
        val id: Int = 0,
        val title: String = "",
        val ownerId: Int = 0,
        val text: String = "",
        val imageIds: List<Int> = emptyList(),
        val createdAt: String = "",
        @SerializedName("startsAt") val rawStartsAt: String = "",
        val durationMillis: Long = 0L,
        val point: Int = 0
) {

    val startsAtTime: DateTime by lazy { DateTime.parse(rawStartsAt) }
    val duration: Duration by lazy { Duration.millis(durationMillis) }
}

data class ProblemRequestResponse(
        val accepted: Boolean = false,
        val message: String = "",
        val problem: Problem = Problem()
)

enum class JudgingState {
    Solved,
    WaitingForJudge,
    Accepted,
    WrongAnswer
}

data class Solution(
        val id: Int = 0,
        val text: String = "sample",
        val authorId: Int = 0,
        val problemId: Int = 0,
        val imageCount: Int = 0,
        val imageIds: List<Int> = emptyList(),
        @SerializedName("judgingState") val rawJudgingState: String = ""
) {

    val judgingState by lazy { JudgingState.valueOf(rawJudgingState) }
    val judged by lazy {
        judgingState == JudgingState.Accepted || judgingState == JudgingState.WrongAnswer
    }
    val accepted by lazy {
        judgingState == JudgingState.Accepted
    }
}