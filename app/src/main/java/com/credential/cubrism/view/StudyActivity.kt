package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyBinding

class StudyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyBinding.inflate(layoutInflater) }

    private lateinit var currentFragment: Fragment
    private val homeFragment = StudyGroupHomeFragment()
    private val func2Fragment = StudyGroupFunc2Fragment()
    private val func3Fragment = StudyGroupFunc3Fragment()
    private val func4Fragment = StudyGroupFunc4Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 제거

        intent.getStringExtra("studyGroupTitle")?.let { title ->
            binding.txtTitle.text = title
        }

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 네비게이션 드로어 메뉴 아이템 선택 시 처리할 리스너 등록
        binding.navigation.setNavigationItemSelectedListener { menuItem ->
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
                intent.putExtra("titleName", binding.txtTitle.text.toString())
                startActivity(intent)
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
            transaction.add(binding.fragmentContainerView.id, fragment)
        }
        for (fragment in listOf(func2Fragment, func3Fragment, func4Fragment)) {
            transaction.hide(fragment)
        }

        transaction.commit()
    }

    private fun menuSetUp() { // 상단 프래그먼트 메뉴 이동 버튼 설정
        binding.homeGroup.setOnClickListener {
            changeFragment(homeFragment)
        }
        binding.func2Group.setOnClickListener {
            changeFragment(func2Fragment)
        }
        binding.func3Group.setOnClickListener {
            changeFragment(func3Fragment)
        }
        binding.func4Group.setOnClickListener {
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