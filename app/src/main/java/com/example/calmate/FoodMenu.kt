package com.example.calmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class FoodMenu : AppCompatActivity() {

    private lateinit var backButton: ImageView

    private lateinit var breakfastButton: ImageView

    private lateinit var lunchButton: ImageView

    private lateinit var dinnerButton: ImageView

    private lateinit var snacksButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu)

        backButton = findViewById(R.id.nav_back)
        breakfastButton = findViewById(R.id.breakfast_btn)
        lunchButton = findViewById(R.id.lunch_btn)
        dinnerButton = findViewById(R.id.dinner_btn)
        snacksButton = findViewById(R.id.snacks_btn)

        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        breakfastButton.setOnClickListener {
            val intent = Intent(this, Breakfast::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        lunchButton.setOnClickListener {
            val intent = Intent(this, Lunch::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        dinnerButton.setOnClickListener {
            val intent = Intent(this, Dinner::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        snacksButton.setOnClickListener {
            val intent = Intent(this, Snacks::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

    }
}