package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentMypageBinding
import com.credential.cubrism.databinding.FragmentMypageHomeBinding
import com.credential.cubrism.model.dto.MyPageDto
import com.credential.cubrism.view.adapter.MyPageAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_mypage_home 으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, MyPageFragmentHome())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MyPageFragmentHome : Fragment() {
    private var _binding: FragmentMypageHomeBinding? = null
    private val binding get() = _binding!!

    private val myPageAdapter = MyPageAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMypageHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 수정 화면 출력
        binding.layoutEdit.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        // 나의 스터디 리스트 화면 출력
        binding.layoutStudy.setOnClickListener {
            startActivity(Intent(requireActivity(), MyStudyListActivity::class.java))
        }

        binding.layoutSchedule.setOnClickListener {

        }

        binding.recyclerView.apply {
            adapter = myPageAdapter
            addItemDecoration(ItemDecoratorDivider(0, 48, 0, 0,
                0, 0, null))
            setHasFixedSize(true)
        }

        myPageAdapter.setItemList(listOf(
            MyPageDto("내가 작성한 글"),
            MyPageDto("참여 중인 채팅방"),
            MyPageDto("Q&A 내역")
        ))

        myPageAdapter.setOnItemClickListener { _, position ->
            when (position) {
                0 -> { // 내가 작성한 글
//                    val intent = Intent(requireActivity(), OOOActivity::class.java)
//                    startActivity(intent)
                }
                1 -> { // 참여 중인 채팅방
//                    val intent = Intent(requireActivity(), OOOActivity::class.java)
//                    startActivity(intent)
                }
                2 -> { // Q&A 내역
//                    val intent = Intent(requireActivity(), OOOActivity::class.java)
//                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// fragment 전환 함수
private fun changeFragmentMyStudy(parentFragment: Fragment?, fragment: Fragment) {
    (parentFragment as MyPageFragment).childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
        .replace(R.id.fragmentContainerView, fragment)
        .addToBackStack(null)
        .commit()
}

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragmentManager: FragmentManager?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            parentFragmentManager?.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}