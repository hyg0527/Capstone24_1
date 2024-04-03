package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMainBinding
import com.credential.cubrism.viewmodel.JwtTokenViewModel
import com.credential.cubrism.viewmodel.UserViewModel
import com.credential.cubrism.viewmodel.UserViewModelFactory
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val jwtTokenViewModel: JwtTokenViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels { UserViewModelFactory(jwtTokenViewModel) }

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

        jwtTokenViewModel.accessToken.observe(this) { accessToken ->
            accessToken?.let {
                // retrofit intercept 코드 완성되면 주석 제거 예정
//                userViewModel.userInfo()
            }
        }
    }

    private val bottomNavItems = listOf(
        MeowBottomNavigation.Model(1, R.drawable.home),
        MeowBottomNavigation.Model(2, R.drawable.study),
        MeowBottomNavigation.Model(3, R.drawable.calendar),
        MeowBottomNavigation.Model(4, R.drawable.qualification),
        MeowBottomNavigation.Model(5, R.drawable.mypage)
    )
    private lateinit var currentFragment: Fragment
    private var homeFragment: HomeFragment = HomeFragment()
    private var studyFragment: StudyFragment = StudyFragment()
    private var calFragment: CalFragment = CalFragment()
    private var qualificationFragment: QualificationFragment = QualificationFragment()
    private var myPageFragment: MyPageFragment = MyPageFragment()

    private fun navigationSet() {
        val bottomNavigationView = findViewById<MeowBottomNavigation>(R.id.bottomNavigationView)
        bottomNavigationView.show(1, true)

        bottomNavItems.forEach {
            bottomNavigationView.add(it)
        }
        navigationInit()

        bottomNavigationView.setOnClickMenuListener {
            when (it.id) {
                1 -> showFragment(homeFragment)
                2 -> showFragment(studyFragment)
                3 -> showFragment(calFragment)
                4 -> showFragment(qualificationFragment)
                5 -> showFragment(myPageFragment)
            }
        }
    }
    private fun navigationInit() {
        currentFragment = homeFragment
        val fragmentList = listOf(homeFragment, studyFragment, calFragment, qualificationFragment, myPageFragment)
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentList) {
            transaction.add(R.id.fragmentContainerView, fragment)
        }

        for (fragment in listOf(studyFragment, calFragment, qualificationFragment, myPageFragment)) {
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
}