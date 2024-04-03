package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityFindpwBinding

class PWFindActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFindpwBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            backBtn.setOnClickListener { finish() }
            codeBtn.setOnClickListener {// 코드 전송 버튼
                if (registerEmail.text.isNullOrEmpty())
                    Toast.makeText(root.context, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(root.context, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}