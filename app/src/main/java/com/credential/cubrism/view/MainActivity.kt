package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.model.repository.UserRepository
import com.credential.cubrism.viewmodel.DataStoreViewModel
import com.credential.cubrism.viewmodel.UserViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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

        navigationSet()

        userViewModel.getUserInfo()
        observeViewModel()
    }

    private val bottomNavItems = listOf(
        MeowBottomNavigation.Model(1, R.drawable.home),
        MeowBottomNavigation.Model(2, R.drawable.study),
        MeowBottomNavigation.Model(3, R.drawable.calendar),
        MeowBottomNavigation.Model(4, R.drawable.qualification)
    )
    private lateinit var currentFragment: Fragment
    private var homeFragment: HomeFragment = HomeFragment()
    private var studyFragment: StudyFragment = StudyFragment()
    private var calFragment: CalFragment = CalFragment()
    private var qualificationFragment: QualificationFragment = QualificationFragment()

    private fun navigationSet() {
        binding.bottomNavigationView.show(1, true)

        bottomNavItems.forEach {
            binding.bottomNavigationView.add(it)
        }
        navigationInit()

        binding.bottomNavigationView.setOnClickMenuListener {
            when (it.id) {
                1 -> showFragment(homeFragment)
                2 -> showFragment(studyFragment)
                3 -> showFragment(calFragment)
                4 -> showFragment(qualificationFragment)
            }
        }
    }
    private fun navigationInit() {
        currentFragment = homeFragment
        val fragmentList = listOf(homeFragment, studyFragment, calFragment, qualificationFragment)
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentList) {
            transaction.add(R.id.fragmentContainerView, fragment)
        }

        for (fragment in listOf(studyFragment, calFragment, qualificationFragment)) {
            transaction.hide(fragment)
        }

        transaction.commit()
    }

    private fun showFragment(fragment: Fragment) {
        if (fragment != currentFragment) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
                .hide(currentFragment)
                .show(fragment)
                .commit()
        }
        currentFragment = fragment
    }

    private fun observeViewModel() {
        userViewModel.userInfo.observe(this) { user ->
            dataStoreViewModel.saveEmail(user.email)
            dataStoreViewModel.saveNickname(user.nickname)
            user.profileImage?.let { dataStoreViewModel.saveProfileImage(it) }
        }

        userViewModel.errorMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}