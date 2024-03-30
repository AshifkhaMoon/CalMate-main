package com.example.calmate

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.PixelCopy.Request
import android.view.PixelCopy.Request.Builder
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import okhttp3.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.IOException

class AddCaloriesActivity : AppCompatActivity() {
    private lateinit var breakfastCaloriesTextView: TextView
    private lateinit var lunchCaloriesTextView: TextView
    private lateinit var dinnerCaloriesTextView: TextView
    private lateinit var snacksCaloriesTextView: TextView

    private lateinit var breakfastAddButton: ImageView
    private lateinit var lunchAddButton: ImageView
    private lateinit var dinnerAddButton: ImageView
    private lateinit var snacksAddButton: ImageView
    private lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_calories)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        breakfastCaloriesTextView = findViewById(R.id.breakFast_value)
        lunchCaloriesTextView = findViewById(R.id.Lunch_value)
        dinnerCaloriesTextView = findViewById(R.id.Dinner_value)
        snacksCaloriesTextView = findViewById(R.id.Snacks_value)

        breakfastAddButton = findViewById(R.id.breakfast_add_button)
        lunchAddButton = findViewById(R.id.Lunch_add_button)
        dinnerAddButton = findViewById(R.id.Dinner_add_button)
        snacksAddButton = findViewById(R.id.Snacks_add_button)
        backButton = findViewById(R.id.navigation_back)

        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        setupAddButtonListeners()
        setupFirestoreListener()
    }



    private fun setupAddButtonListeners() {
        breakfastAddButton.setOnClickListener {
            showFoodDialog("breakfast")
        }
        lunchAddButton.setOnClickListener{
            showFoodDialog("lunch")
        }
        dinnerAddButton.setOnClickListener {
            showFoodDialog("dinner")
        }
        snacksAddButton.setOnClickListener {
            showFoodDialog("snacks")
        }
    }

    private fun showFoodDialog(meal: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_food)

        val foodEditText: EditText = dialog.findViewById(R.id.food_name_edit_text)
        val searchButton: Button = dialog.findViewById(R.id.search_food_button)

        searchButton.setOnClickListener {
            val foodName = foodEditText.text.toString().trim()
            if (foodName.isNotEmpty()) {
                getCaloriesForFood(foodName, meal)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a food name", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun getCaloriesForFood(foodName: String, meal: String) {
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.calorieninjas.com/v1/nutrition?query=$foodName")
            .header("X-Api-Key", "3W07pn6H9vwL0Yqd9q+wRg==9adQWWP1a3rbTNzv")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    if (responseData != null) {
                        val json = JSONObject(responseData)
                        val items = json.getJSONArray("items")
                        if (items.length() > 0) {
                            val item = items.getJSONObject(0)
                            val caloriesPer100g = item.getDouble("calories")

                            val weightString = foodName.substringAfterLast(" ", "100")
                            val weight = weightString.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 100.0

                            val caloriesForWeight = (caloriesPer100g * weight) / 100.0

                            runOnUiThread {
                                updateCalorieCount(meal, caloriesForWeight.toInt())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateCalorieCount(meal: String, caloriesToAdd: Int) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = firestore.collection("users").document(userId)
        val mealToFieldMap = mapOf(
            "breakfast" to "breakfastCalories",
            "lunch" to "lunchCalories",
            "dinner" to "dinnerCalories",
            "snacks" to "snacksCalories"
        )

        val mealField = mealToFieldMap[meal]
        if (mealField.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid meal type.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentCalories = snapshot.getLong(mealField) ?: 0L
            val newCalories = currentCalories + caloriesToAdd
            transaction.update(userRef, mealField, newCalories)
        }.addOnSuccessListener {
            Toast.makeText(this, "$meal calories updated.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to update $meal calories: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun calculateAndDisplayMealCalories(bmr: Int, currentBreakfastCalories: Int, currentLunchCalories: Int, currentDinnerCalories: Int, currentSnacksCalories: Int) {
        val breakfastCaloriesGoal = (bmr * 0.25).toInt()
        val lunchCaloriesGoal = (bmr * 0.275).toInt()
        val dinnerCaloriesGoal = (bmr * 0.275).toInt()
        val snacksCaloriesGoal = (bmr * 0.2).toInt()

        breakfastCaloriesTextView.text = getString(R.string.calories_format, currentBreakfastCalories, breakfastCaloriesGoal)
        lunchCaloriesTextView.text = getString(R.string.calories_format, currentLunchCalories, lunchCaloriesGoal)
        dinnerCaloriesTextView.text = getString(R.string.calories_format, currentDinnerCalories, dinnerCaloriesGoal)
        snacksCaloriesTextView.text = getString(R.string.calories_format, currentSnacksCalories, snacksCaloriesGoal)

        val breakfastProgressBar: ProgressBar = findViewById(R.id.progressBarBreakfast)
        val lunchProgressBar: ProgressBar = findViewById(R.id.progressBarLunch)
        val dinnerProgressBar: ProgressBar = findViewById(R.id.progressBarDinner)
        val snacksProgressBar: ProgressBar = findViewById(R.id.progressBarSnacks)

        breakfastProgressBar.progress = calculateProgressBarPercentage(currentBreakfastCalories, breakfastCaloriesGoal)
        lunchProgressBar.progress = calculateProgressBarPercentage(currentLunchCalories, lunchCaloriesGoal)
        dinnerProgressBar.progress = calculateProgressBarPercentage(currentDinnerCalories, dinnerCaloriesGoal)
        snacksProgressBar.progress = calculateProgressBarPercentage(currentSnacksCalories, snacksCaloriesGoal)
    }

    private fun calculateProgressBarPercentage(current: Int, goal: Int): Int {
        return (current.toFloat() / goal * 100).toInt()
    }

    private fun setupFirestoreListener() {
        val userId = auth.currentUser?.uid ?: return


        firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Listen failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val bmr = snapshot.getLong("bmr")?.toInt() ?: 0
                    val currentBreakfastCalories = snapshot.getLong("breakfastCalories")?.toInt() ?: 0
                    val currentLunchCalories = snapshot.getLong("lunchCalories")?.toInt() ?: 0
                    val currentDinnerCalories = snapshot.getLong("dinnerCalories")?.toInt() ?: 0
                    val currentSnacksCalories = snapshot.getLong("snacksCalories")?.toInt() ?: 0

                    calculateAndDisplayMealCalories(bmr, currentBreakfastCalories, currentLunchCalories, currentDinnerCalories, currentSnacksCalories)
                } else {
                    Toast.makeText(this, "Current data: null", Toast.LENGTH_SHORT).show()
                }
            }
    }
}