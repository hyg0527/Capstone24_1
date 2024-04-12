package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentQualificationBinding
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.view.adapter.MajorFieldAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationFragment : Fragment() {
    private var _binding: FragmentQualificationBinding? = null
    private val binding get() = _binding!!

    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val majorFieldAdapter = MajorFieldAdapter()
    private lateinit var gridLayoutmanager: GridLayoutManager

    private lateinit var searchView: SearchView
    private var searchQuery: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationBinding.inflate(inflater, container, false)

        setupToolbar()
        setupRecyclerView()
        setupView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        (activity as AppCompatActivity).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchItem = menu.findItem(R.id.search)
                searchView = searchItem.actionView as SearchView

                searchView.queryHint = "자격증 검색"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchQuery = query
                        searchView.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupView() {
        binding.progressIndicator.show()
        qualificationViewModel.getMajorFieldList()
    }

    private fun setupRecyclerView() {
        gridLayoutmanager = GridLayoutManager(context, 3)
        binding.recyclerView.apply {
            layoutManager = gridLayoutmanager
            adapter = majorFieldAdapter
            addItemDecoration(ItemDecoratorDivider(0, 48, 48, 48, 0, 0, null))
            setHasFixedSize(true)
        }

        majorFieldAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(requireContext(), QualificationMiddleActivity::class.java)
            intent.putExtra("majorFieldName", item.majorFieldName)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        qualificationViewModel.apply {
            majorFieldList.observe(viewLifecycleOwner) { result ->
                binding.progressIndicator.hide()
                majorFieldAdapter.setItemList(result)
            }

            errorMessage.observe(viewLifecycleOwner) { message ->
                message.getContentIfNotHandled()?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

//class QualificationSearchFragment : Fragment() {
//    private var _binding: FragmentQualificationSearchBinding? = null
//    private val binding get() = _binding!!
//
//    private val viewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
//    private lateinit var linearLayoutManager: LinearLayoutManager
//    private val qualificationAdapter = QualificationAdapter()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentQualificationSearchBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    private var sView: View? = null
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.progressIndicator.show()
//
//        linearLayoutManager = LinearLayoutManager(context)
//        binding.recyclerView.apply {
//            layoutManager = linearLayoutManager
//            adapter = qualificationAdapter
//            itemAnimator = null
//            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.GRAY))
//            setHasFixedSize(true)
//        }
//
//        viewModel.getQualificationList()
//        viewModel.qualificationList.observe(viewLifecycleOwner) { result ->
//            binding.progressIndicator.hide()
//            when (result) {
//                is ResultUtil.Success -> { qualificationAdapter.setItemList(result.data) }
//                is ResultUtil.Error -> { Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show() }
//                is ResultUtil.NetworkError -> { Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
//            }
//        }
//
//        qualificationAdapter.setOnItemClickListener { item, _ ->
//            showInfoFragmentSearch(item.name, item.code)
//            hideKeyboard(binding.root)
//        }
//
//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                binding.searchView.clearFocus()
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                qualificationAdapter.filter.filter(newText)
//                return false
//            }
//        })
//        // SearchView의 닫기 버튼 리스너 설정
//        binding.searchView.setOnCloseListener(object : SearchView.OnCloseListener {
//            override fun onClose(): Boolean {
//                handleBackStack(view, parentFragment)
//                return false
//            }
//        })
//
//        binding.btnBack.setOnClickListener {
//            (parentFragment as QualificationFragment).childFragmentManager.popBackStack()
//        }
//
//        // 뒤로가기 이벤트 처리
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // SearchView의 Close 버튼 이벤트를 호출하여 "x" 표시를 누름
//                binding.searchView.onActionViewCollapsed()
//                handleBackStack(view, parentFragment)
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//
//        sView = view
//        handleBackStack(view, parentFragment)
//        binding.searchView.clearFocus()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onHiddenChanged(hidden: Boolean) {
//        super.onHiddenChanged(hidden)
//
//        if (!hidden) {
//            // Fragment가 다시 화면에 나타날 때의 작업 수행
//            sView?.let { handleBackStack(it, parentFragment) }
//        }
//    }
//
//    private fun showInfoFragmentSearch(qualificationName: String, qualificationCode: String) {
//        val fragment = QualificationDetailsFragment()
//        val bundle = Bundle()
//        bundle.putString("qualificationName", qualificationName)
//        bundle.putString("qualificationCode", qualificationCode)
//        fragment.arguments = bundle
//
//        (parentFragment as QualificationFragment).childFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
//            .replace(R.id.fragmentContainerView, fragment)
//            .addToBackStack(null)
//            .commit()
//    }
//
//    // 뷰에 포커스를 주고 키보드를 숨기는 함수
//    private fun hideKeyboard(view: View) {
//        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }
//}

// 백스택 호출 함수 선언 (뒤로 가기)
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as QualificationFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}