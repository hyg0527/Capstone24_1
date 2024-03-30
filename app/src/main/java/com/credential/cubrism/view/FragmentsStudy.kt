package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentStudyBinding
import com.credential.cubrism.databinding.FragmentStudyHomeBinding
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.StudyGroupAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class StudyFragment : Fragment() {
    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_study_home으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, StudyHomeFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class StudyHomeFragment : Fragment() {
    private var _binding: FragmentStudyHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }
    private val studyGroupAdapter = StudyGroupAdapter()

    private var loadingState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = studyGroupAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 80, Color.parseColor("#E0E0E0")))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)

                    // 스크롤을 끝까지 내렸을 때
                    if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !loadingState) {
                        viewModel.page.value?.let { page ->
                            // 다음 페이지가 존재하면 다음 페이지 데이터를 가져옴
                            page.nextPage?.let { viewModel.getStudyGroupList(it, 5) }
                        }
                    }
                }
            })
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getStudyGroupList(0, 5, true)
            binding.swipeRefreshLayout.isRefreshing = true
        }

        viewModel.apply {
            getStudyGroupList(0, 5)
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

        studyGroupAdapter.setOnItemClickListener { item, _ ->
            // TODO: 스터디 가입 요청 화면으로 이동
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun changeFragmentStudy(fragment: Fragment, tag: String) {
        (parentFragment as StudyFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.fragmentContainerView, fragment, tag)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class StudyInfoFragment : Fragment(R.layout.fragment_study_info) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as StudyFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}