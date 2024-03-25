package com.example.calmate

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.login_btn)
        loginButton.setOnClickListener {
            val Intent = Intent(this,Login::class.java)
            startActivity(Intent)
        }

        val registerButton = findViewById<Button>(R.id.register_btn)
        registerButton.setOnClickListener {
            val Intent = Intent(this,Register::class.java)
            startActivity(Intent)
        }
    }
}