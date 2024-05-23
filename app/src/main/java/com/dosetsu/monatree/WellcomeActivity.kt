package com.dosetsu.monatree

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dosetsu.monatree.controller.LoginActivity
import com.dosetsu.monatree.controller.RegisterActivity
import com.dosetsu.monatree.pairing.HubungkanDenganOrangTuaActivity

class WellcomeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wellcome)
        setButtonClickListeners(this)
    }

    fun setButtonClickListeners(context: Context) {
        val buttonOrangTua = findViewById<Button>(R.id.btnOrangTua)
        val buttonAnak = findViewById<Button>(R.id.btnAnak)

        buttonOrangTua.setOnClickListener {
            val intent = Intent(context, LoginActivity ::class.java)
            startActivity(intent)
        }

        buttonAnak.setOnClickListener {
            val intent = Intent(context, HubungkanDenganOrangTuaActivity::class.java)
            startActivity(intent)
        }

    }
}
