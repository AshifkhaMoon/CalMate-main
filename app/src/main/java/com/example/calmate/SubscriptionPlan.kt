package com.example.calmate

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SubscriptionPlan : AppCompatActivity() {

    private lateinit var checkBoxBasicPlan: CheckBox
    private lateinit var checkBoxPremiumPlan: CheckBox
    private lateinit var buttonDone: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_plan)

        checkBoxBasicPlan = findViewById(R.id.adFreeCheckbox)
        checkBoxPremiumPlan = findViewById(R.id.adFreeCheckbox1)
        buttonDone = findViewById(R.id.arrow_done_button)

        buttonDone.setOnClickListener {
            val isPremium = checkBoxPremiumPlan.isChecked
            updateMembershipStatus(isPremium)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateMembershipStatus(isPremium: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userRef.update("memberStatus", isPremium)
            .addOnSuccessListener {
                val message = if (isPremium) "Upgraded to Premium Plan." else "Switched to Basic Plan."
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update membership status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}