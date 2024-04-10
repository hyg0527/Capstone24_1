package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.view.utils.FragmentType
import com.credential.cubrism.viewmodel.DataStoreViewModel
import com.credential.cubrism.viewmodel.MainViewModel
import com.credential.cubrism.viewmodel.UserViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

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
                dataStoreViewModel.saveNickname(it.nickname)
                it.profileImage?.let { image ->
                    dataStoreViewModel.saveProfileImage(image)
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