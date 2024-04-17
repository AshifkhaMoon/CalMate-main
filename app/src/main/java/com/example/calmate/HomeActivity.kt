package com.example.calmate

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var carbsTextView: TextView
    private lateinit var proteinTextView: TextView
    private lateinit var fatsTextView: TextView
    private lateinit var eatenTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        calculateTotalEatenCalories()
        fetchAndDisplayUserMacros()


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val greetingTextView: TextView = findViewById(R.id.greeting_tv)

        carbsTextView = findViewById(R.id.carbsValueTV)
        proteinTextView = findViewById(R.id.proteinValueTV)
        fatsTextView = findViewById(R.id.fatValueTV)
        eatenTextView = findViewById(R.id.eaten_tv)

        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name") ?: "User"
                        val bmr = document.getLong("bmr")?.toInt() ?: 0

                        greetingTextView.text = getString(R.string.greeting, name)

                    } else {
                        Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchAndDisplayUserMacros() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Toast.makeText(this, "Listen failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val bmr = snapshot.getLong("bmr")?.toInt() ?: 0
                val breakfastCalories = snapshot.getLong("breakfast") ?: 0L
                val lunchCalories = snapshot.getLong("lunch") ?: 0L
                val dinnerCalories = snapshot.getLong("dinner") ?: 0L
                val snacksCalories = snapshot.getLong("snacks") ?: 0L

                val totalEatenCalories = breakfastCalories + lunchCalories + dinnerCalories + snacksCalories
                val remainingCalories = bmr - totalEatenCalories.toInt()

                val fatsTotal = snapshot.getLong("fatsTotal")?.toInt() ?: 0
                val carbsTotal = snapshot.getLong("carbsTotal")?.toInt() ?: 0
                val proteinsTotal = snapshot.getLong("proteinsTotal")?.toInt() ?: 0

                val carbsAvg = (bmr * 0.55).toInt()
                val proteinAvg = (bmr * 0.225).toInt()
                val fatsAvg = (bmr * 0.275).toInt()

                carbsTextView.text = getString(R.string.macronutrient_format, carbsTotal, carbsAvg)
                proteinTextView.text = getString(R.string.macronutrient_format, proteinsTotal, proteinAvg)
                fatsTextView.text = getString(R.string.macronutrient_format, fatsTotal, fatsAvg)

                updateEatenUI(totalEatenCalories)
                updateRemainingCaloriesUI(remainingCalories)
            } else {
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRemainingCaloriesUI(remainingCalories: Int) {
        val remainingCaloriesTextView: TextView = findViewById(R.id.circleProgress_tv)

        remainingCaloriesTextView.text = remainingCalories.toString()
    }

    private fun calculateTotalEatenCalories() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val breakfastCalories = document.getLong("breakfast") ?: 0L
                    val lunchCalories = document.getLong("lunch") ?: 0L
                    val dinnerCalories = document.getLong("dinner") ?: 0L
                    val snacksCalories = document.getLong("snacks") ?: 0L

                    val totalEaten = breakfastCalories + lunchCalories + dinnerCalories + snacksCalories
                    updateEatenUI(totalEaten)
                } else {
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateEatenUI(totalEaten: Long) {
        eatenTextView.text = "Eaten \n $totalEaten"
    }
    fun onFoodMenuButtonClick(view: View) {
        val intent = Intent(this, FoodMenu::class.java)
        startActivity(intent)
    }

    fun onCustomerButtonClick(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
    fun onAddButtonClick(view: View) {
        val intent = Intent(this, AddCaloriesActivity::class.java)
        startActivity(intent)
    }

    fun onExceriseButtonClick(view: View) {
        val intent = Intent(this, BurnedCalories::class.java)
        startActivity(intent)
    }
}
