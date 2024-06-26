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
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.model.repository.ScheduleRepository
import com.credential.cubrism.viewmodel.MainFragmentType
import com.credential.cubrism.viewmodel.MainViewModel
import com.credential.cubrism.viewmodel.ScheduleViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mainViewModel: MainViewModel by viewModels()
    private val scheduleViewModel: ScheduleViewModel by viewModels { ViewModelFactory(ScheduleRepository()) }

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

        if (savedInstanceState == null) { setupFragment() }
        setupBottomNav()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        scheduleViewModel.getScheduleList(null, null)
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
        binding.transparentView.setOnClickListener {}
    }

    private fun observeViewModel() {
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
            add(binding.fragmentContainerView.id, HomeFragment(), MainFragmentType.HOME.tag)
            add(binding.fragmentContainerView.id, StudyFragment(), MainFragmentType.STUDY.tag)
            add(binding.fragmentContainerView.id, ScheduleFragment(), MainFragmentType.SCHEDULE.tag)
            add(binding.fragmentContainerView.id, QualificationFragment(), MainFragmentType.QUALIFICATION.tag)
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