package com.credential.cubrism

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class StudyManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_manage)

        val titleTxt = intent.getStringExtra("titleName")
        val manageFragment = StudyGroupManageFragment()
        val bundle = Bundle()
        bundle.putString("titleName", titleTxt)
        manageFragment.arguments = bundle

        // 처음 화면을 fragment_study_home으로 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.studyGroupManageContainerView, manageFragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }

    fun changeFragmentManage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.studyGroupManageContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun popBackStackFragment() {
        supportFragmentManager.popBackStack()
    }
}