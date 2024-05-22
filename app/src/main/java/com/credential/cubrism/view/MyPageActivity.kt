package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMyPageBinding
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.NotiRepository
import com.credential.cubrism.view.adapter.MyPageAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val notificationRepository = NotiRepository(myApplication.getNotiDao())
    private val dataStore = myApplication.getDataStoreRepository()

    private val myPageAdapter = MyPageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        setupProfile()
    }

    private fun setupView() {
        // 정보 수정
        binding.layoutEdit.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // 나의 스터디
        binding.layoutStudy.setOnClickListener {
            startActivity(Intent(this, MyStudyListActivity::class.java))
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

    private fun setupProfile() {
        val isLoggedIn = myApplication.getUserData().getLoginStatus()
        if (isLoggedIn) {
            myApplication.getUserData().let {
                Glide.with(this@MyPageActivity).load(it.getProfileImage())
                    .error(R.drawable.profile)
                    .fallback(R.drawable.profile)
                    .dontAnimate()
                    .into(binding.imgProfile)
                binding.txtNickname.text = it.getNickname()
                binding.txtEmail.text = it.getEmail()
            }
        } else {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = myPageAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(this@MyPageActivity, 0, 16, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }

        myPageAdapter.setItemList(listOf(
            "내가 작성한 글",
            "관심 자격증 관리",
            "스터디 그룹 신청 내역"
        ))

        myPageAdapter.setOnItemClickListener { _, position ->
            when (position) {
                // 내가 작성한 글
                0 -> startActivity(Intent(this, MyPagePostActivity::class.java))
                // 관심 자격증 관리
                1 -> startActivity(Intent(this, MyPageCertManageActivity::class.java))
                // 스터디 그룹 신청 내역
                2 -> startActivity(Intent(this, MyPageStudyGroupApplyActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {
        authViewModel.apply {
            // 로그아웃
            logOut.observe(this@MyPageActivity) {
                lifecycleScope.launch {
                    dataStore.deleteAccessToken()
                    dataStore.deleteRefreshToken()
                    notificationRepository.deleteAllNoties()

                    myApplication.getUserData().apply {
                        setLoginStatus(false)
                        deleteUserData()
                    }

                    finish()
                }
            }

            errorMessage.observe(this@MyPageActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (message.lowercase().contains("jwt"))
                        finish()
                    else
                        Toast.makeText(this@MyPageActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}