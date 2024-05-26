package com.credential.cubrism.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.credential.cubrism.MyApplication
import com.credential.cubrism.databinding.ActivitySplashBinding
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeViewModel()

        authViewModel.getUserInfo()
    }

    private fun observeViewModel() {
        authViewModel.apply {
            getUserInfo.observe(this@SplashActivity) { user ->
                // 서버에서 유저 정보를 성공적으로 받아오면 유저 정보를 저장
                myApplication.getUserData().apply {
                    setLoginStatus(true)
                    setUserData(user.email, user.nickname, user.profileImage)
                }

                startMainActivity()
            }

            // 유저 정보를 받아오지 못하면 메인 액티비티로 이동
            errorMessage.observe(this@SplashActivity) { event ->
                event.getContentIfNotHandled()?.let {
                    startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        lifecycleScope.launch {
            delay(2000) // 2초 딜레이 (임시)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}