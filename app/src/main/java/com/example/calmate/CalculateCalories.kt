package com.example.calmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CalculateCalories : AppCompatActivity() {

    private lateinit var ageEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var doneButton: Button
    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var lightButton: Button
    private lateinit var activeButton: Button
    private lateinit var veryButton: Button

    private var isMale: Boolean = true
    private var activityFactor: Double = 1.375

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_calories)

        ageEditText = findViewById(R.id.age_EditText)
        heightEditText = findViewById(R.id.height_EditText)
        weightEditText = findViewById(R.id.weight_EditText)
        doneButton = findViewById(R.id.done_btn)
        maleButton = findViewById(R.id.male_btn)
        femaleButton = findViewById(R.id.female_btn)
        lightButton = findViewById(R.id.lightly_btn)
        activeButton = findViewById(R.id.active_btn)
        veryButton = findViewById(R.id.very_btn)


        doneButton.setOnClickListener{
            saveDataToFirestore()
        }
        maleButton.setOnClickListener {
            isMale = true
        }
        femaleButton.setOnClickListener {
            isMale = false
        }
        lightButton.setOnClickListener {
            activityFactor = 1.375
        }
        activeButton.setOnClickListener {
            activityFactor = 1.55
        }
        veryButton.setOnClickListener {
            activityFactor = 1.725
        }
    }

    private fun saveDataToFirestore() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid?: return

        val age = ageEditText.text.toString().toIntOrNull() ?: 0
        val height = heightEditText.text.toString().toIntOrNull() ?: 0
        val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0

        val bmr = calculateBMR(age, height, weight, isMale, activityFactor)

        val userProfile = hashMapOf(
            "age" to age,
            "height" to height,
            "weight" to weight,
            "bmr" to bmr,
            "activityLevel" to when(activityFactor) {
                1.375 -> "lightly active"
                1.55 -> "moderately active"
                1.725 -> "very active"
                else -> "unknown"
            }
        )

        firestore.collection("users").document(userId).set(userProfile, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully with BMR: $bmr", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SubscriptionPlan::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateBMR(
        age: Int,
        height: Int,
        weight: Double,
        isMale: Boolean,
        activityFactor: Double
    ): Int {
        val bmr = if (isMale) {
            (88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age))
        } else {
            (447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age))
        }
        return (bmr * this.activityFactor).toInt()
    }
}