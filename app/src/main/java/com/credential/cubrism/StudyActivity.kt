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
import com.google.android.material.navigation.NavigationView

class StudyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        val actionBar = findViewById<Toolbar>(R.id.titleActionBar) // 액션바 설정
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 제거

        val titleTxt = intent.getStringExtra("studyGroupTitle") // title 정보 가져와서 출력
        val title = findViewById<TextView>(R.id.txtStudyGroupTitleIn)

        title.text = titleTxt

        supportFragmentManager.beginTransaction()
            .replace(R.id.studyGroupContainerView, StudyGroupHomeFragment())
            .setReorderingAllowed(true)
            .commit()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.nav_view_drawer)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, actionBar,
            0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 네비게이션 메뉴 아이템 선택 시 처리할 리스너 등록
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.study_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.medal -> {     // 관리 버튼을 누르면 관리 화면으로 이동
                val intent = Intent(this, StudyManageActivity::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeFragment(fragment: Fragment) { // 프래그먼트 전환 함수
        supportFragmentManager.beginTransaction()
            .replace(R.id.studyGroupContainerView, fragment)
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .commit()
    }
}