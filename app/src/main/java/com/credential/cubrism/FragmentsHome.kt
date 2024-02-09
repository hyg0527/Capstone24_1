package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment


class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_home_ui로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.homeFragmentContainerView, HomeUiFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class HomeUiFragment : Fragment(R.layout.fragment_home_ui) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val login = view.findViewById<LinearLayout>(R.id.loginBtnLayout)
        val notify = view.findViewById<ImageButton>(R.id.btnNotify)

        login.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
        notify.setOnClickListener { // 알림 화면 출력
            (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
                .replace(R.id.homeFragmentContainerView, NotifyFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}

class NotifyFragment : Fragment(R.layout.fragment_home_notification) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnNotify)

        backBtn.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }
    }
}