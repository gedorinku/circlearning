package com.kurume_nct.studybattle.listFragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import com.kurume_nct.studybattle.model.UnitPersonal
import com.kurume_nct.studybattle.view.CreateProblemActivity
import java.util.*

class DirectionFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {


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

    fun onGetInitDate() : MutableList<Int>{
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 6)
        return mutableListOf(c[Calendar.YEAR], c[Calendar.MONTH] + 1,c[Calendar.DAY_OF_MONTH])
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
