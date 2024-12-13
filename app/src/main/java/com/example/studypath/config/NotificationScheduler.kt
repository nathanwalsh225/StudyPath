package com.example.studypath.config

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

//https://medium.com/@appdevinsights/work-manager-android-6ea8daad56ee
class NotificationScheduler(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        //Basic notification for now will just be sending the task Name and number of subtasks left
        val taskName = inputData.getString("taskName") ?: return Result.failure()
        val subtaskAmount = inputData.getString("subtasks") ?: return Result.failure()

        Log.d("TAG", "Sending notification for $taskName with $subtaskAmount subtasks left")

        sendNotification(taskName, subtaskAmount)

        return Result.success()
    }

    private fun sendNotification(taskName: String, subtaskAmount: String) {
        val message = RemoteMessage.Builder("studypath-73936@gcm.googleapis.com")
            .setMessageId(System.currentTimeMillis().toString())
            .addData("title", "Reminder: $taskName")
            .addData("body", "You have $subtaskAmount subtasks left for $taskName")
            .build()

        Log.d("TAG", "Sending notification: $message")

        FirebaseMessaging.getInstance().send(message)
    }

}