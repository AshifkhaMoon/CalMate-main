package com.example.calmate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Lunch : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var lowCaloriesButton: ImageView
    private lateinit var midCaloriesButton: ImageView
    private lateinit var highCaloriesButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        backButton = findViewById(R.id.nav_back)
        lowCaloriesButton = findViewById(R.id.lowcaloreis_btn)
        midCaloriesButton = findViewById(R.id.midcalories_btn)
        highCaloriesButton = findViewById(R.id.highcalories_btn)

        lowCaloriesButton.setOnClickListener {
            getRandomRecipeLink("low")
        }
        midCaloriesButton.setOnClickListener {
            getRandomRecipeLink("mid")
        }
        highCaloriesButton.setOnClickListener {
            getRandomRecipeLink("high")
        }

        backButton.setOnClickListener {
            finish()
        }
    }
    private fun getRandomRecipeLink(caloriesCategory: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("food").document("lunch")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val categoryMap = document.getData()
                    val linksMap = categoryMap?.get(caloriesCategory) as? Map<String, String>
                    val linksList = linksMap?.values?.toList()
                    val randomLink = linksList?.random()
                    randomLink?.let { link ->
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        startActivity(browserIntent)
                    }
                } else {
                    Toast.makeText(this, "No recipes found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get recipe link: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}