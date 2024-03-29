package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        splashScreenLoad()
    }

    private fun splashScreenLoad() {
        // 5초 딜레이 후에 다른 액티비티 호출
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 7000)
    }
}