package com.kurume_nct.studybattle.listFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.DatePicker
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.CreateProblemActivity

class DirectionFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val personal = UnitPersonal()
        val peopleCount = personal.groupCount
        val c = Calendar.getInstance()
        val cYear = c[Calendar.YEAR]
        val cMonth = c[Calendar.MONTH]
        val cDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
                activity
                , activity as CreateProblemActivity
                , cYear
                , cMonth
                , cDay
        )

        c.add(Calendar.DAY_OF_MONTH, 3)
        val geoF = GregorianCalendar()
        geoF.set(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])
        c.add(Calendar.DAY_OF_MONTH, peopleCount * 6)
        val geoE = GregorianCalendar()
        geoE.set(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH])
        val datePicker = datePickerDialog.datePicker
        datePicker.minDate = geoF.timeInMillis
        datePicker.maxDate = geoE.timeInMillis
        return datePickerDialog
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
