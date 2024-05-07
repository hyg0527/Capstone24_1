package com.credential.cubrism.model.service

import android.util.Log
import com.auth0.android.jwt.JWT
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.MyApplication
import com.credential.cubrism.model.api.AuthApi
import com.credential.cubrism.model.repository.NotiRepository
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val dataStore = MyApplication.getInstance().getDataStoreRepository()
private val notificationRepository = NotiRepository(MyApplication.getInstance().getNotiDao())

object RetrofitClient {
    private var retrofit: Retrofit? = null
    private var retrofitWithAuth: Retrofit? = null
    private val tikXml = TikXml.Builder()
        .exceptionOnUnreadXml(false)
        .build()

    // 인증 불필요
    fun getRetrofit(): Retrofit? {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BuildConfig.SPRING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 인증 필요
    fun getRetrofitWithAuth(): Retrofit? {
        val interceptorClient = OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor())
            .addInterceptor(ResponseInterceptor())
            .build()

        return retrofitWithAuth ?: Retrofit.Builder()
            .baseUrl(BuildConfig.SPRING_URL)
            .client(interceptorClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // XML 파싱
    fun getRetrofitWithXml(): Retrofit? {
        return retrofit ?: Retrofit.Builder()
            .baseUrl("https://cubrism-bucket.s3.ap-northeast-2.amazonaws.com")
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .build()
    }
}

// 요청을 가로챔 (Access Token을 추가하기 위해)
class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            dataStore.getAccessToken().first()
        }

//        val refreshToken = runBlocking {
//            dataStore.getRefreshToken().first()?.let { JWT(it) }
//        }
//
//        // Refresh Token의 만료 기간이 2일 이내로 남았으면
//        if ((refreshToken?.expiresAt?.time ?: (0 - System.currentTimeMillis())) < 2 * 24 * 60 * 60 * 1000) {
//            // Refresh Token 재발급 요청
//            val response = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.reissueRefreshToken()?.execute()
//
//            if (response?.isSuccessful == true) { // RefreshToken 재발급 성공 시
//                response.body()?.refreshToken?.let { newRefreshToken ->
//                    Log.d("테스트", "[RequestInterceptor] New RefreshToken: $newRefreshToken")
//
//                    runBlocking {
//                        // 새로 발급받은 RefreshToken을 DataStore에 저장
//                        dataStore.saveRefreshToken(newRefreshToken)
//                    }
//                }
//            }
//        }

        Log.d("테스트", "[RequestInterceptor] AccessToken: $accessToken")

        // 헤더에 Access Token 추가
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(request)
    }
}

// 응답을 가로챔 (Access Token 만료 시 재발급을 위해)
class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 응답을 받음
        val originalResponse = chain.proceed(chain.request())
        // 응답 본문(Body)을 String으로 변환
        val responseBodyString = originalResponse.body?.string() ?: ""

        // 응답 코드가 401(Unauthorized)일 경우
        if (originalResponse.code == 401) {
            // Body를 JSONObject로 변환 후 에러 메시지를 가져옴
            val errorMessage = JSONObject(responseBodyString).optString("message")

            Log.d("테스트", "[ResponseInterceptor] ErrorMessage: $errorMessage")

            // AccessToken이 만료되었을 경우
            if (errorMessage == "JWT 토큰이 만료되었습니다.") {
                // DataStore에서 AccessToken과 RefreshToken을 가져옴
                val accessToken = runBlocking { dataStore.getAccessToken().first() }
                val refreshToken = runBlocking { dataStore.getRefreshToken().first() }

                Log.d("테스트", "[ResponseInterceptor] AccessToken: $accessToken")
                Log.d("테스트", "[ResponseInterceptor] RefreshToken: $refreshToken")

                if (accessToken != null && refreshToken != null) {
                    // AccessToken과 RefreshToken을 이용해 AccessToken 재발급 요청
                    val response = RetrofitClient.getRetrofit()?.create(AuthApi::class.java)?.reissueAccessToken("Bearer $accessToken", refreshToken)?.execute()

                    if (response?.isSuccessful == true) { // AccessToken 재발급 성공 시
                        response.body()?.accessToken?.let { newAccessToken ->
                            Log.d("테스트", "[ResponseInterceptor] New AccessToken: $newAccessToken")
                            runBlocking {
                                dataStore.saveAccessToken(newAccessToken)
                            }

                            val newRequest = chain.request().newBuilder()
                                .removeHeader("Authorization")
                                .addHeader("Authorization", "Bearer $newAccessToken")
                                .build()

                            Log.d("테스트", "[ResponseInterceptor] New Request: $newRequest")

                            return chain.proceed(newRequest)
                        }
                    } else { // RefreshToken이 만료되었을 경우
                        Log.d("테스트", "RefreshToken 만료")

                        runBlocking {
                            // DataStore에 저장된 AccessToken과 RefreshToken을 삭제
                            dataStore.deleteAccessToken()
                            dataStore.deleteRefreshToken()

                            // DataStore에 저장되어 있는 유저 정보를 삭제
                            dataStore.deleteEmail()
                            dataStore.deleteNickname()
                            dataStore.deleteProfileImage()

                            // Room에 저장된 알림 삭제
                            notificationRepository.deleteAllNoties()
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