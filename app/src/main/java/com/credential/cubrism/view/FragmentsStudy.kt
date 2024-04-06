package com.credential.cubrism.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.credential.cubrism.databinding.FragmentStudyBinding
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyFragment : Fragment() {
    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }
    private val studyGroupAdapter = StudyGroupAdapter()

    private var loadingState = false
    private var isRecruiting = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = studyGroupAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 80, Color.parseColor("#E0E0E0")))
        }

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (scrollY >= v.getChildAt(0).measuredHeight - v.measuredHeight) {
                // 스크롤을 끝까지 내렸을 때
                if (!loadingState) {
                    viewModel.page.value?.let { page ->
                        // 다음 페이지가 존재하면 다음 페이지 데이터를 가져옴
                        page.nextPage?.let { viewModel.getStudyGroupList(it, 5, isRecruiting) }
                    }
                }
            }
        })

        studyGroupAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(requireContext(), StudyInfoActivity::class.java)
            intent.putExtra("studyGroupId", item.studyGroupId)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getStudyGroupList(0, 5, isRecruiting, true)
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun setupView() {
        binding.switchRecruiting.setOnCheckedChangeListener { _, isChecked ->
            isRecruiting = isChecked
            viewModel.getStudyGroupList(0, 5, isRecruiting, true)
            binding.swipeRefreshLayout.isRefreshing = true
            binding.scrollView.scrollTo(0, 0)
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            getStudyGroupList(0, 5, isRecruiting)
            binding.swipeRefreshLayout.isRefreshing = true

            studyGroupList.observe(viewLifecycleOwner) {
                studyGroupAdapter.setItemList(it ?: emptyList())
                binding.swipeRefreshLayout.isRefreshing = false
            }
            isLoading.observe(viewLifecycleOwner) {
                studyGroupAdapter.setLoading(it)
                loadingState = it
            }
            errorMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}