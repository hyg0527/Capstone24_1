package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
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
import com.credential.cubrism.viewmodel.CalendarViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPageBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()
    private val notificationRepository = NotiRepository(MyApplication.getInstance().getNotiDao())

    private val myPageAdapter = MyPageAdapter()

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.getUserInfo()
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
                    dataStore.deleteEmail()
                    dataStore.deleteNickname()
                    dataStore.deleteProfileImage()
                    notificationRepository.deleteAllNoties()

                    setResult(RESULT_OK).also { finish() }
                }
            }

            // 유저 정보
            getUserInfo.observe(this@MyPageActivity) { user ->
                lifecycleScope.launch {
                    dataStore.apply {
                        saveEmail(user.email)
                        saveNickname(user.nickname)
                        saveProfileImage(user.profileImage ?: "")
                    }
                }
            }

            errorMessage.observe(this@MyPageActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (message == "JWT 토큰이 잘못되었습니다.")
                        setResult(RESULT_OK).also { finish() }
                }
            }
        }

        dataStore.apply {
            getEmail().asLiveData().observe(this@MyPageActivity) { email ->
                binding.txtEmail.text = email
            }

            getNickname().asLiveData().observe(this@MyPageActivity) { nickname ->
                binding.txtNickname.text = nickname
            }

            getProfileImage().asLiveData().observe(this@MyPageActivity) { image ->
                Glide.with(this@MyPageActivity).load(image)
                    .error(R.drawable.profile)
                    .fallback(R.drawable.profile)
                    .dontAnimate()
                    .into(binding.imgProfile)
            }
        }
    }
}