package com.kurume_nct.studybattle.listFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.DatePicker
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.CreateProblemActivity
import java.util.*

class DurationFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("i'm ", javaClass.name)
        val personal = UnitPersonal()
        val peopleCount = personal.myGroupCount
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
        c.add(Calendar.DAY_OF_MONTH, 6)
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

    fun onGetInitDate(): MutableList<Int> {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 6)
        return mutableListOf(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
    }

    fun onGetToday(): MutableList<Int> {
        val c = Calendar.getInstance()
        return mutableListOf(c[Calendar.YEAR], c[Calendar.MONTH] + 1, c[Calendar.DAY_OF_MONTH])
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    }

    fun newInstance(): DurationFragment {
        val fragment = DurationFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

}
