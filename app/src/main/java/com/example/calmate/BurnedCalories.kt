package com.example.calmate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


class BurnedCalories : AppCompatActivity(), ActivitiesAdapter.OnActivityClickListener {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivitiesAdapter
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_burned_calories)

        backButton = findViewById(R.id.nav_back)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerViewActivities)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActivitiesAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.search_activity)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onActivityClicked(activityItem: ActivityItem) {
        saveActivityToDatabase(activityItem.caloriesBurned)
    }

    private fun saveActivityToDatabase(caloriesBurned: Int) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userActivitiesDocRef = db.collection("users").document(userId)

        userActivitiesDocRef.update(mapOf(
            "caloriesBurnedPerHour" to caloriesBurned
        ))
            .addOnSuccessListener {
                Toast.makeText(this, "Calories burned saved to database!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save calories burned: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun performSearch(query: String) {
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.api-ninjas.com/v1/caloriesburned?activity=$query")
            .header("X-Api-Key", "3W07pn6H9vwL0Yqd9q+wRg==weRLtb7MQCkrHiaX")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw IOException("Unexpected code $e")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        throw  IOException("Unexpected code $response")
                    } else {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            val items = JSONArray(responseData)
                            val activitiesList = mutableListOf<ActivityItem>()

                            for (i in 0 until items.length()) {
                                val item = items.getJSONObject(i)
                                val name = item.getString("name")
                                val totalCalories = item.getInt("total_calories")
                                activitiesList.add(ActivityItem(name, totalCalories))
                            }

                            runOnUiThread {
                                (recyclerView.adapter as? ActivitiesAdapter)?.updateData(activitiesList)
                            }
                        }
                    }
                }
            }
        })
    }
}