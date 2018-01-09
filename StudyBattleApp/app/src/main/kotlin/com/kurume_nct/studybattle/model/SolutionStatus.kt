package com.kurume_nct.studybattle.model

/**
 * Created by hunachi on 2018/01/10.
 */
enum class SolutionStatus {
    YET_ANSWER {
        override val statementId = 1
    },
    YET_FIRST_JUDGE {
        override val statementId = 0
    },
    YET_FINAL_JUDGE {
        override val statementId = 2
    },
    ALL_FINISH {
        override val statementId = 3
    },
    NON_STATUS{
        override val statementId = -1
    };

    open val statementId = -1

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