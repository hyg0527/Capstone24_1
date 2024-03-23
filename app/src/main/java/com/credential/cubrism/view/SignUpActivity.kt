package com.credential.cubrism.view

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.R
import com.credential.cubrism.data.api.AuthApi
import com.credential.cubrism.data.dto.EmailVerifyDto
import com.credential.cubrism.data.dto.EmailVerifyRequestDto
import com.credential.cubrism.data.dto.SignUpDto
import com.credential.cubrism.data.service.RetrofitClient
import com.credential.cubrism.databinding.ActivitySignupBinding
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private var countDown: CountDownTimer? = null
    private var isTimerRunning = false
    private var isValidEmail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

    // 타이머 시작
    private fun startCountdownTimer() {
        countDown?.cancel() // 기존의 타이머가 있다면 취소
        binding.requestCodeBtn.isEnabled = false
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

    // 이메일 인증 번호 요청
    private fun requestVerifyCode() {
        binding.requestCodeBtn.isEnabled = false
        binding.progressIndicator.show()

        RetrofitClient.getRetrofit()
            ?.create(AuthApi::class.java)
            ?.emailVerifyRequest(EmailVerifyRequestDto(binding.registerEmail.text.toString()))
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val message: String?
                    if (response.isSuccessful) {
                        message = response.body()?.string()?.let { JSONObject(it).getString("message") }
                        startCountdownTimer()
                    } else {
                        message = response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                    }
                    Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                    binding.requestCodeBtn.isEnabled = true
                    binding.progressIndicator.hide()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    binding.progressIndicator.hide()
                }
            })
    }

    // 이메일 인증
    private fun verifyEmail() {
        RetrofitClient.getRetrofit()
            ?.create(AuthApi::class.java)
            ?.emailVerify(EmailVerifyDto(binding.registerEmail.text.toString(), binding.emailCode.text.toString()))
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        emailVerifySuccess()
                    } else {
                        emailVerifyFailed()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            })
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

        RetrofitClient.getRetrofit()
            ?.create(AuthApi::class.java)
            ?.signUp(SignUpDto(email, password, nickname))
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val message: String? = if (response.isSuccessful) {
                        response.body()?.string()?.let { JSONObject(it).getString("message") }
                        // TODO: 회원가입 성공 시 자동으로 로그인
                    } else {
                        response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                    }
                    Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            })
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
}