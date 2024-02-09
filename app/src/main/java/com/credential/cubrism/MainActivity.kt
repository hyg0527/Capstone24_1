package com.credential.cubrism

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationSet()
        // 아래 부분은 미 로그인 상태 에만 실행 하도록 이후에 변경 예정
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private val bottomNavItems = listOf(
        MeowBottomNavigation.Model(1, R.drawable.home),
        MeowBottomNavigation.Model(2, R.drawable.study),
        MeowBottomNavigation.Model(3, R.drawable.calendar),
        MeowBottomNavigation.Model(4, R.drawable.medal),
        MeowBottomNavigation.Model(5, R.drawable.mypage)
    )
    private lateinit var currentFragment: Fragment
    private var homeFragment: HomeFragment = HomeFragment()
    private var studyFragment: StudyFragment = StudyFragment()
    private var calFragment: CalFragment = CalFragment()
    private var medalFragment: MedalFragment = MedalFragment()
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
                4 -> showFragment(medalFragment)
                5 -> showFragment(myPageFragment)
            }
        }
    }
    private fun navigationInit() {
        currentFragment = homeFragment
        val fragmentList = listOf(homeFragment, studyFragment, calFragment, medalFragment, myPageFragment)
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentList) {
            transaction.add(R.id.fragmentContainerView, fragment)
        }
        for (fragment in listOf(studyFragment, calFragment, medalFragment, myPageFragment)) {
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

    private var backPressedTime = 0L

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기를 한번 더 누르면 종료 합니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            super.onBackPressed()
        }
    }
}