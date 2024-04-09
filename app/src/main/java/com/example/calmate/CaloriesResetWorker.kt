package com.example.calmate

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CaloriesResetWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId") ?: let {
            Log.e("ResetCaloriesWorker", "User ID is null.")
            return Result.failure()
        }

        Log.d("ResetCaloriesWorker", "Working on resetting calories for user ID: $userId")

        return try {
            firestore.collection("users").document(userId)
                .update(
                    "breakfast", 0,
                    "lunch", 0,
                    "dinner", 0,
                    "snacks", 0,
                    "carbsTotal", 0,
                    "fatsTotal", 0,
                    "proteinsTotal", 0
                ).await()
            Log.d("ResetCaloriesWorker", "Calories reset successfully for user: $userId")
            Result.success()
        } catch (e: Exception) {
            Log.e("ResetCaloriesWorker", "Exception in resetting calories for user: $userId", e)
            Result.failure()
        }
    }
}