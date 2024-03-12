package com.credential.cubrism

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class StudyFragment : Fragment(R.layout.fragment_study) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_study_home으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.studyFragmentContainerView, StudyHomeFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class StudyHomeFragment : Fragment(R.layout.fragment_study_home) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val itemList = ArrayList<String>().apply {
            for (i in 1..7)
                add("스터디 모집글 $i")
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.studyListView)
        val adapter = StudyListAdapter(itemList)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
        recyclerView.addItemDecoration(divider)

        adapter.setItemClickListener(object: StudyItemClickListener {
            override fun onItemClicked(item: String) {
                val infoFragment = StudyInfoFragment()
                changeFragmentStudy(infoFragment, "studyInfo")
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }

    private fun changeFragmentStudy(fragment: Fragment, tag: String) {
        (parentFragment as StudyFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.studyFragmentContainerView, fragment, tag)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }
}

class StudyInfoFragment : Fragment(R.layout.fragment_study_info) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

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

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as StudyFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}
