package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.databinding.ActivityFindpwBinding
import com.credential.cubrism.model.dto.EmailVerifyRequestDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class PWFindActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFindpwBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        binding.btnEmail.setOnClickListener {
            authViewModel.resetPassword(EmailVerifyRequestDto(binding.editEmail.text.toString()))
            binding.progressIndicator.show()
        }
    }

    private fun observeViewModel() {
        authViewModel.apply {
            resetPassword.observe(this@PWFindActivity) { messageDto ->
                binding.progressIndicator.hide()
                binding.btnEmail.text = "이메일 재전송"
                Toast.makeText(this@PWFindActivity, messageDto.message, Toast.LENGTH_SHORT).show()
            }

            errorMessage.observe(this@PWFindActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    binding.progressIndicator.hide()
                    Toast.makeText(this@PWFindActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}