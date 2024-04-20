package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.MyApplication
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.databinding.ActivitySigninBinding
import com.credential.cubrism.model.dto.FcmTokenRequest
import com.credential.cubrism.model.dto.SocialTokenDto
import com.credential.cubrism.model.repository.DataStoreRepository
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.service.YourService
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.DataStoreViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val dataStoreViewModel: DataStoreViewModel by viewModels { ViewModelFactory(DataStoreRepository()) }

    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            account?.serverAuthCode?.let {
                googleLogin(it)
            }
        } catch (_: ApiException) { }
    }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

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
        authViewModel.signIn.observe(this) { result ->
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

        authViewModel.googleSignIn.observe(this) { result ->
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

        authViewModel.kakaoSignIn.observe(this) { result ->
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
    private fun signInSuccess(accessToken: String, refreshToken: String) {
        dataStoreViewModel.saveAccessToken(accessToken)
        dataStoreViewModel.saveRefreshToken(refreshToken)
        setResult(RESULT_OK).also { finish() }

//        var myEmail: String? = null
//
//        lifecycleScope.launch {
//            while (true) {
//                myEmail = dataStore.getEmail().first()
//                Log.d("FCM", "Fetched email: $myEmail") // 추가된 로그
//                if (myEmail != null) {
//                    Log.d("FCM", "Email break: $myEmail")
//                    break
//                }
//                Log.d("FCM", "Waiting for email...")
//                delay(3000) // 3초 동안 기다림
//            }
//
//            Log.d("FCM", "Email: $myEmail")
//
//            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
//                    return@addOnCompleteListener
//                }
//
//                // Get new FCM registration token
//                val token = task.result
//
//                // Log and toast
//                Log.d("FCM", "FCM token send!!!: $token")
//                Log.d("FCM", "myEmail: $myEmail")
//
//                // 서버에 토큰을 전송
//                val request = FcmTokenRequest(token!!)
//                val service = RetrofitClient.getRetrofitWithAuth()?.create(YourService::class.java)
//                if (service != null) {
//                    val call = service.updateFcmToken(myEmail ?: return@addOnCompleteListener, request)
//                    call.enqueue(object : Callback<Void> {
//                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                            if (response.isSuccessful) {
//                                Log.d("FCM", "Token sent to server successfully.")
//                            } else {
//                                Log.w("FCM", "Failed to send token to server.")
//                            }
//                        }
//
//                        override fun onFailure(call: Call<Void>, t: Throwable) {
//                            Log.e("FCM", "Error occurred while sending token to server.", t)
//                        }
//                    })
//                } else {
//                    Log.e("FCM", "Service creation failed.")
//                }
//            }
//        }
        // FCM 토큰을 가져와서 서버에 전송

    }
}