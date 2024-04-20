package com.credential.cubrism.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.model.dto.FcmTokenRequest
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.model.service.RetrofitClient
import com.credential.cubrism.model.service.YourService
import com.credential.cubrism.view.utils.FragmentType
import com.credential.cubrism.viewmodel.DataStoreViewModel
import com.credential.cubrism.viewmodel.MainViewModel
import com.credential.cubrism.viewmodel.UserViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mainViewModel: MainViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels { ViewModelFactory(UserRepository()) }
    private val dataStoreViewModel: DataStoreViewModel by viewModels { ViewModelFactory(MyApplication.getInstance().getDataStoreRepository()) }

    private var backPressedTime: Long = 0
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() - backPressedTime >= 2000) {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(this@MainActivity, "뒤로 가기를 한번 더 누르면 종료 합니다.", Toast.LENGTH_SHORT).show()
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        userViewModel.getUserInfo()

        if (savedInstanceState == null) { setupFragment() }
        setupBottomNav()
        observeViewModel()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FCM", "Current token: $token")
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.apply {
            add(MeowBottomNavigation.Model(1, R.drawable.home))
            add(MeowBottomNavigation.Model(2, R.drawable.study))
            add(MeowBottomNavigation.Model(3, R.drawable.calendar))
            add(MeowBottomNavigation.Model(4, R.drawable.qualification))

            setOnClickMenuListener {
                mainViewModel.setCurrentFragment(it)
            }
        }
    }

    private fun observeViewModel() {
        userViewModel.userInfo.observe(this) { user ->
            user?.let {
                dataStoreViewModel.saveEmail(it.email)
                Log.d("MyPageActivity", "Email saved: ${it.email}")
                dataStoreViewModel.saveNickname(it.nickname)
                it.profileImage?.let { image ->
                    dataStoreViewModel.saveProfileImage(image)
                }

                // 이메일이 저장된 후에 FCM 토큰을 가져오고 서버에 전송
                lifecycleScope.launch {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                            return@addOnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result

                        // Log and toast
                        Log.d("FCM", "FCM token send!!!: $token")
                        Log.d("FCM", "myEmail: ${it.email}")

                        // 서버에 토큰을 전송
                        val request = FcmTokenRequest(token!!)
                        val service = RetrofitClient.getRetrofitWithAuth()?.create(YourService::class.java)
                        if (service != null) {
                            val call = service.updateFcmToken(it.email, request)
                            call.enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Log.d("FCM", "Token sent to server successfully.")
                                    } else {
                                        Log.w("FCM", "Failed to send token to server.")
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Log.e("FCM", "Error occurred while sending token to server.", t)
                                }
                            })
                        } else {
                            Log.e("FCM", "Service creation failed.")
                        }
                    }
                }
            }
        }

        mainViewModel.currentFragmentType.observe(this) { fragmentType ->
            binding.bottomNavigationView.show(fragmentType.ordinal + 1, true)

            val currentFragment = supportFragmentManager.findFragmentByTag(fragmentType.tag)
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.fragments.forEach { fragment ->
                    if (fragment == currentFragment)
                        show(fragment)
                    else
                        hide(fragment)
                }
            }.commit()
        }
    }

    private fun setupFragment() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, HomeFragment(), FragmentType.HOME.tag)
            add(R.id.fragmentContainerView, StudyFragment(), FragmentType.STUDY.tag)
            add(R.id.fragmentContainerView, CalFragment(), FragmentType.CALENDAR.tag)
            add(R.id.fragmentContainerView, QualificationFragment(), FragmentType.QUALIFICATION.tag)
            commit()
        }
    }
}