package com.example.liquidacion.home

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.liquidacion.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_fire.*
import kotlinx.android.synthetic.main.fragment_fire.btn_calc_giveUp
import kotlinx.android.synthetic.main.fragment_fire.et_dias_vacaciones
import kotlinx.android.synthetic.main.fragment_fire.et_fecha_egreso
import kotlinx.android.synthetic.main.fragment_fire.et_fecha_ingreso
import kotlinx.android.synthetic.main.fragment_fire.et_sueldo_bruto
import kotlinx.android.synthetic.main.fragment_fire.tv_date_error
import kotlinx.android.synthetic.main.fragment_give_up.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FrgFire : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    var daysPending = 0
    var preaviso : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var mCallback: IHomeActions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as IHomeActions
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var start : Date
        var end : Date

        et_fecha_ingreso.setOnFocusChangeListener { _, _ ->
            if(et_fecha_ingreso.hasFocus())
                openCalendar(et_fecha_ingreso)
        }
        et_fecha_egreso.setOnFocusChangeListener { _, _ ->
            if(et_fecha_egreso.hasFocus())
                openCalendar(et_fecha_egreso)
        }


        rb_negative.setOnCheckedChangeListener { _, _ ->
            if(rb_negative.isChecked){
                preaviso = false
            }
        }
        rb_positive.setOnCheckedChangeListener { _, _ ->
            if(rb_positive.isChecked){
                preaviso = true
            }
        }


        btn_calc_giveUp.setOnClickListener {
            try {
                start = SimpleDateFormat("dd/MM/yyyy").parse(et_fecha_ingreso.text.toString())
                end = SimpleDateFormat("dd/MM/yyyy").parse(et_fecha_egreso.text.toString())
                if(start.time < end.time){
                    openGiveUp()
                }else
                    Toast.makeText(this.context, "Verific?? las fechas ingresadas!", Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                Toast.makeText(this.context, "Completar los campos!", Toast.LENGTH_SHORT).show()
            }

        }

    }


    fun openGiveUp(){
        if(!et_fecha_ingreso.text.isNullOrEmpty() && !et_fecha_ingreso.text.isNullOrEmpty() && !et_sueldo_bruto.text.isNullOrEmpty() && (rb_negative.isChecked || rb_positive.isChecked)){
            val salary : Int = et_sueldo_bruto.text.toString().toInt()
            val entryDate : String = et_fecha_ingreso.text.toString()
            val departureDate : String = et_fecha_egreso.text.toString()

            val start : Date = SimpleDateFormat("dd/MM/yyyy").parse(entryDate)
            val end : Date = SimpleDateFormat("dd/MM/yyyy").parse(departureDate)
            val diff : Long = end.time - start.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            if(et_dias_vacaciones.text.toString() != ""){
                daysPending = et_dias_vacaciones.text.toString().toInt()
            }
            getDataToFire(salary, start.time, end.time, daysPending, preaviso)
            et_fecha_ingreso.clearFocus()
            et_fecha_egreso.clearFocus()
            mCallback.openGiveUp()
        }else{
            tv_date_error.text = "Ten??s que completar todos los campos"
            tv_date_error.visibility = View.VISIBLE
            et_fecha_ingreso.clearFocus()
            et_fecha_egreso.clearFocus()
        }

    }

    fun getDataToFire(salary : Int, start: Long, end : Long, daysPending : Int, preaviso : Boolean?){
        mCallback.getDataToFire(salary,start, end, daysPending, preaviso)
    }

    fun openCalendar(it: TextInputEditText){

        val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay->
            val mmMonth = mMonth+1
            val date = ""+ mDay+ "/" + mmMonth +"/"+mYear
            it.setText(date)
        },year,month,day)
        /*
        if(et_fecha_egreso.hasFocus()){
            dpd.datePicker.minDate =
        }*/
        dpd.show()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FrgFire().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}