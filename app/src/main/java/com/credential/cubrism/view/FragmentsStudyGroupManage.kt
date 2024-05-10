package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.DialogGoalAddBinding
import com.credential.cubrism.databinding.DialogScheduleDatepickBinding
import com.credential.cubrism.databinding.FragmentStudygroupDdayBinding
import com.credential.cubrism.databinding.FragmentStudygroupGoalBinding
import com.credential.cubrism.databinding.FragmentStudygroupManageacceptBinding
import com.credential.cubrism.databinding.FragmentStudygroupManagehomeBinding
import com.credential.cubrism.model.dto.DDayDto
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.Goals
import com.credential.cubrism.view.adapter.GroupAcceptButtonClickListener
import com.credential.cubrism.view.adapter.GroupDenyButtonClickListener
import com.credential.cubrism.view.adapter.JoinAcceptAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyGroupManageFragment : Fragment() { // 관리 홈화면
    private var _binding: FragmentStudygroupManagehomeBinding? = null
    private val binding get() = _binding!!

    private val groupId by lazy { arguments?.getInt("groupId") ?: -1 }
    private val ddayTitle by lazy { arguments?.getString("ddayTitle") }
    private val dday by lazy { arguments?.getString("dday") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupManagehomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        binding.apply {
            manageGoal.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupGoalFragment()) }
            manageDday.setOnClickListener {
                val fragment = StudyGroupDDayFragment()
                fragment.arguments = Bundle().apply {
                    putInt("groupId", groupId)
                    putString("ddayTitle", ddayTitle)
                    putString("dday", dday)
                }
                (activity as StudyManageActivity).changeFragmentManage(fragment)
            }
            manageAccept.setOnClickListener {
                val fragment = StudyGroupAcceptFragment()
                fragment.arguments = Bundle().apply { putInt("groupId", groupId) }
                (activity as StudyManageActivity).changeFragmentManage(fragment)
            }
        }
    }
}

class StudyGroupGoalFragment : Fragment() { // 목표 설정 화면
    private var _binding: FragmentStudygroupGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = initGoalRecyclerView()

        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        binding.btnAddGoal.setOnClickListener {
            if (adapter.getItem().size >= 3) {
                Toast.makeText(requireContext(), "목표 개수는 3개까지 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                callAddDialog(adapter)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoalRecyclerView(): GoalAdapter {
        val items = ArrayList<Goals>()
        val adapter = GoalAdapter(items, false)

        binding.goalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.goalRecyclerView.adapter = adapter

        return adapter
    }

    private fun callAddDialog(adapter: GoalAdapter) { // 목표 추가 다이얼로그 호출
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogGoalAddBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
            .setPositiveButton("추가") { dialog, _ ->
                if (dialogBinding.editTextGoalTitle.text.isEmpty()) {
                    Toast.makeText(requireContext(), "목표 제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                else {
                    adapter.addItem(adapter.getItem().size + 1, dialogBinding.editTextGoalTitle.text.toString())
                    Toast.makeText(requireContext(), "목표가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}

class StudyGroupDDayFragment : Fragment() { // 디데이 설정 화면
    private var _binding: FragmentStudygroupDdayBinding? = null
    private val binding get() = _binding!!

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val groupId by lazy { arguments?.getInt("groupId") ?: -1 }
    private val ddayTitle by lazy { arguments?.getString("ddayTitle") }
    private val dday by lazy { arguments?.getString("dday") }

    private lateinit var dialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupDdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        setupDialog()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        (activity as StudyManageActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
    }

    private fun setupView() {
        binding.editTitle.setText(ddayTitle)
        binding.txtDate.text = dday

        // D-Day가 설정되어있지 않은 경우에만 설정 가능
        if (dday.isNullOrEmpty() && ddayTitle.isNullOrEmpty()) {
            binding.txtDate.setOnClickListener {
                dialog.show()
            }

            binding.btnSet.setOnClickListener {
                val title = binding.editTitle.text.toString()
                val date = binding.txtDate.text.toString()

                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (date.isEmpty()) {
                    Toast.makeText(requireContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                studyGroupViewModel.setDday(DDayDto(groupId, title, date))
            }
        } else {
            binding.editTitle.isEnabled = false
            binding.btnSet.isEnabled = false
        }
    }

    private fun setupDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogScheduleDatepickBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        dialog = builder.create()

        // 날짜 선택 이벤트 처리
        dialogBinding.calendarViewDialog.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.time

            binding.txtDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)
            dialog.dismiss()
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.setDday.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
    }
}

class StudyGroupAcceptFragment : Fragment(), GroupAcceptButtonClickListener, GroupDenyButtonClickListener { // 신청 관리 화면
    private var _binding: FragmentStudygroupManageacceptBinding? = null
    private val binding get() = _binding!!

    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var joinAcceptAdapter: JoinAcceptAdapter

    private val groupId by lazy { arguments?.getInt("groupId") ?: -1 }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupManageacceptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        if (groupId != -1) {
            studyGroupViewModel.getStudyGroupJoinReceiveList(groupId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAcceptButtonClick(item: StudyGroupJoinReceiveListDto) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(item.userName)
            setMessage("가입 신청을 수락하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("수락") { _, _ ->
                studyGroupViewModel.acceptJoinRequest(item.memberId)
            }
            show()
        }
    }

    override fun onDenyButtonClick(item: StudyGroupJoinReceiveListDto) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(item.userName)
            setMessage("가입 신청을 거절하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("거절") { _, _ ->
                studyGroupViewModel.denyJoinRequest(item.memberId)
            }
            show()
        }
    }

    private fun setupToolbar() {
        (activity as StudyManageActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
    }

    private fun setupRecyclerView() {
        joinAcceptAdapter = JoinAcceptAdapter(this, this)
        binding.recyclerView.apply {
            adapter = joinAcceptAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.parseColor("#E0E0E0")))
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            joinReceiveList.observe(viewLifecycleOwner) {
                joinAcceptAdapter.setItemList(it)
            }

            acceptJoinRequest.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getStudyGroupJoinReceiveList(groupId)
            }

            denyJoinRequest.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                studyGroupViewModel.getStudyGroupJoinReceiveList(groupId)
            }
        }
    }
}