package com.example.liquidacion.util


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.liquidacion.home.FrgFire
import com.example.liquidacion.home.FrgGiveUp

class MyPagerAdapter (frg : FragmentManager) : FragmentPagerAdapter(frg) {
    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> FrgGiveUp()
            else -> return FrgFire()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "Renuncia"
            else -> "Despido"
        }
    }
}