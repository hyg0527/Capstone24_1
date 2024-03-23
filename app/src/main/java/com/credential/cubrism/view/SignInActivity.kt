package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.data.api.AuthApi
import com.credential.cubrism.data.dto.SignInDto
import com.credential.cubrism.data.dto.TokenDto
import com.credential.cubrism.data.service.RetrofitClient
import com.credential.cubrism.databinding.ActivitySigninBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySigninBinding.inflate(layoutInflater) }
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

        RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.signIn(SignInDto(email, password))?.enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    val tokenDto = response.body()
                    val accessToken = tokenDto?.accessToken
                    val refreshToken = tokenDto?.refreshToken
                    Log.d("SignInActivity", "AccessToken: $accessToken")
                    Log.d("SignInActivity", "RefreshToken: $refreshToken")
                    Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    val message = response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                    Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 구글 로그인
    private fun googleLogin(code: String) {
        RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.googleLogIn(code)?.enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    val tokenDto = response.body()
                    val accessToken = tokenDto?.accessToken
                    val refreshToken = tokenDto?.refreshToken
                    Log.d("SignInActivity", "AccessToken: $accessToken")
                    Log.d("SignInActivity", "RefreshToken: $refreshToken")
                    Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    val message = response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                    Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                Toast.makeText(this@SignInActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
        getGoogleClient().signOut() // JWT 토큰을 사용하기 때문에 로그아웃 처리
    }

    // 카카오 로그인
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this@SignInActivity, "로그인을 실패했습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.kakaoLogIn(token.accessToken)?.enqueue(object : Callback<TokenDto> {
                    override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                        if (response.isSuccessful) {
                            val tokenDto = response.body()
                            val accessToken = tokenDto?.accessToken
                            val refreshToken = tokenDto?.refreshToken
                            Log.d("SignInActivity", "AccessToken: $accessToken")
                            Log.d("SignInActivity", "RefreshToken: $refreshToken")
                            Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        } else {
                            val message = response.errorBody()?.string()?.let { JSONObject(it).getString("message") }
                            Toast.makeText(this@SignInActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                        Toast.makeText(this@SignInActivity, "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }

        UserApiClient.instance.logout { _ -> } // JWT 토큰을 사용하기 때문에 로그아웃 처리
    }
}