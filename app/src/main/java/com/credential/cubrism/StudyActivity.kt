package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class StudyActivity : AppCompatActivity() {
    private lateinit var currentFragment: Fragment
    private val homeFragment = StudyGroupHomeFragment()
    private val func2Fragment = StudyGroupFunc2Fragment()
    private val func3Fragment = StudyGroupFunc3Fragment()
    private val func4Fragment = StudyGroupFunc4Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val actionBar = findViewById<Toolbar>(R.id.titleActionBar) // 액션바 설정
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 제거

        val titleTxt = intent.getStringExtra("studyGroupTitle") // title 정보 가져와서 출력
        val title = findViewById<TextView>(R.id.txtStudyGroupTitleIn)

        title.text = titleTxt

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.nav_view_drawer)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, actionBar,
            0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 네비게이션 드로어 메뉴 아이템 선택 시 처리할 리스너 등록
        navView.setNavigationItemSelectedListener { menuItem ->
            // 선택한 메뉴 아이템에 따라 처리
            when (menuItem.itemId) {
                R.id.nav_item1 -> {
                    // 첫 번째 메뉴 아이템 선택 시 수행할 작업
                    true
                }
                R.id.nav_item2 -> {
                    // 두 번째 메뉴 아이템 선택 시 수행할 작업
                    true
                }
                else -> false
            }
        }

        navigationInit()
        menuSetUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.study_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.manage -> {     // 관리 버튼을 누르면 관리 화면으로 이동
                val intent = Intent(this, StudyManageActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.goal -> {     // 목표 설정 화면 호출

                true
            }
            R.id.d_day -> {     // 디데이 설정 화면 호출

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigationInit() {
        currentFragment = homeFragment
        val fragmentList = listOf(homeFragment, func2Fragment, func3Fragment, func4Fragment)
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentList) {
            transaction.add(R.id.studyGroupContainerView, fragment)
        }
        for (fragment in listOf(func2Fragment, func3Fragment, func4Fragment)) {
            transaction.hide(fragment)
        }

        transaction.commit()
    }

    private fun menuSetUp() { // 상단 프래그먼트 메뉴 이동 버튼 설정
        val homeBtn = findViewById<CircleImageView>(R.id.homeGroup)
        val func2Btn = findViewById<CircleImageView>(R.id.func2Group)
        val func3Btn = findViewById<CircleImageView>(R.id.func3Group)
        val func4Btn = findViewById<CircleImageView>(R.id.func4Group)

        homeBtn.setOnClickListener {
            changeFragment(homeFragment)
        }
        func2Btn.setOnClickListener {
            changeFragment(func2Fragment)
        }
        func3Btn.setOnClickListener {
            changeFragment(func3Fragment)
        }
        func4Btn.setOnClickListener {
            changeFragment(func4Fragment)
        }
    }

    private fun changeFragment(fragment: Fragment) { // 프래그먼트 전환 함수
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