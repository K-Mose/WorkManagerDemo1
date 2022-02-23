package com.example.workmanagerdemo1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn = findViewById<Button>(R.id.btn_start)
        btn.setOnClickListener {
            setOneTImeWorkerRequest()
        }

    }

    // workManager가 수행할 코드
    private fun setOneTImeWorkerRequest() {
        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .build()
        // workManagerInstance
        WorkManager.getInstance(applicationContext)
            .enqueue(uploadRequest)

    }
}