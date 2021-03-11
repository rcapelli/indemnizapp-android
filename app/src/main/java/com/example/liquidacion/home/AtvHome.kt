package com.example.liquidacion.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import com.example.liquidacion.DataToFire
import com.example.liquidacion.DataToGiveUp
import com.example.liquidacion.GiveUp.AtvGiveUp
import com.example.liquidacion.R
import com.example.liquidacion.util.MyPagerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class AtvHome : AppCompatActivity(), IHomeActions {
    var salario : Int? = null
    var start : Long? = null
    var end : Long? = null
    var data : DataToGiveUp? = null
    var dataFire : DataToFire? = null
    var daysPending : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar.setTitle("Calculá tu indemnización!")
        setSupportActionBar(toolbar)
        val fragmentAdapter =
            MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)

    }

    override fun getData(salary : Int, startDate: Long, endDate: Long, daysPending: Int) {
        salario = salary
        start = startDate
        end = endDate
        data = DataToGiveUp(salario!!,start!!,end!!,daysPending)
      //DataToGiveUp(salary,date1,date2)
    }

    override fun getDataToFire(salary : Int, startDate: Long, endDate: Long, daysPending: Int, preaviso : Boolean?) {
        salario = salary
        start = startDate
        end = endDate
        dataFire = DataToFire(salario!!,start!!,end!!,daysPending,preaviso)
        //DataToGiveUp(salary,date1,date2)
    }

    override fun openGiveUp(){
        if(data != null){
            startActivity(AtvGiveUp.newIntent(this,data!!))
        }
        if(dataFire != null){
            startActivity(AtvGiveUp.newIntent(this,dataFire!!))
        }

    }

    override fun openFire() {
        TODO("Not yet implemented")
    }

}
