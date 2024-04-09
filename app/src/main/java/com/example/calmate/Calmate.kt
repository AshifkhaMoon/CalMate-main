package com.example.calmate

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class Calmate : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        scheduleCaloriesResetWork()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleCaloriesResetWork() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Calmate", "User ID is null, not scheduling work.")
            return
        }

        val workData = workDataOf("userId" to userId)

        val resetRequest = PeriodicWorkRequestBuilder<CaloriesResetWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.SECONDS)
            .setInputData(workData)
            .build()


        Log.d("chackIfWorkDone", "WORKED")
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CaloriesReset",
            ExistingPeriodicWorkPolicy.KEEP,
            resetRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
     fun calculateInitialDelay(): Long {
        val currentDateTime = LocalDateTime.now(ZoneId.of("GMT+3"))
        val tomorrowMidnight = currentDateTime
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay(ZoneId.of("GMT+3"))
        val durationUntilMidnight = Duration.between(currentDateTime, tomorrowMidnight)
        Log.d("calculateInitialDelay", "currentDateTime: $currentDateTime")
        Log.d("calculateInitialDelay", "tomorrowMidnight: $tomorrowMidnight")
        Log.d("calculateInitialDelay", "durationUntilMidnight: $durationUntilMidnight")
        return durationUntilMidnight.seconds
    }
}