package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUp = findViewById<TextView>(R.id.signUp2)
        signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
        }

        val resetBtn = findViewById<TextView>(R.id.forgotEmail)
        resetBtn.setOnClickListener {
            startActivity(Intent(this, PWFindActivity::class.java))
        }
    }

}