package com.kurume_nct.studybattle.listFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import com.kurume_nct.studybattle.view.CreateProblemActivity

class DirectionFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        return DatePickerDialog(activity, activity as CreateProblemActivity, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    fun newInstance(): DirectionFragment {
        val fragment = DirectionFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

}
