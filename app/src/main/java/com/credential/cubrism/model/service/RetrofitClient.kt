package com.credential.cubrism.model.service

import com.credential.cubrism.BuildConfig
import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.data.UserDataManager
import com.credential.cubrism.model.repository.JwtTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var retrofitWithAuth: Retrofit? = null

    // 인증 불필요
    fun getRetrofit(): Retrofit? {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BuildConfig.SPRING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 인증 필요
    fun getRetrofitWithAuth(jwtTokenRepository: JwtTokenRepository): Retrofit? {
        val interceptorClient = OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(jwtTokenRepository))
            .addInterceptor(ResponseInterceptor(jwtTokenRepository))
            .build()

        return retrofitWithAuth ?: Retrofit.Builder()
            .baseUrl(BuildConfig.SPRING_URL)
            .client(interceptorClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

// 요청을 가로챔 (Access Token을 추가하기 위해)
class RequestInterceptor(private val jwtTokenRepository: JwtTokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // DataStore 에서 Access Token을 가져옴
        val accessToken = runBlocking {
            jwtTokenRepository.getAccessToken().first()
        }

        // 헤더에 Access Token 추가
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(request)
    }
}

// 응답을 가로챔 (Access Token 만료 시 재발급을 위해)
class ResponseInterceptor(private val jwtTokenRepository: JwtTokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 응답을 받음
        val originalResponse = chain.proceed(chain.request())
        // 응답 본문(Body)을 String으로 변환
        val responseBodyString = originalResponse.body?.string() ?: ""

        // 응답 코드가 401(Unauthorized)일 경우
        if (originalResponse.code == 401) {
            // Body를 JSONObject로 변환 후 에러 메시지를 가져옴
            val errorMessage = JSONObject(responseBodyString).optString("message")
            // AccessToken이 만료되었을 경우
            if (errorMessage == "JWT 토큰이 만료되었습니다.") {
                // DataStore에서 AccessToken과 RefreshToken을 가져옴
                val accessToken = runBlocking { jwtTokenRepository.getAccessToken().first() }
                val refreshToken = runBlocking { jwtTokenRepository.getRefreshToken().first() }

                if (accessToken != null && refreshToken != null) {
                    // AccessToken과 RefreshToken을 이용해 AccessToken 재발급 요청
                    val call = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.reissueAccessToken("Bearer $accessToken", refreshToken)
                    runBlocking {
                        val response = call?.execute()
                        if (response?.isSuccessful == true) { // AccessToken 재발급 성공 시
                            // DataStore에 새로 발급받은 AccessToken을 저장
                            val newAccessToken = response.body()?.accessToken?.let {
                                jwtTokenRepository.saveAccessToken(it)
                            }


                            // 새로 발급받은 AccessToken을 헤더에 추가
                            val newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer $newAccessToken")
                                .build()

                            // 요청을 다시 보냄
                            chain.proceed(newRequest)
                        } else { // RefreshToken이 만료되었을 경우
                            // DataStore에 저장된 AccessToken과 RefreshToken을 삭제
                            jwtTokenRepository.deleteAccessToken()
                            jwtTokenRepository.deleteRefreshToken()

                            // UserDataManager에 저장되어 있는 유저 정보를 삭제
                            UserDataManager.clearUserInfo()
                        }
                    }
                }
            }
        }

        // Body를 다시 ResponseBody로 변환 (Body를 String으로 변환할 경우 스트림이 닫혀 사용이 불가능해짐)
        val responseBody = responseBodyString.toResponseBody(originalResponse.body?.contentType())
        // 변환된 ResponseBody로 다시 응답을 생성해서 반환
        return originalResponse.newBuilder().body(responseBody).build()
    }
}