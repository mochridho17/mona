package com.dosetsu.monatree

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreenActivity : AppCompatActivity() {

    private val splashDelay: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

            Handler().postDelayed({
                //Intent ke mainActivity
                val intent = Intent(this, WellcomeActivity::class.java)
                startActivity(intent)
                finish()
            }, splashDelay)
    }
}