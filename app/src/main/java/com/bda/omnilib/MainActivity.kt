package com.bda.omnilib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.bda.omnilibrary.dialog.assitant.AssistantDialog
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.splashActivity.SplashActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btnStart)
        btn.setOnClickListener {
            val intent = Intent(this, SplashActivity::class.java)
            intent.putExtra("ISINVOICE", false)
            startActivity(intent)
        }
    }
}