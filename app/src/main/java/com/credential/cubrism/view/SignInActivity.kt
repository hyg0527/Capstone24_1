package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.databinding.ActivitySigninBinding
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.JwtTokenViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val jwtTokenViewModel: JwtTokenViewModel by viewModels()

    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            account?.serverAuthCode?.let {
                googleLogin(it)
            }
        } catch (_: ApiException) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModelObserver()

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.loginButton.setOnClickListener {
            signIn()
        }

        binding.googleSymbol.setOnClickListener {
            val googleSignInClient = getGoogleClient()
            val signInIntent = googleSignInClient.signInIntent
            googleAuthLauncher.launch(signInIntent)
        }

        binding.kakaoSymbol.setOnClickListener {
            kakaoLogin()
        }

        binding.signUp2.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotEmail.setOnClickListener {
            startActivity(Intent(this, PWFindActivity::class.java))
        }
    }

    private fun viewModelObserver() {
        authViewModel.signInResult.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val accessToken = result.data.accessToken
                    val refreshToken = result.data.refreshToken

                    if (accessToken != null && refreshToken != null)
                        signInSuccess(accessToken, refreshToken)
                }
                is ResultUtil.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, result.networkError, Toast.LENGTH_SHORT).show()
                }
            }
        }

        authViewModel.googleSignInResult.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val accessToken = result.data.accessToken
                    val refreshToken = result.data.refreshToken

                    if (accessToken != null && refreshToken != null)
                        signInSuccess(accessToken, refreshToken)
                }
                is ResultUtil.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, result.networkError, Toast.LENGTH_SHORT).show()
                }
            }
        }

        authViewModel.kakaoSignInResult.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val accessToken = result.data.accessToken
                    val refreshToken = result.data.refreshToken

                    if (accessToken != null && refreshToken != null)
                        signInSuccess(accessToken, refreshToken)
                }
                is ResultUtil.Error -> {
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is ResultUtil.NetworkError -> {
                    Toast.makeText(this, result.networkError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    // 이메일 로그인
    private fun signIn() {
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        authViewModel.signIn(email, password)
    }

    // 구글 로그인
    private fun googleLogin(serverAuthCode: String) {
        authViewModel.googleSignIn(serverAuthCode)
        getGoogleClient().signOut() // JWT 토큰을 사용하기 때문에 로그아웃 처리
    }

    // 카카오 로그인
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this@SignInActivity, "로그인을 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                val accessToken = token.accessToken
                authViewModel.kakaoSignIn(accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }

        UserApiClient.instance.logout { _ -> } // JWT 토큰을 사용하기 때문에 로그아웃 처리
    }

    // 로그인 성공
    private fun signInSuccess(accessToken: String, refreshToken: String) {
        jwtTokenViewModel.saveAccessToken(accessToken)
        jwtTokenViewModel.saveRefreshToken(refreshToken)
        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show() // 로그인 성공 확인을 위한 임시 토스트
        // TODO: 메인 화면으로 이동
    }
}