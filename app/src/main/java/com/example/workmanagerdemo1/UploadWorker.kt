package com.example.workmanagerdemo1

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

// 특정 조건이 충족될 떄 실행되는 workManager instance class
class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            for (i in 0 .. 600) {
                Log.i("MY TAG", "Uploading $i")
            }

            return Result.success()
        } catch (e:Exception) {
            return Result.failure()
        }
    }
}