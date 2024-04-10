package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMyPageBinding
import com.credential.cubrism.model.dto.MyPageDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.MyPageAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.CalendarViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()
    private val calendarViewModel: CalendarViewModel by viewModels()

    private val myPageAdapter = MyPageAdapter()

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "내 정보를 수정했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupView() {
        lifecycleScope.launch {
            Glide.with(this@MyPageActivity).load(dataStore.getProfileImage().first())
                .error(R.drawable.account_circle)
                .fallback(R.drawable.account_circle)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.txtEmail.text = dataStore.getEmail().first()
            binding.txtNickname.text = dataStore.getNickname().first()
        }

        // 정보 수정
        binding.layoutEdit.setOnClickListener {
            startForRegisterResult.launch(Intent(this, EditProfileActivity::class.java))
        }

        // 나의 스터디
        binding.layoutStudy.setOnClickListener {
            val intent = Intent(this, MyStudyListActivity::class.java)
            intent.putExtra("myScheduleList", calendarViewModel.calMonthList.value)
            startActivity(intent)
        }

        // 나의 일정
        binding.layoutSchedule.setOnClickListener {
            startActivity(Intent(this, MyScheduleListActivity::class.java))
        }

        // 로그아웃
        binding.btnLogout.setOnClickListener {
            authViewModel.logOut()
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

    private fun observeViewModel() {
        authViewModel.logOut.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    lifecycleScope.launch {
                        dataStore.deleteAccessToken()
                        dataStore.deleteRefreshToken()
                        dataStore.deleteEmail()
                        dataStore.deleteNickname()
                        dataStore.deleteProfileImage()
                    }
                    setResult(RESULT_OK).also { finish() }
                }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}