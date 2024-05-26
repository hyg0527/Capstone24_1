package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivitySignupBinding
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }

    private var countDown: CountDownTimer? = null
    private var isTimerRunning = false
    private var isValidEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        viewModelObserver()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        binding.btnRequest.setOnClickListener { requestVerifyCode() }
        binding.btnVerify.setOnClickListener { verifyEmail() }
        binding.btnSignUp.setOnClickListener { signUp() }
        binding.editCode.addTextChangedListener {
            binding.txtVerify.visibility = View.GONE
            binding.editCode.background = ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner, null)
        }
    }

    private fun viewModelObserver() {
        authViewModel.apply {
            emailVerifyRequest.observe(this@SignUpActivity) { result ->
                when (result) {
                    is ResultUtil.Success -> {
                        Toast.makeText(this@SignUpActivity, result.data.message, Toast.LENGTH_SHORT).show()
                        startCountdownTimer()
                    }
                    is ResultUtil.Error -> Toast.makeText(this@SignUpActivity, result.error, Toast.LENGTH_SHORT).show()
                    is ResultUtil.NetworkError -> Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
                binding.btnRequest.apply {
                    isEnabled = true
                    background = ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner, null)
                }
                binding.btnRequest.text = "재요청"
                binding.progressIndicator.visibility = View.GONE
            }

            emailVerify.observe(this@SignUpActivity) { result ->
                when (result) {
                    is ResultUtil.Success -> emailVerifySuccess()
                    is ResultUtil.Error -> emailVerifyFailed()
                    is ResultUtil.NetworkError -> Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            signUp.observe(this@SignUpActivity) { result ->
                when (result) {
                    is ResultUtil.Success -> {
                        Toast.makeText(this@SignUpActivity, result.data.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("email", binding.editEmail.text.toString())
                        intent.putExtra("password", binding.editPasswordConfirm.text.toString())
                        setResult(RESULT_OK, intent).also { finish() }
                    }
                    is ResultUtil.Error -> {
                        Toast.makeText(this@SignUpActivity, result.error, Toast.LENGTH_SHORT).show()
                        binding.progressIndicator.visibility = View.GONE
                    }
                    is ResultUtil.NetworkError -> {
                        Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        binding.progressIndicator.visibility = View.GONE
                    }
                }
            }
        }
    }

    // 이메일 인증 번호 요청
    private fun requestVerifyCode() {
        countDown?.cancel()

        binding.btnRequest.apply {
            isEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_gray3, null)
        }
        binding.progressIndicator.show()

        authViewModel.emailVerifyRequest(binding.editEmail.text.toString())
    }

    // 이메일 인증
    private fun verifyEmail() {
        val email = binding.editEmail.text.toString()
        val code = binding.editCode.text.toString()

        authViewModel.emailVerify(email, code)
    }

    // 회원가입
    private fun signUp() {
        val email = binding.editEmail.text.toString()
        val password = binding.editPassword.text.toString()
        val passwordConfirm = binding.editPasswordConfirm.text.toString()
        val nickname = binding.editNickname.text.toString()

        if (password != passwordConfirm) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidEmail) {
            Toast.makeText(this, "이메일 인증을 완료해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding.checkBox.isChecked) {
            Toast.makeText(this, "약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.signUp(email, password, nickname)
        binding.progressIndicator.show()
    }

    // 이메일 인증 성공
    private fun emailVerifySuccess() {
        countDown?.cancel()
        binding.txtVerify.apply {
            visibility = View.VISIBLE
            text = "✓ 인증이 완료되었습니다."
            setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.green))
        }
        binding.editCode.setBackgroundResource(R.drawable.edittext_rounded_corner)
        binding.btnRequest.apply {
            isEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_gray3, null)
        }
        binding.btnVerify.apply {
            isEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_gray3, null)
        }
        binding.editEmail.apply {
            isEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_gray, null)
        }
        binding.editCode.apply {
            isEnabled = false
            background = ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_gray, null)
        }
        binding.txtTimer.visibility = View.GONE
        isValidEmail = true
    }

    // 이메일 인증 실패
    private fun emailVerifyFailed() {
        binding.txtVerify.apply {
            visibility = View.VISIBLE
            text = "✓ 유효하지 않은 인증 코드입니다."
            setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red2))
        }
        binding.editCode.background = ResourcesCompat.getDrawable(resources, R.drawable.edittext_rounded_corner_red, null)
        isValidEmail = false
    }

    // 타이머 시작
    private fun startCountdownTimer() {
        countDown?.cancel() // 기존의 타이머가 있다면 취소
        binding.txtTimer.visibility = View.VISIBLE

        countDown = object : CountDownTimer(300000, 1000) {
            // 유효시간 5분
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(minute)

                val displayTime = String.format("%02d:%02d", minute, second)
                binding.txtTimer.text = displayTime
            }

            override fun onFinish() {
                isTimerRunning = false
                if (!isValidEmail) {
                    binding.txtTimer.visibility = View.GONE
                    binding.btnVerify.text = "재인증"
                    binding.txtVerify.apply {
                        visibility = View.VISIBLE
                        text = "✓ 재인증이 필요합니다."
                    }
                }
            }
        }

        countDown?.start()
        isTimerRunning = true
    }
}