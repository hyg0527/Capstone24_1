package com.credential.cubrism.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyManageBinding

class StudyManageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyManageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val titleTxt = intent.getStringExtra("titleName")
        val manageFragment = StudyGroupManageFragment()
        val bundle = Bundle()
        bundle.putString("titleName", titleTxt)
        manageFragment.arguments = bundle

        // 처음 화면을 fragment_study_home으로 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, manageFragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }

    fun changeFragmentManage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(binding.fragmentContainerView.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun popBackStackFragment() {
        supportFragmentManager.popBackStack()
    }
}