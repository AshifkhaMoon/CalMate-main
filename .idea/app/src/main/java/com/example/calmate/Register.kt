package com.example.calmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firestore.v1.FirestoreGrpc.FirestoreStub

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
        register_btn2 = findViewById(R.id.register_btn2)

        register_btn2.setOnClickListener {
            val name = personNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmEditText.text.toString()

            if(isValidRegistration(name, email, password, confirmPassword)){
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration successful!!", Toast.LENGTH_SHORT).show()
                            val userId = auth.currentUser?.uid
                            Intent(this, CalculateCalories::class.java).also { intent ->
                                userId?.let { id ->
                                    intent.putExtra("USER_ID", id)
                                }
                                startActivity(intent)
                                finish()
                            }
                        } else{
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Invalid registration details", Toast.LENGTH_SHORT).show()
            }

        }
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