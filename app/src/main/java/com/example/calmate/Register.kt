package com.example.calmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var personNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmEditText: EditText
    private lateinit var register_btn2: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        personNameEditText = findViewById(R.id.personName)
        emailEditText = findViewById(R.id.email_address)
        passwordEditText = findViewById(R.id.password)
        confirmEditText = findViewById(R.id.Edittext_confrimpassword)
        register_btn2 = findViewById(R.id.update_data_btn)

        register_btn2.setOnClickListener {
            val name = personNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmEditText.text.toString()

            if (isValidRegistration(name, email, password, confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            saveUserToDatabase(name, email)
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Invalid registration details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(name: String, email: String) {
        val userId = auth.currentUser?.uid ?: return

        val userData = hashMapOf(
            "name" to name,
            "email" to email,
            "age" to 0,
            "weight" to 0.0,
            "height" to 0,
            "memberStatus" to false
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved to database", Toast.LENGTH_SHORT).show()
                navigateUpCalculateCalories(userId)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateUpCalculateCalories(userId: String) {
        val intent = Intent(this, CalculateCalories::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
        finish()
    }

    private fun isValidRegistration(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun isValidEmail(email: String): Boolean{
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
}