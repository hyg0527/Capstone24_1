package com.credential.cubrism

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.os.IResultReceiver._Parcel
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import de.hdodenhof.circleimageview.CircleImageView


class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_mypage_home 으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.myPageFragmentContainerView, MyPageFragmentHome())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class MyPageFragmentHome : Fragment(R.layout.fragment_mypage_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileFix = view.findViewById<CircleImageView>(R.id.circle1)
        val myStudy = view.findViewById<CircleImageView>(R.id.circle2)

        profileFix.setOnClickListener { // 프로필 수정 화면 출력
            val intent = Intent(requireActivity(), ProfileFixActivity::class.java)
            startActivity(intent)
        }
        myStudy.setOnClickListener { // 나의 스터디 리스트 화면 출력
            changeFragmentMyStudy(parentFragment, MyPageFragmentMyStudy())
        }

    }
}

class MyPageFragmentMyStudy : Fragment(R.layout.fragment_mypage_mystudy) {
    private var view: View? = null
    private lateinit var studyViewModel: StudyListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        showStudyList(view)

        val addStudyGroup = view.findViewById<ImageView>(R.id.btnAddStudyGroup)
        addStudyGroup.setOnClickListener { // 스터디그룹 만들기 화면으로 이동
            changeFragmentMyStudy(parentFragment, MyPageCreateStudyFragment())
        }

        handleBackStack(view, parentFragment)
    }

    private fun showStudyList(view: View) { // 스터디 리스트 출력
        val itemList = ArrayList<StudyList>()

        val recyclerView = view.findViewById<RecyclerView>(R.id.myStudyView)
        val adapter = StudyListAdapter(itemList, true)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation) // 구분선 추가
        recyclerView.addItemDecoration(dividerItemDecoration)

        studyViewModel = ViewModelProvider(requireActivity())[StudyListViewModel::class.java]
        updateViewModel(adapter)
    }

    private fun updateViewModel(adapter: StudyListAdapter) {
        studyViewModel.studyList.observe(viewLifecycleOwner) { studyList ->
            adapter.clearItem()
            studyList.forEach { item ->
                adapter.addItem(item)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}

class MyPageCreateStudyFragment : Fragment(R.layout.fragment_mypage_addstudy) {
    private var view: View? = null
    private lateinit var studyListViewModel: StudyListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        studyListViewModel = ViewModelProvider(requireActivity())[StudyListViewModel::class.java]

        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        val addBtn = view.findViewById<Button>(R.id.btnStudyCreate)

        val items = initTagRecyclerView(view)

        backBtn.setOnClickListener { // 뒤로가기 버튼
            (parentFragment as MyPageFragment).childFragmentManager.popBackStack()
        }
        addBtn.setOnClickListener { // 스터디 등록 부분
            submitButtonPressed(view, items)
        }

        handleBackStack(view, parentFragment)
    }

    private fun initTagRecyclerView(v: View): ArrayList<Tags> {
        val itemList = listOf("#널널함", "#열공", "#일주일", "#자유롭게", "#한달", "#필참", "#뭐가", "#있지", "#음..", "#알아서", "#없음")
        val items = ArrayList<Tags>().apply {
            for (item in itemList)
                add(Tags(item))
        }

        val recyclerView = v.findViewById<RecyclerView>(R.id.tagRecyclerView)
        val adapter = TagAdapter(items, true)

        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        recyclerView.adapter = adapter

        return items
    }

    private fun submitButtonPressed(view: View, items: ArrayList<Tags>) { // 가입하기 버튼 눌렀을 때 처리하는 함수
        val title = view.findViewById<EditText>(R.id.editTextStudyTitle).text
        val info = view.findViewById<EditText>(R.id.editTextStudyInfo).text.toString()
        val numS = view.findViewById<EditText>(R.id.editTextNums).text.toString()
        val numSInt = if (numS.isNotEmpty()) {
            numS.toInt()
        } else 0 // 기본값은 0으로 설정

        val tagS = ArrayList<Tags>() // 선택한 태그만 데이터에 ArrayList타입으로 추가
        for (item in items)
            if (item.isEnabled) tagS.add(item)

        when {
            (title.isEmpty()) -> Toast.makeText(requireContext(), "스터디그룹명을 입력하세요.", Toast.LENGTH_SHORT).show()
            tagS.size <= 0 -> Toast.makeText(requireContext(), "태그를 한 개 이상 선택하세요.", Toast.LENGTH_SHORT).show()
            (numS.isEmpty()) -> Toast.makeText(requireContext(), "스터디그룹 인원 수를 입력하세요.", Toast.LENGTH_SHORT).show()
            (numSInt <= 1) -> Toast.makeText(requireContext(), "스터디그룹 인원 수를 2명 이상 입력하세요.", Toast.LENGTH_SHORT).show()
            (info.isEmpty()) -> Toast.makeText(requireContext(), "스터디그룹 설명을 입력하세요.", Toast.LENGTH_SHORT).show()
            else -> {
                studyListViewModel.addList(StudyList(title.toString(), info, tagS, numSInt, 1))

                hideKeyboard(requireContext(), view)
                Toast.makeText(requireContext(), "스터디를 등록하였습니다.", Toast.LENGTH_SHORT).show()
                (parentFragment as MyPageFragment).childFragmentManager.popBackStack()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }

    // 뷰에 포커스를 주고 키보드를 숨기는 함수
    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

// fragment 전환 함수
private fun changeFragmentMyStudy(parentFragment: Fragment?, fragment: Fragment) {
    (parentFragment as MyPageFragment).childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
        .replace(R.id.myPageFragmentContainerView, fragment)
        .addToBackStack(null)
        .commit()
}

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as MyPageFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}