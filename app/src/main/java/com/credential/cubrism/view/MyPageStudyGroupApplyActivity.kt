package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMypageApplyhistoryBinding
import com.credential.cubrism.databinding.DialogMenuBinding
import com.credential.cubrism.model.dto.MenuDto
import com.credential.cubrism.model.dto.StudyGroupJoinListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.MenuAdapter
import com.credential.cubrism.view.adapter.StudyGroupJoinAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyPageStudyGroupApplyActivity : AppCompatActivity(), StudyGroupJoinAdapter.OnViewClickListener {
    private val binding by lazy { ActivityMypageApplyhistoryBinding.inflate(layoutInflater) }

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupJoinAdapter: StudyGroupJoinAdapter
    private val menuAdapter = MenuAdapter()

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var memberId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupBottomSheetDialog()
        setupRecyclerView()
        observeViewModel()

        studyGroupViewModel.getStudyGroupJoinRequestList()
    }

    override fun setOnViewClick(item: StudyGroupJoinListDto) {
        memberId = item.memberId
        bottomSheetDialog.show()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogMenuBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        val menuList: List<MenuDto> = listOf(
            MenuDto(R.drawable.icon_close, "가입 신청 취소"),
        )

        menuAdapter.setItemList(menuList)

        bottomSheetBinding.recyclerView.apply {
            adapter = menuAdapter
            itemAnimator = null
            setHasFixedSize(true)
        }

        menuAdapter.setOnItemClickListener { item, _ ->
            when (item.text) {
                "가입 신청 취소" -> {
                    memberId?.let {
                        AlertDialog.Builder(this).apply {
                            setMessage("가입 신청을 취소하시겠습니까?")
                            setNegativeButton("취소", null)
                            setPositiveButton("확인") { _, _ ->
                                studyGroupViewModel.cancelJoin(it)
                            }
                            show()
                        }
                    }
                }
            }
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupRecyclerView() {
        studyGroupJoinAdapter = StudyGroupJoinAdapter(this)

        binding.recyclerView.apply {
            adapter = studyGroupJoinAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.parseColor("#E0E0E0")))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            joinRequestList.observe(this@MyPageStudyGroupApplyActivity) {
                binding.progressIndicator.hide()
                studyGroupJoinAdapter.setItemList(it)

                binding.txtNoJoin.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }

            cancelJoin.observe(this@MyPageStudyGroupApplyActivity) {
                Toast.makeText(this@MyPageStudyGroupApplyActivity, it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getStudyGroupJoinRequestList()
            }

            errorMessage.observe(this@MyPageStudyGroupApplyActivity) {
                it.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyPageStudyGroupApplyActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}