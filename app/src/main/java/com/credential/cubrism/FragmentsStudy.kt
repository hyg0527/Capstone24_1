package com.credential.cubrism

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Switch
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
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
                val bundle = Bundle()
                val infoFragment = StudyInfoFragment()

                bundle.putParcelable("studyGroupInfo", item)
                infoFragment.arguments = bundle

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
        val item = arguments?.getParcelable<StudyList>("studyGroupInfo")

        val joinBtn = view.findViewById<Button>(R.id.btnStudyJoin)
        if (item?.totalNum == 4) joinBtn.text = "가입하기"
        else {
            joinBtn.text = "가입완료"
            joinBtn.setBackgroundResource(R.drawable.button_rounded_corner_gray2)

            val resolvedColor = ContextCompat.getColor(requireContext(), R.color.black)
            joinBtn.setTextColor(resolvedColor)
            joinBtn.isEnabled = false
        }

        joinBtn.setOnClickListener {
            Toast.makeText(requireContext(), "가입 요청이 전달되었습니다.", Toast.LENGTH_SHORT).show()
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
