package com.credential.cubrism.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityAddstudyBinding
import com.credential.cubrism.databinding.FragmentStudygroupDdayBinding
import com.credential.cubrism.databinding.FragmentStudygroupGoalBinding
import com.credential.cubrism.databinding.FragmentStudygroupManageacceptBinding
import com.credential.cubrism.databinding.FragmentStudygroupManagehomeBinding
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.viewmodel.DDayViewModel
import com.credential.cubrism.viewmodel.GoalListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class StudyGroupManageFragment : Fragment() { // 관리 홈화면
    private var _binding: FragmentStudygroupManagehomeBinding? = null
    private val binding get() = _binding!!

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
        val announceFragment = StudyGroupAnnounceFixFragment()

        val bundle = Bundle()
        bundle.putString("titleName", title)
        announceFragment.arguments = bundle

        binding.apply {
            manageAnnounce.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(announceFragment) }
            manageGoal.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupGoalFragment()) }
            manageDday.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupDDayFragment()) }
            manageAccept.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupAcceptFragment()) }
        }
    }
}

class StudyGroupAnnounceFixFragment : Fragment() { // 게시글 정보 수정
    private var _binding: ActivityAddstudyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityAddstudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val title = arguments?.getString("titleName")
        initPageInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initPageInfo() {
        binding.apply {
            txtTitle.text = "게시글 정보 수정"
            btnCreate.text = "수정하기"
            switchHide.visibility = View.VISIBLE

            btnBack.setOnClickListener {
                (activity as StudyManageActivity).popBackStackFragment()
            }
            switchHide.setOnCheckedChangeListener { _, isChecked ->
                when (isChecked) {
                    true -> layoutBody.visibility = View.GONE
                    false -> layoutBody.visibility = View.VISIBLE
                }
            }
            btnCreate.setOnClickListener {// 수정 버튼 클릭
                if (switchHide.isEnabled) {

                    Toast.makeText(requireContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else {

                    Toast.makeText(requireContext(), "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}

class StudyGroupGoalFragment : Fragment() { // 목표 설정 화면
    private var _binding: FragmentStudygroupGoalBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalViewModel: GoalListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalViewModel = ViewModelProvider(requireActivity())[GoalListViewModel::class.java]
        val adapter = initGoalRecyclerView()

        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        binding.txtGoalSubmit.setOnClickListener {
            for (item in adapter.getItem()) {
                goalViewModel.addList(item)
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
        val items = goalViewModel.goalList.value ?: ArrayList()
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
            println(calculateDays(date))

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

class StudyGroupAcceptFragment : Fragment() { // 신청관리 화면
    private var _binding: FragmentStudygroupManageacceptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStudygroupManageacceptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}