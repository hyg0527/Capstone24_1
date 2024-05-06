package com.credential.cubrism.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityAddstudyBinding
import com.credential.cubrism.databinding.FragmentStudygroupDdayBinding
import com.credential.cubrism.databinding.FragmentStudygroupFunc2Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunc3Binding
import com.credential.cubrism.databinding.FragmentStudygroupGoalBinding
import com.credential.cubrism.databinding.FragmentStudygroupHomeBinding
import com.credential.cubrism.databinding.FragmentStudygroupManagehomeBinding
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.model.repository.ChatRepository
import com.credential.cubrism.model.service.StompClient
import com.credential.cubrism.view.adapter.ChatAdapter
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.Rank
import com.credential.cubrism.view.adapter.StudyGroupRankAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.ChatViewModel
import com.credential.cubrism.viewmodel.DDayViewModel
import com.credential.cubrism.viewmodel.GoalListViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

class StudyGroupHomeFragment : Fragment() {
    private var _binding: FragmentStudygroupHomeBinding? = null
    private val binding get() = _binding!!

    private val goalListViewModel: GoalListViewModel by viewModels()
    private val dDayViewModel: DDayViewModel by activityViewModels()
    private var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        initGoalListView()
        dDayInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dDayInit() {
        dDayViewModel.pairStringLiveData.observe(viewLifecycleOwner) { data ->
            binding.goaltxt.text = data.first + "까지"
            binding.ddaynumtext.text = data.second.toString()
        }
    }

    private fun initGoalListView() {
        val items = goalListViewModel.goalList.value ?: ArrayList()
        val adapter = GoalAdapter(items, true)

        binding.goalRecyclerViewList.layoutManager = LinearLayoutManager(requireContext())
        binding.goalRecyclerViewList.adapter = adapter
    }
}

class StudyGroupFunc2Fragment : Fragment() {
    private var _binding: FragmentStudygroupFunc2Binding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StudyGroupRankAdapter
    private var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        initRankList(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRankList(v: View) {
        val items = ArrayList<Rank>().apply {
            for (i in 1..5) {
                add(Rank("참가자 $i",(6 - i) * 20))
            }
        }
        val recyclerView = v.findViewById<RecyclerView>(R.id.studyGroupRankView)
        adapter = StudyGroupRankAdapter(items)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) { adapter.reloadItems() }
    }
}

class StudyGroupFunc3Fragment : Fragment() {
    private var _binding: FragmentStudygroupFunc3Binding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by activityViewModels { ViewModelFactory(ChatRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var stompClient: StompClient

    private var studygroupId: Long = 100 //임의로 설정함. 나중에 수정
    private var myEmail: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc3Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stompClient.disconnect()
        _binding = null
    }

    private fun setupView() {
        chatViewModel.getChattingList(studygroupId)

        binding.btnSend.setOnClickListener {
            val text = binding.editMessage.text.toString()

            myEmail?.let {
                if (text.isNotEmpty()) {
                    binding.editMessage.text?.clear()
                    binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

                    stompClient.sendMessage(studygroupId, ChatRequestDto(it, text))
                }
            }
        }
    }

    private fun setupRecyclerView(myEmail: String) {
        chatAdapter = ChatAdapter(myEmail)
        stompClient = StompClient()
        stompClient.chatAdapter = chatAdapter
        stompClient.connect(studygroupId)
        binding.recyclerView.apply {
            adapter = chatAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 40, 0, 0, 0, 0, null))
        }
    }

    private fun observeViewModel() {
        chatViewModel.apply {
            chatList.observe(viewLifecycleOwner) {
                chatAdapter.setItemList(it)
            }

            errorMessage.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dataStore.getEmail().asLiveData().observe(viewLifecycleOwner) { email ->
            setupRecyclerView(email ?: "")
            myEmail = email
        }
    }
}

class StudyGroupManageFragment : Fragment() {
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
        }
    }
}

class StudyGroupAnnounceFixFragment : Fragment() {
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
//            txtTitle.text = "게시글 정보 수정"
//            btnCreate.text = "수정하기"
//            switchHide.visibility = View.VISIBLE
//
//            btnBack.setOnClickListener {
//                (activity as StudyManageActivity).popBackStackFragment()
//            }
//            switchHide.setOnCheckedChangeListener { _, isChecked ->
//                when (isChecked) {
//                    true -> layoutBody.visibility = View.GONE
//                    false -> layoutBody.visibility = View.VISIBLE
//                }
//            }
//            btnCreate.setOnClickListener {// 수정 버튼 클릭
//                if (switchHide.isEnabled) {
//
//                    Toast.makeText(requireContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//                else {
//
//                    Toast.makeText(requireContext(), "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }

        }
    }

}

class StudyGroupGoalFragment : Fragment() { // 목표 설정 프래그먼트
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

class StudyGroupDDayFragment : Fragment() {
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

//class StudyGroupFunc4Fragment : Fragment() {
//    private var _binding: FragmentStudygroupFunc4Binding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentStudygroupFunc4Binding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // 처음 화면을 fragment_studygroup_funclist 으로 설정
//        if (savedInstanceState == null) {
//            childFragmentManager.beginTransaction()
//                .replace(R.id.studyGroupFunc4ContainerView, StudyGroupFuncListFragment())
//                .setReorderingAllowed(true)
//                .commit()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class StudyGroupFuncListFragment : Fragment() { // 4번째 리스트 출력 프래그먼트
//    private var _binding: FragmentStudygroupFunclistBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentStudygroupFunclistBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.txtTimerMenu.setOnClickListener { changeFragmentFunc4(parentFragment, StudyGroupTimerFragment()) }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun changeFragmentFunc4(parentFragment: Fragment?, fragment: Fragment) {
//        (parentFragment as StudyGroupFunc4Fragment).childFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
//            .replace(R.id.studyGroupFunc4ContainerView, fragment)
//            .addToBackStack(null)
//            .commit()
//    }
//}

//class StudyGroupTimerFragment : Fragment() {
//    private var _binding: FragmentStudygroupTimerBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentStudygroupTimerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//class StudyGroupSetTitleFragment : Fragment(R.layout.fragment_studygroup_settitle) {
//    private var _binding: FragmentStudygroupSettitleBinding? = null
//    private val binding get() = _binding!!
//    private val titleViewModel: TitleViewModel by activityViewModels()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentStudygroupSettitleBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initView()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private fun initView() {
//        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
//        binding.btnTitleSubmit.setOnClickListener {
//            titleViewModel.setEditTextValue(binding.editTextStudyTitleTxt.text.toString())
//
//            Toast.makeText(requireContext(), "소개글 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
//            (activity as StudyManageActivity).popBackStackFragment()
//        }
//    }
//}