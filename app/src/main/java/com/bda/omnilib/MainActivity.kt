package com.bda.omnilib

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bda.omnilibrary.OmniShop
import com.bda.omnilibrary.ui.splashActivity.SplashActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btnStart)
        btn.setOnClickListener {
            OmniShop(
                "box2019",
                "UNOvlmlgC2dC38hc",
                "NCCm9zXmjYkWamrU",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2MTUyMzc5OTE5MjcsImp0aSI6IjJmNWZiNmRlLTBkNDUtNGE2NS04OWY4LWYwZGI0NTJiZDViNiIsInN1YiI6IjE2MDU4NzYifQ.bWJWTVP7dhtMKy-W0XaYXRHb3LFb6YgsucFcdOIOl2Q",
            ).init()
            val intent = Intent(this, SplashActivity::class.java)
            intent.putExtra("ISINVOICE", false)
            startActivity(intent)
        }
    }
}