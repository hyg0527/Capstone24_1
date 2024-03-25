package com.credential.cubrism.view

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.R
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.databinding.ActivitySignupBinding
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 코드 설명
 *
 * 이메일을 입력하고 인증 요청 버튼을 누르면 서버에서 이메일 존재 여부를 확인하고 인증 코드를 발급한다.
 * 인증 코드를 입력하고 코드 인증 버튼을 누르면 서버에서 인증 코드를 확인한다.
 * 인증이 완료되면 이메일 EditText, 인증 코드 EditText, 인증 요청 버튼, 코드 인증 버튼이 비활성화된다.
 * 닉네임, 비밀번호를 입력하고 회원가입 버튼을 누르면 서버에서 각각 유효성을 확인하고 회원가입을 진행한다.
 */
class SignUpActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }
    private val viewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }

    private var countDown: CountDownTimer? = null
    private var isTimerRunning = false
    private var isValidEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModelObserver()

        binding.apply {
            backBtn.setOnClickListener { finish() }
            requestCodeBtn.setOnClickListener { requestVerifyCode() }
            verifyCodeBtn.setOnClickListener { verifyEmail() }
            registerBtn.setOnClickListener { signUp() }
            emailCode.addTextChangedListener {
                binding.isvalidCode.visibility = View.GONE
                binding.emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner)
            }
        }
    }

    private fun viewModelObserver() {
        viewModel.emailVerifyRequestResult.observe(this) { result ->
            binding.requestCodeBtn.isEnabled = true
            binding.progressIndicator.hide()
            when (result) {
                is ResultUtil.Success -> {
                    startCountdownTimer()
                    val message = result.data.string().let { JSONObject(it).getString("message") }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.emailVerifyResult.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    emailVerifySuccess()
                }
                is ResultUtil.Error -> {
                    emailVerifyFailed()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.signUpResult.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val message = result.data.string().let { JSONObject(it).getString("message") }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    // TODO: 회원가입 성공 시 자동으로 로그인
                }
                is ResultUtil.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 이메일 인증 번호 요청
    private fun requestVerifyCode() {
        binding.requestCodeBtn.isEnabled = false
        binding.progressIndicator.show()

        val email = binding.registerEmail.text.toString()
        viewModel.emailVerifyRequest(email)
    }

    // 이메일 인증
    private fun verifyEmail() {
        val email = binding.registerEmail.text.toString()
        val code = binding.emailCode.text.toString()

        viewModel.emailVerify(email, code)
    }

    // 회원가입
    private fun signUp() {
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val passwordCheck = binding.registerPWConfirm.text.toString()
        val nickname = binding.registerName.text.toString()

        if (password != passwordCheck) {
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

        viewModel.signUp(email, password, nickname)
    }

    // 이메일 인증 성공
    private fun emailVerifySuccess() {
        countDown?.cancel()
        binding.apply {
            isvalidCode.apply {
                visibility = View.VISIBLE
                text = "✓ 인증이 완료되었습니다."
                setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.green))
            }
            emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner)
            requestCodeBtn.isEnabled = false
            verifyCodeBtn.isEnabled = false
            registerEmail.isEnabled = false
            emailCode.isEnabled = false
            countDown.visibility = View.GONE
        }
        isValidEmail = true
    }

    // 이메일 인증 실패
    private fun emailVerifyFailed() {
        binding.isvalidCode.apply {
            visibility = View.VISIBLE
            text = "✓ 유효하지 않은 인증 코드입니다."
            setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red2))
        }
        binding.emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner_red)
        isValidEmail = false
    }

    // 타이머 시작
    private fun startCountdownTimer() {
        countDown?.cancel() // 기존의 타이머가 있다면 취소
        binding.countDown.visibility = View.VISIBLE

        countDown = object : CountDownTimer(300000, 1000) {
            // 유효시간 5분
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(minute)

                val displayTime = String.format("%02d:%02d", minute, second)
                binding.countDown.text = displayTime
            }

            override fun onFinish() {
                isTimerRunning = false
                if (!isValidEmail) {
                    binding.countDown.visibility = View.GONE
                    binding.verifyCodeBtn.text = "재인증"
                    binding.isvalidCode.apply {
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