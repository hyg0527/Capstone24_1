package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.MyApplication
import com.credential.cubrism.databinding.ActivitySigninBinding
import com.credential.cubrism.model.dto.SignInDto
import com.credential.cubrism.model.dto.SignInSuccessDto
import com.credential.cubrism.model.dto.SocialTokenDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    // 구글 로그인 실행
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            account?.serverAuthCode?.let {
                googleLogin(it)
            }
        } catch (_: ApiException) { }
    }

    // 회원가입 후 해당 계정으로 로그인
    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val email = data?.getStringExtra("email") ?: ""
            val password = data?.getStringExtra("password") ?: ""

            signIn(email, password)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        observeViewModel()
    }

    private fun setupView() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            signIn(email, password)
        }

        binding.imgGoogle.setOnClickListener {
            val googleSignInClient = getGoogleClient()
            val signInIntent = googleSignInClient.signInIntent
            googleAuthLauncher.launch(signInIntent)
        }

        binding.imgKakao.setOnClickListener {
            kakaoLogin()
        }

        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startForRegisterResult.launch(intent)
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, PWFindActivity::class.java))
        }
    }

    private fun observeViewModel() {
        authViewModel.apply {
            signIn.observe(this@SignInActivity) {
                signInSuccess(it)
            }

            googleLogIn.observe(this@SignInActivity) {
                signInSuccess(it)
            }

            kakaoLogIn.observe(this@SignInActivity) {
                signInSuccess(it)
            }

            errorMessage.observe(this@SignInActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
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
    private fun signIn(email: String, password: String) {
        lifecycleScope.launch {
            val fcmToken = dataStore.getFcmToken().first()
            if (fcmToken != null)
                authViewModel.signIn(SignInDto(email, password, fcmToken))
            else
                authViewModel.signIn(SignInDto(email, password, null))
        }
    }

    // 구글 로그인
    private fun googleLogin(serverAuthCode: String) {
        authViewModel.googleSignIn(SocialTokenDto(serverAuthCode))
        getGoogleClient().signOut() // JWT 토큰을 사용하기 때문에 로그아웃 처리
    }

    // 카카오 로그인
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this@SignInActivity, "로그인을 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                val accessToken = token.accessToken
                authViewModel.kakaoSignIn(SocialTokenDto(accessToken))
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
    private fun signInSuccess(dto : SignInSuccessDto) {
        lifecycleScope.launch {
            // DataStore에 토큰 저장
            dataStore.apply {
                dto.token.accessToken?.let { saveAccessToken(it) }
                dto.token.refreshToken?.let { saveRefreshToken(it) }
            }

            // 유저 정보 저장
            myApplication.getUserData().apply {
                setLoginStatus(true)
                setUserData(dto.user.email, dto.user.nickname, dto.user.profileImage)
            }

            finish()
        }
    }
}