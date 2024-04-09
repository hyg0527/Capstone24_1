package com.credential.cubrism.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyInfoBinding
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.StudyGroupTagAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyInfoBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val studyGroupTagAdapter by lazy { StudyGroupTagAdapter(2) }

    private val studyGroupId by lazy { intent.getIntExtra("studyGroupId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        if (studyGroupId != -1) {
            studyGroupViewModel.getStudyGroupInfo(studyGroupId)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = studyGroupTagAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 20, 0, 20, 0, 0, 0))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.studyGroupInfo.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    val groupInfo = result.data
                    Glide.with(this).load(groupInfo.adminProfileImage)
                        .error(R.drawable.profile_skyblue)
                        .fallback(R.drawable.profile_skyblue)
                        .dontAnimate()
                        .into(binding.imgProfile)
                    binding.txtNickname.text = "  ${groupInfo.groupAdmin}  "
                    binding.txtGroupName.text = groupInfo.groupName
                    binding.txtGroupDescription.text = groupInfo.groupDescription
                    binding.txtMember.text = "${groupInfo.currentMembers} / ${groupInfo.maxMembers}"
                    binding.btnStudyJoin.apply {
                        isEnabled = groupInfo.recruiting
                        text = if (groupInfo.recruiting) "가입 요청" else "모집 완료"
                        background = if (groupInfo.recruiting) ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_skyblue2, null) else ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_gray2, null)
                    }
                    studyGroupTagAdapter.setItemList(groupInfo.tags)
                }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}