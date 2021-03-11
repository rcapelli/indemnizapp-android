package com.example.liquidacion.home

import com.example.liquidacion.DataToGiveUp
import java.util.*

interface IHomeActions {
    fun openGiveUp()
    fun openFire()
    fun getData(salary : Int, start: Long, end: Long, daysPending : Int)
    fun getDataToFire(salary : Int, start: Long, end: Long, daysPending : Int, preaviso : Boolean?)
}