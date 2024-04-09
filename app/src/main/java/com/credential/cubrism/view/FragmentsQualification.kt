package com.credential.cubrism.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentQualificationBinding
import com.credential.cubrism.databinding.FragmentQualificationDetailsBinding
import com.credential.cubrism.databinding.FragmentQualificationMajorfieldBinding
import com.credential.cubrism.databinding.FragmentQualificationMiddlefieldBinding
import com.credential.cubrism.databinding.FragmentQualificationSearchBinding

import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.MajorFieldAdapter
import com.credential.cubrism.view.adapter.MiddleFieldAdapter
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QualificationFragment : Fragment() {
    private var _binding: FragmentQualificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, MajorFieldFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MajorFieldFragment : Fragment() {
    private var _binding: FragmentQualificationMajorfieldBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private lateinit var gridLayoutmanager: GridLayoutManager
    private val majorFieldAdapter = MajorFieldAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationMajorfieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressIndicator.show()

        gridLayoutmanager = GridLayoutManager(context, 3)
        binding.recyclerView.apply {
            layoutManager = gridLayoutmanager
            adapter = majorFieldAdapter
            addItemDecoration(ItemDecoratorDivider(0, 80, 48, 48, 0, 0, null))
            setHasFixedSize(true)

            setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY) {
                    binding.floatingActionButton.hide()
                } else {
                    binding.floatingActionButton.show()
                }
            }
        }

        viewModel.getMajorFieldList()
        viewModel.majorFieldList.observe(viewLifecycleOwner) { result ->
            binding.progressIndicator.hide()
            when (result) {
                is ResultUtil.Success -> { majorFieldAdapter.setItemList(result.data) }
                is ResultUtil.Error -> { Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        majorFieldAdapter.setOnItemClickListener { item, _ ->
            showMiddleFieldFragment(item.majorFieldName)
        }

        binding.floatingActionButton.setOnClickListener {
            showSearchFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showMiddleFieldFragment(majorFieldName: String) {
        val fragment = MiddleFieldFragment()
        val bundle = Bundle()
        bundle.putString("majorFieldName", majorFieldName)
        fragment.arguments = bundle

        (parentFragment as QualificationFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSearchFragment() {
        (parentFragment as QualificationFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.fragmentContainerView, QualificationSearchFragment())
            .addToBackStack(null)
            .commit()
    }
}

class MiddleFieldFragment : Fragment() {
    private var _binding: FragmentQualificationMiddlefieldBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val middleFieldAdapter = MiddleFieldAdapter()

    private val majorFieldName by lazy { arguments?.getString("majorFieldName") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationMiddlefieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var sView: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtMajorField.text = majorFieldName
        binding.progressIndicator.show()

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = middleFieldAdapter
            addItemDecoration(ItemDecoratorDivider(0, 64, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }

        majorFieldName?.let {
            viewModel.getMiddleFieldList(it)
        }

        viewModel.middleFieldList.observe(viewLifecycleOwner) { result ->
            binding.progressIndicator.hide()
            when (result) {
                is ResultUtil.Success -> {
                    middleFieldAdapter.setItemList(result.data)
                }
                is ResultUtil.Error -> { Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        middleFieldAdapter.setOnItemClickListener { item, _ ->
            showDetailsFragment(item.name, item.code)
        }

        handleBackStack(binding.root, parentFragment)

        // 뒤로가기 버튼도 동일하게 popstack
        binding.btnBack.setOnClickListener {
            (parentFragment as QualificationFragment).childFragmentManager.popBackStack()
        }

        sView = view
        handleBackStack(view, parentFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            sView?.let { handleBackStack(it, parentFragment) }
        }
    }

    private fun showDetailsFragment(qualificationName: String, qualificationCode: String) {
        val fragment = QualificationDetailsFragment()
        val bundle = Bundle()
        bundle.putString("qualificationName", qualificationName)
        bundle.putString("qualificationCode", qualificationCode)
        fragment.arguments = bundle

        (parentFragment as QualificationFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}

class QualificationDetailsFragment : Fragment() {
    private var _binding: FragmentQualificationDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }

    private val qualificationName by lazy { arguments?.getString("qualificationName") }
    private val qualificationCode by lazy { arguments?.getString("qualificationCode") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var iView: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtQualificationName.text = qualificationName
        qualificationCode?.let { viewModel.getQualificationDetails(it) }

        viewModel.qualificationDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultUtil.Success -> {
                    binding.txtFee.text = "필기 : ${result.data.fee.writtenFee}원\n실기 : ${result.data.fee.practicalFee}원" // 수수료
                    binding.txtTendency.text = result.data.tendency // 출제경향
                    binding.txtAcquistion.text = result.data.acquisition // 취득방법
                }
                is ResultUtil.Error -> { Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        binding.btnBack.setOnClickListener {
            (parentFragment as QualificationFragment).childFragmentManager.popBackStack()
        }

        iView = view
        handleBackStack(view, parentFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            iView?.let { handleBackStack(it, parentFragment) }
        }
    }
}

class QualificationSearchFragment : Fragment() {
    private var _binding: FragmentQualificationSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val qualificationAdapter = QualificationAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var sView: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressIndicator.show()

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = qualificationAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.GRAY))
            setHasFixedSize(true)
        }

        viewModel.getQualificationList()
        viewModel.qualificationList.observe(viewLifecycleOwner) { result ->
            binding.progressIndicator.hide()
            when (result) {
                is ResultUtil.Success -> { qualificationAdapter.setItemList(result.data) }
                is ResultUtil.Error -> { Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }

        qualificationAdapter.setOnItemClickListener { item, _ ->
            showInfoFragmentSearch(item.name, item.code)
            hideKeyboard(binding.root)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                qualificationAdapter.filter.filter(newText)
                return false
            }
        })
        // SearchView의 닫기 버튼 리스너 설정
        binding.searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                handleBackStack(view, parentFragment)
                return false
            }
        })

        binding.btnBack.setOnClickListener {
            (parentFragment as QualificationFragment).childFragmentManager.popBackStack()
        }

        // 뒤로가기 이벤트 처리
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // SearchView의 Close 버튼 이벤트를 호출하여 "x" 표시를 누름
                binding.searchView.onActionViewCollapsed()
                handleBackStack(view, parentFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        sView = view
        handleBackStack(view, parentFragment)
        binding.searchView.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            sView?.let { handleBackStack(it, parentFragment) }
        }
    }

    private fun showInfoFragmentSearch(qualificationName: String, qualificationCode: String) {
        val fragment = QualificationDetailsFragment()
        val bundle = Bundle()
        bundle.putString("qualificationName", qualificationName)
        bundle.putString("qualificationCode", qualificationCode)
        fragment.arguments = bundle

        (parentFragment as QualificationFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 뷰에 포커스를 주고 키보드를 숨기는 함수
    private fun hideKeyboard(view: View) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

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