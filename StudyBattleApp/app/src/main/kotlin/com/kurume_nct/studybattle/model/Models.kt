package com.kurume_nct.studybattle.model

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by gedorinku on 2017/09/23.
 */
data class LoginResult(val authenticationKey: String = "")

data class User(val id: Int = 0, val userName: String = "", val displayName: String = "", val icon: Image? = null)

data class Group(val id: Int = 0, val name: String = "", val owner: User = User(), val members: List<User> = emptyList())

data class HunachiItem(var bomb: Int = 0, var card: Int = 0, var shield: Int = 0, var magicHand: Int = 0)

data class Image(
        val id: Int = 0,
        val url: String = "",
        val fileName: String = "")

//TODO これやめたい
data class IDResponse(val id: Int, val itemId: Int)

data class Problem(
        val id: Int = 0,
        val title: String = "",
        val ownerId: Int = 0,
        val text: String = "",
        val imageIds: List<Int> = emptyList(),
        val createdAt: String = "",
        @SerializedName("startsAt") val rawStartsAt: String = "",
        val durationMillis: Long = 0L,
        val point: Int = 0,
        val solutions: List<Solution> = emptyList(),
        val assignedUser: User? = null,
        @SerializedName("assignedAt") val rawAssignedAt: String = "",
        val durationPerUserMillis: Long = 0L,
        @SerializedName("state") val rawState: String = ""
) {

    val startsAtTime: DateTime by lazy { DateTime.parse(rawStartsAt) }
    val duration: Duration by lazy { Duration.millis(durationMillis) }
    val assignedAtTime: DateTime by lazy { DateTime.parse(rawAssignedAt) }
    val durationPerUser: Duration by lazy { Duration.millis(durationPerUserMillis) }
    val problemState: ProblemState by lazy { ProblemState.valueOf(rawState) }
}

enum class ProblemState {
    Opening,
    Judging,
    Judged
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

data class ProblemOpenResponse(
        val happened: String = ""
) {

    val openAction: ProblemOpenAction by lazy { ProblemOpenAction.valueOf(happened) }
}

enum class ProblemOpenAction {
    NONE,
    EXPLODED,
    DEFENDED
}

data class Ranking(val myWeekScore: Int = 0,
                   val myLastWeekScore: Int = 0,
                   val myTotalScore: Int = 0,
                   val ranking: List<Pair<User, Int>> = emptyList())