package com.kurume_nct.studybattle.model

/**
 * Created by hunachi on 2018/01/10.
 */
enum class SolutionStatus(val statementId: Int) {
    YET_ANSWER(statementId = 1),
    YET_FIRST_JUDGE(statementId = 0),
    YET_FINAL_JUDGE(statementId = 2),
    ALL_FINISH(statementId = 3),
    NON_STATUS(statementId = -1);

    companion object {
        fun status(id: Int) =
                when (id) {
                    1 -> YET_ANSWER
                    0 -> YET_FIRST_JUDGE
                    2 -> YET_FINAL_JUDGE
                    3 -> ALL_FINISH
                    else -> NON_STATUS
                }
    }
}