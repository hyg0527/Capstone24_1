package com.credential.cubrism.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.model.dto.FcmTokenDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.FcmRepository
import com.credential.cubrism.view.utils.FragmentType
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.FcmViewModel
import com.credential.cubrism.viewmodel.MainViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val fcmViewModel: FcmViewModel by viewModels { ViewModelFactory(FcmRepository()) }

    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

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

    companion object {
        private const val POST_NOTIFICATIONS_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermission()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        authViewModel.getUserInfo()

        if (savedInstanceState == null) { setupFragment() }
        setupBottomNav()
        observeViewModel()

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
        // 서버에서 유저 정보를 가져오면
        authViewModel.getUserInfo.observe(this) { user ->
            lifecycleScope.launch {
                // DataStore에 유저 정보를 저장
                dataStore.apply {
                    saveEmail(user.email)
                    saveNickname(user.nickname)
                    saveProfileImage(user.profileImage ?: "")

                    // 서버로 FCM 토큰 전송
                    getFcmToken().first()?.let { token ->
                        fcmViewModel.updateFcmToken(FcmTokenDto(token))
                    }
                }
            }
        }

        mainViewModel.currentFragmentType.observe(this) { fragmentType ->
            binding.bottomNavigationView.show(fragmentType.ordinal + 1, true)

            // 선택한 Fragment만 보여주고 나머지는 숨김 처리
            val currentFragment = supportFragmentManager.findFragmentByTag(fragmentType.tag)
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out).apply {
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
        // Fragment 초기화
        supportFragmentManager.beginTransaction().apply {
            add(binding.fragmentContainerView.id, HomeFragment(), FragmentType.HOME.tag)
            add(binding.fragmentContainerView.id, StudyFragment(), FragmentType.STUDY.tag)
            add(binding.fragmentContainerView.id, CalFragment(), FragmentType.CALENDAR.tag)
            add(binding.fragmentContainerView.id, QualificationFragment(), FragmentType.QUALIFICATION.tag)
            commit()
        }
    }

    private fun checkPermission() {
        // 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), POST_NOTIFICATIONS_CODE)
        }
    }
}