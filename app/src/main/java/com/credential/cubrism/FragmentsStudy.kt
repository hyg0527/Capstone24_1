package com.credential.cubrism

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var studyListViewModel: StudyListViewModel
    private lateinit var adapter: StudyListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<StudyList>()
        val switch = view.findViewById<Switch>(R.id.switch1)
        val recyclerView = view.findViewById<RecyclerView>(R.id.studyListView)
        adapter = StudyListAdapter(itemList, false)

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
        recyclerView.addItemDecoration(divider)

        studyListViewModel = ViewModelProvider(requireActivity())[StudyListViewModel::class.java]
        updateViewModel(adapter)

        switch.thumbDrawable.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.MULTIPLY)

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switch.thumbDrawable.setColorFilter(resources.getColor(R.color.blue), PorterDuff.Mode.MULTIPLY)
            } else {
                switch.thumbDrawable.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.MULTIPLY)
            }
        }

        adapter.setItemClickListener(object: StudyItemClickListener {
            override fun onItemClicked(item: StudyList) {
                val infoFragment = StudyInfoFragment()
                changeFragmentStudy(infoFragment, "studyInfo")
            }
        })
    }

    private fun updateViewModel(adapter: StudyListAdapter) {
        studyListViewModel.studyList.observe(viewLifecycleOwner) { studyList ->
            adapter.clearItem()
            studyList.forEach { item ->
                adapter.addItem(item)
            }
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            adapter.updateItem()
        }
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
