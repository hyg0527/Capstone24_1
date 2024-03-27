package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.credential.cubrism.databinding.ActivityFindpwBinding
import java.util.concurrent.TimeUnit

class PWFindActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFindpwBinding.inflate(layoutInflater) }
    private var countDown: CountDownTimer? = null
    private var isTimerRunning = false
    private var isValidEmail = false

    /*코드 전송 버튼을 누르고 인증 번호를 입력 후에 인증하기 버튼을 누르면 인증 여부가 나옴.
    * 시간이 지나면 재인증 버튼 활성화 되고 인증 하기 버튼 비활성화됨
    * 인증이 성공하면 비밀번호찾기 버튼이 활성화 되고 누르면 비밀번호 변경 화면으로 이동함.*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            backBtn.setOnClickListener { finish() }
            btnFindPW.setOnClickListener {
                startActivity(Intent(this@PWFindActivity, PWResetActivity::class.java))
                finish()
            }
            codeBtn.setOnClickListener {// 코드 전송 버튼
                Toast.makeText(root.context, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                sendVerification()
            }
            verifyBtn.setOnClickListener { // 인증 하기 버튼
                setUpTextChangedListener()
            }
            btnFindPW.isEnabled = false
        }
    }

    private fun sendVerification() {
        binding.apply {
            codeBtn.isEnabled = false
            codeBtn.setBackgroundResource(R.drawable.button_rounded_corner_lightblue)
            verifyBtn.isEnabled = true
            verifyBtn.setBackgroundResource(R.drawable.button_rounded_corner)

            emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner)
            emailCode.setText("")
            isvalidCode.text = ""

            startCountdownTimer()
            emailCode.setHint("인증 코드")

            if (countDown.visibility == View.INVISIBLE) {
                countDown.visibility = View.VISIBLE
            }

            if (isvalidCode.visibility == View.GONE) {
                isvalidCode.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpTextChangedListener() {
        binding.apply {
            if (emailCode.text.toString().equals("A")) {
                isvalidCode.text = "✓ 인증이 완료되었습니다."
                isvalidCode.setTextColor(ContextCompat.getColor(this@PWFindActivity, R.color.green))
                emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner)
                isValidEmail = true

                btnFindPW.isEnabled = true // 비밀번호 찾기 버튼 활성화 (현재는 'A'입력 시 활성화됨)
                btnFindPW.setBackgroundResource(R.drawable.button_rounded_corner)
            }
            else {
                isvalidCode.text = "✓ 유효하지 않은 인증 코드입니다."
                isvalidCode.setTextColor(ContextCompat.getColor(this@PWFindActivity, R.color.red2))
                emailCode.setBackgroundResource(R.drawable.edittext_rounded_corner_red)
                isValidEmail = false
            }
        }
    }

    private fun startCountdownTimer() { // 인증 시간 타이머 설
        countDown?.cancel() // 기존의 타이머가 있다면 취소
        countDown = object : CountDownTimer(11000, 1000) {
            // 유효시간 5분(코드확인 편의를 위해 11초로 설정해둠)
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minute)

                val displayTime = String.format("%02d:%02d", minute, second)
                binding.countDown.text = displayTime
            }

            override fun onFinish() {
                isTimerRunning = false
                if (isValidEmail == false) {
                    binding.apply {
                        codeBtn.text="재인증"
                        codeBtn.setBackgroundResource(R.drawable.button_rounded_corner)
                        codeBtn.setEnabled(true);
                        isvalidCode.text = "✓ 재인증이 필요합니다."

                        verifyBtn.isEnabled = false // 인증하기 버튼 다시 비활성화
                        verifyBtn.setBackgroundResource(R.drawable.button_rounded_corner_lightblue)
                    }
                }
            }
        }

        countDown?.start()
        isTimerRunning = true
    }
}