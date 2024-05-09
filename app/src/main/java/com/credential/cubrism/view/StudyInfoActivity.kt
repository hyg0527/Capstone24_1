package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyInfoBinding
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupTagAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class StudyInfoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyInfoBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val studyGroupTagAdapter by lazy { StudyGroupTagAdapter(2) }

    private val myEmail = MyApplication.getInstance().getUserData().getEmail()
    private val studyGroupId by lazy { intent.getIntExtra("studyGroupId", -1) }
    private var studyGroupName: String? = null
    private var isJoined = false

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

        binding.btnStudyJoin.setOnClickListener {
            if (isJoined) {
                if (studyGroupId != -1 && studyGroupName != null) {
                    val intent = Intent(this, StudyActivity::class.java)
                    intent.putExtra("studyGroupId", studyGroupId)
                    intent.putExtra("studyGroupName", studyGroupName)
                    startActivity(intent)
                }
            } else {
                studyGroupViewModel.requestJoin(studyGroupId)
                binding.progressIndicator.visibility = View.VISIBLE
            }
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
        studyGroupViewModel.apply {
            studyGroupInfo.observe(this@StudyInfoActivity) { group ->
                Glide.with(this@StudyInfoActivity).load(group.adminProfileImage)
                    .error(R.drawable.profile_skyblue)
                    .fallback(R.drawable.profile_skyblue)
                    .dontAnimate()
                    .into(binding.imgProfile)
                binding.txtNickname.text = "  ${group.groupAdmin}  "
                binding.txtGroupName.text = group.groupName
                binding.txtGroupDescription.text = group.groupDescription
                binding.txtMember.text = "${group.currentMembers} / ${group.maxMembers}"
                binding.btnStudyJoin.apply {
                    lifecycleScope.launch {
                        if (group.members.contains(myEmail)) {
                            text = "스터디 그룹 입장"
                            isEnabled = true
                            isJoined = true
                        } else {
                            isEnabled = group.recruiting
                            text = if (group.recruiting) "가입 요청" else "모집 완료"
                            background = if (group.recruiting)
                                ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_skyblue2, null)
                            else
                                ResourcesCompat.getDrawable(resources, R.drawable.button_rounded_corner_gray2, null)
                            isJoined = false
                        }
                    }
                }
                studyGroupTagAdapter.setItemList(group.tags)
                studyGroupName = group.groupName
            }

            requestJoin.observe(this@StudyInfoActivity) {
                Toast.makeText(this@StudyInfoActivity, "가입 요청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                binding.progressIndicator.visibility = View.GONE
            }

            errorMessage.observe(this@StudyInfoActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyInfoActivity, message, Toast.LENGTH_SHORT).show()
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }
}