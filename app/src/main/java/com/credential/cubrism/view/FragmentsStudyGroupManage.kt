package com.credential.cubrism.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.FragmentStudygroupDdayBinding
import com.credential.cubrism.databinding.FragmentStudygroupGoalBinding
import com.credential.cubrism.databinding.FragmentStudygroupManageacceptBinding
import com.credential.cubrism.databinding.FragmentStudygroupManagehomeBinding
import com.credential.cubrism.model.dto.StudyGroupJoinReceiveListDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.GroupAcceptButtonClickListener
import com.credential.cubrism.view.adapter.GroupDenyButtonClickListener
import com.credential.cubrism.view.adapter.JoinAcceptAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.DDayViewModel
import com.credential.cubrism.viewmodel.GoalListViewModel
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class StudyGroupManageFragment : Fragment() { // 관리 홈화면
    private var _binding: FragmentStudygroupManagehomeBinding? = null
    private val binding get() = _binding!!

    private val groupId by lazy { arguments?.getInt("groupId") ?: -1 }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupManagehomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("titleName")
        initList(title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initList(title: String?) {

        binding.apply {
            manageGoal.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupGoalFragment()) }
            manageDday.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupDDayFragment()) }
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
    private val goalListViewModel: GoalListViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = initGoalRecyclerView()

        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        binding.txtGoalSubmit.setOnClickListener {
            for (item in adapter.getItem()) {
                goalListViewModel.addList(item)
            }

            Toast.makeText(requireContext(), "목표 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
        binding.btnAddGoal.setOnClickListener {
            if (adapter.getItem().size >= 3) {
                Toast.makeText(requireContext(), "목표 개수는 3개까지 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                adapter.addItem(adapter.getItem().size + 1)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoalRecyclerView(): GoalAdapter {
        val items = goalListViewModel.goalList.value ?: ArrayList()
        val adapter = GoalAdapter(items, false)

        binding.goalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.goalRecyclerView.adapter = adapter

        return adapter
    }
}

class StudyGroupDDayFragment : Fragment() { // 디데이 설정 화면
    private var _binding: FragmentStudygroupDdayBinding? = null
    private val binding get() = _binding!!
    private val dDayViewModel: DDayViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupDdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        val title = binding.editTextDdayTitle.text.toString()
        val date = binding.txtDdayDate.text.toString()

        binding.btnDdaySubmit.setOnClickListener {
            dDayViewModel.setDDay(Pair(title, calculateDays(date)))

            Toast.makeText(requireContext(), "디데이를 등록하였습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
    }

    private fun calculateDays(date: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val inputDate = LocalDate.parse(date, formatter)

        // 날짜 차이 계산
        val today = LocalDate.now()
        val difference = ChronoUnit.DAYS.between(today, inputDate)

        return difference.toInt()
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