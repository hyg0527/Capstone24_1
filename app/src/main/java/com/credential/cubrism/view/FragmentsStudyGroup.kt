package com.credential.cubrism.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.credential.cubrism.MyApplication
import com.credential.cubrism.databinding.FragmentStudygroupFunc2Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunc3Binding
import com.credential.cubrism.databinding.FragmentStudygroupHomeBinding
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.dto.GoalsDto
import com.credential.cubrism.model.dto.MembersDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.view.adapter.ChatAdapter
import com.credential.cubrism.view.adapter.StudyGroupGoalAdapter
import com.credential.cubrism.view.adapter.StudyGroupGoalClickListener
import com.credential.cubrism.view.adapter.StudyGroupGoalType
import com.credential.cubrism.view.adapter.StudyGroupRankAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.view.utils.KeyboardVisibilityUtils
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class StudyGroupHomeFragment : Fragment(), StudyGroupGoalClickListener {
    private var _binding: FragmentStudygroupHomeBinding? = null
    private val binding get() = _binding!!

    private val myApplication = MyApplication.getInstance()

    private val studyGroupViewModel: StudyGroupViewModel by activityViewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var studyGroupGoalAdapter: StudyGroupGoalAdapter

    private var studyGroupId = -1
    private var isGroupAdmin = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGoalClick(item: GoalsDto) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(item.goalName)
            setMessage("목표를 완료하시겠습니까?")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                studyGroupViewModel.completeGoal(item.goalId)
            }
            show()
        }
    }

    private fun setupView() {
        binding.cardDefault.setOnClickListener {
            if (isGroupAdmin) {
                val intent = Intent(requireContext(), StudyManageDDayActivity::class.java)
                intent.putExtra("groupId", studyGroupId)
                intent.putExtra("ddayTitle", "")
                intent.putExtra("dday", "")
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        studyGroupGoalAdapter = StudyGroupGoalAdapter(StudyGroupGoalType.GOAL_LIST, this)

        binding.recyclerView.apply {
            adapter = studyGroupGoalAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            studyGroupEnterData.observe(viewLifecycleOwner) { data ->
                val myEmail = myApplication.getUserData().getEmail()

                if (data.day.title != null && data.day.day != null) {
                    binding.progressIndicatorDDay.hide()
                    binding.cardDefault.visibility = View.INVISIBLE
                    binding.cardDDay.visibility = View.VISIBLE
                    binding.txtGoal.text = "${data.day.title}까지"
                    binding.txtDDay.text = calculateDDay(data.day.day).toString()
                } else {
                    binding.progressIndicatorDDay.hide()
                    binding.cardDefault.visibility = View.VISIBLE
                    binding.cardDDay.visibility = View.INVISIBLE
                }

                data.members.find { it.email == myEmail }?.userGoal?.goals?.let { list ->
                    binding.progressIndicatorGoal.hide()
                    studyGroupGoalAdapter.setItemList(list)

                    binding.txtNoGoal.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }

            completeGoal.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                if (studyGroupId != -1)
                    getStudyGroupEnterData(studyGroupId)
            }

            groupId.observe(viewLifecycleOwner) {
                studyGroupId = it
            }

            isAdmin.observe(viewLifecycleOwner) {
                isGroupAdmin = it
            }
        }
    }

    private fun calculateDDay(targetDateString: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val targetDate = LocalDate.parse(targetDateString, formatter)
        val currentDate = LocalDate.now()

        return ChronoUnit.DAYS.between(currentDate, targetDate)
    }
}

class StudyGroupFunc2Fragment : Fragment() {
    private var _binding: FragmentStudygroupFunc2Binding? = null
    private val binding get() = _binding!!

    private val studyGroupViewModel: StudyGroupViewModel by activityViewModels { ViewModelFactory(StudyGroupRepository()) }

    private val studyGroupRankAdapter = StudyGroupRankAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            studyGroupRankAdapter.refreshAllItems()
        }
    }

    private fun setupView() {
        binding.btnQuestion.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setMessage("외부 링크로 이동하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("이동") { _, _ ->
                    openCBTLink()
                }
                show()
            }
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.btnQuestion.visibility = View.GONE
            } else {
                binding.btnQuestion.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = studyGroupRankAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            studyGroupEnterData.observe(viewLifecycleOwner) {
                binding.progressIndicator.hide()

                it.members.sortedWith(compareByDescending<MembersDto> { member-> member.userGoal.completionPercentage }.thenBy { member -> member.nickname }).let { list ->
                    studyGroupRankAdapter.setItemList(list)
                }
            }
        }
    }

    private fun openCBTLink() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://m.comcbt.com/"))
        val packageManager = requireContext().packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)
        val isIntentSafe = activities.isNotEmpty()

        if (isIntentSafe) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "해당 URL을 열 수 있는 앱이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

class StudyGroupFunc3Fragment : Fragment() {
    private var _binding: FragmentStudygroupFunc3Binding? = null
    private val binding get() = _binding!!

    private val studyGroupViewModel: StudyGroupViewModel by activityViewModels { ViewModelFactory(StudyGroupRepository()) }

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private var myEmail = MyApplication.getInstance().getUserData().getEmail()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    private fun setupView() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = {
                binding.recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        )

        binding.btnSend.setOnClickListener {
            val text = binding.editMessage.text.toString()

            myEmail?.let {
                if (text.isNotEmpty()) {
                    binding.editMessage.text?.clear()
                    (activity as? StudyActivity)?.sendMessage(ChatRequestDto(it, text))
                }
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(myEmail)

        binding.recyclerView.apply {
            adapter = chatAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 20, 0, 0, 0, 0, null))
        }
    }

    private fun observeViewModel() {
        studyGroupViewModel.apply {
            chatList.observe(viewLifecycleOwner) {
                chatAdapter.setItemList(it)
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
