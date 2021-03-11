package com.example.liquidacion

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.liquidacion.home.AtvHome


class AtvSplash : AppCompatActivity() {

    companion object {
        @JvmStatic fun newIntent(ctx: Context): Intent =
            Intent(ctx, AtvSplash::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val background = object :Thread() {
            override fun run(){
                try{
                    sleep(3500)
                    val intent = Intent(baseContext, AtvHome::class.java)
                    startActivity(intent)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}