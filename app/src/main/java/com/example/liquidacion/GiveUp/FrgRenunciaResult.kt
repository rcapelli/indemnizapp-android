package com.example.liquidacion.GiveUp

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.liquidacion.R
import com.itextpdf.text.Chunk
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import kotlinx.android.synthetic.main.fragment_give_up_result.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"
private const val PRIMERO_JULIO = 1625097600000L
private const val PRIMERO_ENERO = 1609459200000L
private const val FIN_ANIO = 1640908800000L
private const val YEAR = 360
private const val PRIMERO_JULIO_VEINTE = 1593561600000L
private const val PRIMERO_ENERO_VEINTE = 1577836800000L
private const val FIN_ANIO_VEINTE = 1609372800000L

class FrgRenunciaResult : Fragment() {
    private var sueldo: Int? = null
    private var start: Long? = null
    private var end: Long? = null
    private var daysPending: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sueldo = it.getInt(ARG_PARAM1)
            start = it.getLong(ARG_PARAM2)
            end = it.getLong(ARG_PARAM3)
            daysPending = it.getInt(ARG_PARAM4)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_give_up_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.title = "Indemnización aproximada"
        val email = "ricki.capelli@gmail.com"
        val subject = "Asesoramiento Indemnizapp"
        val message = "Mi nombre es: \n y quiero comunicarme por asesoramiento sobre una indemnización.\nMuchas gracias"
        btn_contactus.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent, "Select email"))
        }
        btn_download.setOnClickListener {
            try{
                if(ContextCompat.checkSelfPermission(this.context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PERMISSION_DENIED){
                    requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                }else{
                    savePdf()
                }
            }catch (e : IOException){
                e.printStackTrace()
            }
        }

        val startDate = convertLongToTime(start!!)
        val endDate = convertLongToTime(end!!)
        val diff : Long = end!!.times(1) - start!!.times(1)
        val days = ((diff / 1000) / 60 / 60 / 24).toInt()
        val years = (days / 360).toInt()
        val month = (days / 30)
        val textDias = getString(R.string.diasTotales) +" " + days + " " + getString(R.string.dias)
        val textStart = getString(R.string.fechaDesde) + " " + startDate
        val textEnd = getString(R.string.fechaHasta) + " " + endDate
        val dailySalary = sueldo!!/30.toDouble()
        tv_desde.text = textStart
        tv_hasta.text = textEnd
        val sueldoMes = String.format("%.0f",sueldoMes(dailySalary,endDate)).toDouble().roundToInt()
        val sacAcum = String.format("%.0f",if(end!!<FIN_ANIO_VEINTE){ getSACPasado(sueldo!!, end!!,start!!)} else getSAC(sueldo!!, end!!, start!!)).toDouble().roundToInt()
        val vacaciones = String.format("%.0f",if(end!!<FIN_ANIO_VEINTE){ getVNGPasadas(sueldo!!, days, end!!) } else getVNG(sueldo!!, days, end!!)).toDouble()
        val sacVacaciones : Double = String.format("%.0f",vacaciones/12).toDouble()

        val sueldo_txt = getString(R.string.sueldoMes) + " $" + sueldoMes.toString()
        tv_remunerativo_1.text = sueldo_txt
        val sac_txt = getString(R.string.SAC) + " $" + sacAcum.toString()
        tv_remunerativo_2.text = sac_txt

        val jubilacion : Int = String.format("%.0f",(sueldoMes+sacAcum)*0.11).toDouble().roundToInt()
        val ley : Double = String.format("%.0f",(sueldoMes+sacAcum)*0.03).toDouble()
        val obraSocial : Double = String.format("%.0f",(sueldoMes+sacAcum)*0.03).toDouble()
        val jub_txt = getString(R.string.jubilacion) + " $" + jubilacion.toInt()
        tv_descuentos_1.text = jub_txt
        val ley_txt = getString(R.string.ley) + " $" + ley.toInt()
        tv_descuentos_2.text = ley_txt
        val os_txt = getString(R.string.obrasocial) + " $" + obraSocial.toInt()
        tv_descuentos_3.text = os_txt

        val vacaciones_txt = getString(R.string.vacaciones_nog) + " $" + vacaciones.toInt()
        val sacvacaciones_txt = getString(R.string.sac_vacaciones) + " $" + sacVacaciones.toInt()
        tv_no_remunerativo_1.text = vacaciones_txt
        tv_no_remunerativo_2.text = sacvacaciones_txt

        val sumas = sueldoMes+sacAcum+vacaciones+sacVacaciones
        val restas = jubilacion+ley+obraSocial
        val montoTotal = String.format("%.0f",sumas).toDouble() - String.format("%.0f",restas).toDouble()
        val total = getString(R.string.monto_total) + " $" + montoTotal.toInt()
        tv_monto_total.text = total

    }
    fun convertLongToTime(time: Long) : String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    fun sueldoMes(daily : Double, day : String) : Double {
        var dayOfMonth = day.split("/").first().toDouble()
        if(dayOfMonth.toInt() == 31){
            dayOfMonth -=1
        }
        val monthSalary =  (dayOfMonth * daily)
        return monthSalary
    }

    fun getSAC(salary : Int, day: Long, startDate: Long) : Double {
        var sac : Double
        if(day < PRIMERO_JULIO){
            val daysL : Long = day.times(1) - PRIMERO_ENERO.times(1)
            var days = (daysL / 1000) / 60 / 60 / 24
            if(startDate<PRIMERO_ENERO){
                sac = (salary.toDouble()/YEAR)*days
            }else{
                val daysL : Long = day.times(1) - startDate.times(1)
                val days = (daysL / 1000) / 60 / 60 / 24
                sac = (salary.toDouble()/YEAR)*days
            }
            return sac
        }else {
            val daysL : Long = day.times(1) - PRIMERO_JULIO.times(1)
            val days = (daysL / 1000) / 60 / 60 / 24
            if(startDate< PRIMERO_JULIO){
                sac = (salary.toDouble()/YEAR)*days
            }else{
                val daysL : Long = day.times(1) - startDate.times(1)
                val days = (daysL / 1000) / 60 / 60 / 24
                sac = (salary.toDouble()/YEAR)*days
            }
            return sac
        }
    }

    fun getSACPasado(salary : Int, day: Long, startDate: Long) : Double {
        var sac = 0.0
        if(day < PRIMERO_JULIO_VEINTE){
            val daysL : Long = day.times(1) - PRIMERO_ENERO_VEINTE.times(1)
            var days = (daysL / 1000) / 60 / 60 / 24
            if(startDate< PRIMERO_ENERO_VEINTE){
                sac = (salary.toDouble()/YEAR)*days
            }else{
                val daysL : Long = day.times(1) - startDate.times(1)
                val days = (daysL / 1000) / 60 / 60 / 24
                sac = (salary.toDouble()/YEAR)*days
            }
            return sac
        }else {
            val daysL : Long = day.times(1) - PRIMERO_JULIO_VEINTE.times(1)
            val days = (daysL / 1000) / 60 / 60 / 24
            if(startDate< PRIMERO_JULIO_VEINTE){
                sac = (salary.toDouble()/YEAR)*days
            }else{
                val daysL : Long = day.times(1) - startDate.times(1)
                val days = (daysL / 1000) / 60 / 60 / 24
                sac = (salary.toDouble()/YEAR)*days
            }
            return sac
        }
    }


    fun getVNG(salary : Int, totalDays: Int, end : Long) : Double {
        var holidays : Double

        //1. valor del dia de vacaciones = bruto/25
        val amountHPD : Double = salary/25.toDouble()

        var days : Long
        // dias transcurridos desde el principio de año hasta que me voy
        if(start!!.times(1) > PRIMERO_ENERO.times(1)){
            val todayToStart = end.times(1) - start!!.times(1)
            days = (todayToStart /1000) / 60 / 60 / 24
        } else {
            val diff : Long = end.times(1) - PRIMERO_ENERO.times(1)
            days = (diff / 1000) / 60 / 60 / 24
        }

        //diferencia entre el 31/12 y la fecha que me voy
        val decemberToNow : Long = FIN_ANIO.times(1) - end.times(1)
        val diff2 = (decemberToNow / 1000) / 60 / 60 / 24



        // cantidad de dias que corresponden segun antiguedad
        val finalDays = totalDays+diff2.toDouble()
        if(finalDays in 181.00..1799.00){
            holidays = (((14.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays in 1800.00..3599.00){
            holidays = (((21.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays in 3600.00..7199.00){
            holidays = (((28.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays >= 7200){
            holidays = (((35.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else{
            holidays = ((((totalDays/30).toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }
        return holidays
    }

    fun getVNGPasadas(salary : Int, totalDays: Int, end : Long) : Double {
        var holidays : Double

        //1. valor del dia de vacaciones = bruto/25
        val amountHPD : Double = salary/25.toDouble()

        var days : Long
        // dias transcurridos desde el principio de año hasta que me voy
        if(start!!.times(1) > PRIMERO_ENERO_VEINTE.times(1)){
            val todayToStart = end.times(1) - start!!.times(1)
            days = (todayToStart /1000) / 60 / 60 / 24
        } else {
            val diff : Long = end.times(1) - PRIMERO_ENERO_VEINTE.times(1)
            days = (diff / 1000) / 60 / 60 / 24
        }

        //diferencia entre el 31/12 y la fecha que me voy
        val decemberToNow : Long = FIN_ANIO_VEINTE.times(1) - end.times(1)
        val diff2 = (decemberToNow / 1000) / 60 / 60 / 24



        // cantidad de dias que corresponden segun antiguedad
        val finalDays = totalDays+diff2.toDouble()
        if(finalDays in 181.00..1799.00){
            holidays = (((14.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays in 1800.00..3599.00){
            holidays = (((21.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays in 3600.00..7199.00){
            holidays = (((28.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else if(finalDays >= 7200){
            holidays = (((35.toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }else{
            holidays = ((((totalDays/30).toDouble() / YEAR) * days) * amountHPD) + daysPending * amountHPD
        }
        return holidays
    }

    private fun savePdf() {
        //create object of Document class
        val mDoc = com.itextpdf.text.Document()
        //pdf file name
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"
        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))

            //open the document for writing
            mDoc.open()

            val font = Font(Font.FontFamily.HELVETICA, 18F , Font.NORMAL)
            val fontTitle = Font(Font.FontFamily.HELVETICA, 20F, Font.BOLD)


            //get text from EditText i.e. textEt
            val mTitle1 = "Remunerativo"
            val mTitle2 = "No remunerativo"
            val mTitle3 = "Descuentos"

            val mText1 = tv_remunerativo_1.text.toString()
            val mText2 = tv_remunerativo_2.text.toString()
            val mText3 = tv_descuentos_1.text.toString()
            val mText4 = tv_descuentos_2.text.toString()
            val mText5 = tv_descuentos_3.text.toString()
            val mText6 = tv_no_remunerativo_1.text.toString()
            val mText7 = tv_no_remunerativo_2.text.toString()
            val mText9 = tv_no_remunerativo_3.text.toString()
            val mText10 = tv_no_remunerativo_4.text.toString()
            val mText11 = tv_no_remunerativo_5.text.toString()

            val mText8 = tv_monto_total.text.toString()

            //add author of the document (metadata)
            mDoc.addAuthor("Ricardo Capelli")

            /*val table = PdfPTable(3)
            val left = Paragraph("TEST")
            left.indentationRight = 20F
            val leftCell = PdfPCell()
            leftCell.addElement(left)
            table.addCell(leftCell)
            mDoc.add(table)*/
            //table.addCell(getCell("Remunerativo", PdfPCell.ALIGN_LEFT))
            //table.addCell(getCell("No Remunerativo",PdfPCell.ALIGN_CENTER))
            //table.addCell(getCell("Descuentos",PdfPCell.ALIGN_RIGHT))
            val title1 = Paragraph(mTitle1, fontTitle)
            val title2 = Paragraph(mTitle2, fontTitle)
            val title3 = Paragraph(mTitle3, fontTitle)
            val para1 = Paragraph(mText1,font)
            val para2 = Paragraph(mText2,font)
            val para3 = Paragraph(mText3,font)
            val para4 = Paragraph(mText4,font)
            val para5 = Paragraph(mText5,font)
            val para6 = Paragraph(mText6,font)
            val para7 = Paragraph(mText7,font)

            val para9 = Paragraph(mText9,font)
            val para10 = Paragraph(mText10,font)
            val para11 = Paragraph(mText11,font)
            val para8 = Paragraph(mText8,fontTitle)
            para8.spacingBefore = 78f
            para6.spacingBefore = 50f
            para3.spacingBefore = 50f

            title1.alignment = PdfPCell.ALIGN_LEFT
            title2.alignment = PdfPCell.ALIGN_CENTER
            title3.alignment = PdfPCell.ALIGN_RIGHT
            para3.alignment = PdfPCell.ALIGN_RIGHT
            para4.alignment = PdfPCell.ALIGN_RIGHT
            para5.alignment = PdfPCell.ALIGN_RIGHT
            para6.alignment = PdfPCell.ALIGN_CENTER
            para7.alignment = PdfPCell.ALIGN_CENTER


            para8.alignment = PdfPCell.ALIGN_CENTER


            //add paragraph to the document
            mDoc.add(title1)
            mDoc.add(title2)
            mDoc.add(title3)

            mDoc.add(para1)
            mDoc.add(para2)

            mDoc.add(para6)
            mDoc.add(para7)

            mDoc.add(para3)
            mDoc.add(para4)
            mDoc.add(para5)


            mDoc.add(para8)

            mDoc.addTitle("Indemnización estimada")

            //close document
            mDoc.close()

            //show file saved message with file name and path
            Toast.makeText(this.context, "$mFileName.pdf\n fue guardado en: \n$mFilePath", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            //if anything goes wrong causing exception, get and show exception message
            Toast.makeText(this.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(sueldo: Int, start: Long, end: Long, daysPending: Int) =
            FrgRenunciaResult().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, sueldo)
                    putLong(ARG_PARAM2, start)
                    putLong(ARG_PARAM3, end)
                    putInt(ARG_PARAM4, daysPending)
                }
            }
    }
}