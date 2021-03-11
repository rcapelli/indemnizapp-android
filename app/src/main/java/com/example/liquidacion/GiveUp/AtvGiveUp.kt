package com.example.liquidacion.GiveUp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import com.example.liquidacion.DataToFire
import com.example.liquidacion.DataToGiveUp
import com.example.liquidacion.R
import com.example.liquidacion.home.AtvHome
import java.io.Serializable

class AtvGiveUp : AppCompatActivity(){


    companion object {
        @JvmStatic
        fun newIntent(ctx: Context, datos : DataToGiveUp): Intent{
            val intent= Intent(ctx, AtvGiveUp::class.java)
            intent.putExtra("KEY_PERSONAL_SALARY", datos.grossSalary)
            intent.putExtra("KEY_PERSONAL_BEGIN", datos.start)
            intent.putExtra("KEY_PERSONAL_END", datos.end)
            intent.putExtra("KEY_DAYS_PENDING", datos.daysPending)
            return intent
        }
        fun newIntent(ctx: Context, datos : DataToFire): Intent{
            val intent= Intent(ctx, AtvGiveUp::class.java)
            intent.putExtra("KEY_PERSONAL_SALARY", datos.grossSalary)
            intent.putExtra("KEY_PERSONAL_BEGIN", datos.start)
            intent.putExtra("KEY_PERSONAL_END", datos.end)
            intent.putExtra("KEY_DAYS_PENDING", datos.daysPending)
            intent.putExtra("KEY_PREAVISO", datos.preaviso)
            return intent
        }
    }

    private fun getSueldo() = intent.getSerializableExtra("KEY_PERSONAL_SALARY")
    private fun getStart() = intent.getSerializableExtra("KEY_PERSONAL_BEGIN")
    private fun getEnd() = intent.getSerializableExtra("KEY_PERSONAL_END")
    private fun getDiasPendientes() = intent.getSerializableExtra("KEY_DAYS_PENDING")
    private fun getPreaviso() = intent.getSerializableExtra("KEY_PREAVISO")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_give_up)
        val sueldo = getSueldo()
        val comienzo = getStart()
        val fin = getEnd()
        val dias = getDiasPendientes()
        val preaviso = getPreaviso() ?: ""
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        if(preaviso == ""){
            transaction.replace(R.id.main_frgcontainer, FrgRenunciaResult.newInstance(sueldo as Int,comienzo as Long, fin as Long, dias as Int)).commit()
        }else{
            transaction.replace(R.id.main_frgcontainer, FrgGiveUpResult.newInstance(sueldo as Int,comienzo as Long, fin as Long, dias as Int, preaviso as Boolean)).commit()
        }

    }
}