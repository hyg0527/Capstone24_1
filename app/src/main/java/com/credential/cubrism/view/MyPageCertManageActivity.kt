package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityMypageCertmanageBinding

class MyPageCertManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageCertmanageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }
        binding.btnApply.setOnClickListener {
            Toast.makeText(this, "저장했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}