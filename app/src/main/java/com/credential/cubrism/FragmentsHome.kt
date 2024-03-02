package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            changeFragment(parentFragment, NotifyFragment())
        }

        val qnaEnter = view.findViewById<Button>(R.id.btnQnaEnter)
        qnaEnter.setOnClickListener {
            changeFragment(parentFragment, QnaFragment())
        }
    }
}
// 알림 화면
class NotifyFragment : Fragment(R.layout.fragment_home_notification) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val backBtn = view.findViewById<ImageButton>(R.id.backBtnNotify)
        backBtn.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}
// Qna 메인 화면
class QnaFragment : Fragment(R.layout.fragment_qna) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        changeFragmentInQna(QnaTotalListFragment()) // 처음 TotalList 화면으로 초기화

        val btnAddPost = view.findViewById<ImageView>(R.id.btnAddPost)
        val totalBtn = view.findViewById<TextView>(R.id.textView6)
        val wholeBtn = view.findViewById<TextView>(R.id.textView7)

        btnAddPost.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
                .replace(R.id.homeFragmentContainerView, QnawriteFragment())
                .addToBackStack(null)
                .commit()
        }

        totalBtn.setOnClickListener {
            changeTotalOrWhole("total")
        }
        wholeBtn.setOnClickListener {
            changeTotalOrWhole("whole")
        }

        handleBackStack(view, parentFragment)
    }

    private fun changeTotalOrWhole(value: String) { // 리스트 표시 부분 변경 함수
        if (value.equals("total")) {
            changeFragmentInQna(QnaTotalListFragment())
        }
        else if (value.equals("whole")) {
            changeFragmentInQna(QnaWholeListFragment())
        }
        else return
    }
    // QnaFragment 내부 전환 함수
    private fun changeFragmentInQna(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.qnaTotalList, fragment)
            .commit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}

class QnaTotalListFragment : Fragment(R.layout.fragment_qna_total_post) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rcv = view.findViewById<RecyclerView>(R.id.qnaListView)
        val postList = ArrayList<QnaData>().apply {
            add(QnaData("제목", R.drawable.qna_photo, "", ""))
        }

        rcv.layoutManager = LinearLayoutManager(requireActivity())
        rcv.adapter = QnaAdapter(postList)
    }
}

class QnaWholeListFragment : Fragment(R.layout.fragment_whole_qna_post) {

}

class QnawriteFragment : Fragment(R.layout.fragment_qna_posting) { // 글등록 프래그먼트
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val postingBtn = view.findViewById<Button>(R.id.postingBtn)

        postingBtn.setOnClickListener {
            Toast.makeText(requireContext(), "질문이 등록되었습니다 어쩔", Toast.LENGTH_SHORT).show()
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}

// fragment 전환 함수
private fun changeFragment(parentFragment: Fragment?, fragment: Fragment) {
    (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
        .replace(R.id.homeFragmentContainerView, fragment)
        .addToBackStack(null)
        .commit()
}

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}