package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentMypageBinding
import com.credential.cubrism.databinding.FragmentMypageHomeBinding
import com.credential.cubrism.model.dto.MyPageDto
import com.credential.cubrism.view.adapter.MyPageAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.CalendarViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    private val dataStore = MyApplication.getInstance().getDataStoreRepository()
    private val calendarViewModel: CalendarViewModel by activityViewModels()

    private val myPageAdapter = MyPageAdapter()

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "내 정보를 수정했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMypageHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            lifecycleScope.launch {
                Glide.with(requireContext()).load(dataStore.getProfileImage().first())
                    .error(R.drawable.account_circle)
                    .fallback(R.drawable.account_circle)
                    .dontAnimate()
                    .into(binding.imgProfile)
                binding.txtEmail.text = dataStore.getEmail().first()
                binding.txtNickname.text = dataStore.getNickname().first()
            }
        }
    }

    private fun setupView() {
        // 정보 수정
        binding.layoutEdit.setOnClickListener {
            startForRegisterResult.launch(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        // 나의 스터디
        binding.layoutStudy.setOnClickListener {
            val intent = Intent(requireActivity(), MyStudyListActivity::class.java)
            intent.putExtra("myScheduleList", calendarViewModel.calMonthList.value)
            startActivity(intent)
        }

        // 나의 일정
        binding.layoutSchedule.setOnClickListener {
            startActivity(Intent(requireActivity(), MyScheduleListActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = myPageAdapter
            addItemDecoration(ItemDecoratorDivider(0, 48, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }

        myPageAdapter.setItemList(listOf(
            MyPageDto("내가 작성한 글"),
            MyPageDto("참여 중인 채팅방"),
            MyPageDto("Q&A 내역")
        ))

        myPageAdapter.setOnItemClickListener { _, position ->
//            when (position) {
//                // 내가 작성한 글
//                0 -> startActivity(Intent(requireActivity(), OOOActivity::class.java))
//                // 참여 중인 채팅방
//                1 -> startActivity(Intent(requireActivity(), OOOActivity::class.java))
//                // Q&A 내역
//                2 -> startActivity(Intent(requireActivity(), OOOActivity::class.java))
//            }
        }
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