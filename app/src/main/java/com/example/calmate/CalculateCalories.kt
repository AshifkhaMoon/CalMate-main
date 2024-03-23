package com.example.calmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class CalculateCalories : AppCompatActivity() {

    private lateinit var ageEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_calories)

        ageEditText = findViewById(R.id.age_EditText)
        heightEditText = findViewById(R.id.height_EditText)
        weightEditText = findViewById(R.id.weight_EditText)


    }
}