package com.credential.cubrism

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityResetpwBinding

class PWResetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityResetpwBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            backBtn.setOnClickListener { finish() }
            btnResetPW.setOnClickListener {
                Toast.makeText(root.context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}