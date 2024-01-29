package com.credential.cubrism

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//        val navController = navHostFragment.navController
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNavigationView.setupWithNavController(navController)

        navigationSet()
    }

    private val bottomNavItems = listOf(
        MeowBottomNavigation.Model(1, R.drawable.home),
        MeowBottomNavigation.Model(2, R.drawable.study),
        MeowBottomNavigation.Model(3, R.drawable.calendar),
        MeowBottomNavigation.Model(4, R.drawable.medal),
        MeowBottomNavigation.Model(5, R.drawable.mypage)
    )

    private fun navigationSet() {
        val bottomNavigationView = findViewById<MeowBottomNavigation>(R.id.bottomNavigationView)
        bottomNavigationView.show(1, true)

        bottomNavItems.forEach {
            bottomNavigationView.add(it)
        }

        bottomNavigationView.setOnClickMenuListener {
            when (it.id) {
                1 -> showFragment(HomeFragment())
                2 -> showFragment(StudyFragment())
                3 -> showFragment(CalFragment())
                4 -> showFragment(MedalFragment())
                5 -> showFragment(MyPageFragment())
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custon_fade_out)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

}