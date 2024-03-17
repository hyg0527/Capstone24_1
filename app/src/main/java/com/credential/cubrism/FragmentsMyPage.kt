package com.credential.cubrism

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    private lateinit var studyViewModel: MyStudyViewModel

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
        val itemList = ArrayList<String>()

        val recyclerView = view.findViewById<RecyclerView>(R.id.myStudyView)
        val adapter = MyStudyAdapter(itemList)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation) // 구분선 추가
        recyclerView.addItemDecoration(dividerItemDecoration)

        studyViewModel = ViewModelProvider(requireActivity())[MyStudyViewModel::class.java]
        updateViewModel(adapter)
    }

    private fun updateViewModel(adapter: MyStudyAdapter) {
        studyViewModel.studyList.observe(viewLifecycleOwner) { studyList ->
            adapter.clearItem()
            studyList.forEach { title ->
                adapter.addItem(title)
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
    private lateinit var myStudyListViewModel: MyStudyViewModel
    private lateinit var studyListViewModel: StudyListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        myStudyListViewModel = ViewModelProvider(requireActivity())[MyStudyViewModel::class.java]
        studyListViewModel = ViewModelProvider(requireActivity())[StudyListViewModel::class.java]

        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        val addBtn = view.findViewById<Button>(R.id.btnStudyCreate)

        backBtn.setOnClickListener { // 뒤로가기 버튼
            (parentFragment as MyPageFragment).childFragmentManager.popBackStack()
        }
        addBtn.setOnClickListener { // 스터디 등록 부분
            val title = view.findViewById<EditText>(R.id.editTextStudyTitle)
            myStudyListViewModel.addList(title.text.toString())
            studyListViewModel.addList(title.text.toString())

            hideKeyboard(requireContext(), view)
            Toast.makeText(requireContext(), "스터디를 등록하였습니다.", Toast.LENGTH_SHORT).show()
            (parentFragment as MyPageFragment).childFragmentManager.popBackStack()
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