package com.credential.cubrism.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityAddstudyBinding
import com.credential.cubrism.databinding.FragmentStudygroupDdayBinding
import com.credential.cubrism.databinding.FragmentStudygroupFunc2Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunc3Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunc4Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunclistBinding
import com.credential.cubrism.databinding.FragmentStudygroupGoalBinding
import com.credential.cubrism.databinding.FragmentStudygroupHomeBinding
import com.credential.cubrism.databinding.FragmentStudygroupManagehomeBinding
import com.credential.cubrism.databinding.FragmentStudygroupSettitleBinding
import com.credential.cubrism.databinding.FragmentStudygroupTimerBinding
import com.credential.cubrism.view.adapter.Chat
import com.credential.cubrism.view.adapter.ChattingAdapter
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.Rank
import com.credential.cubrism.view.adapter.StudyGroupRankAdapter
import com.credential.cubrism.viewmodel.DDayViewModel
import com.credential.cubrism.viewmodel.GoalListViewModel
import com.credential.cubrism.viewmodel.TitleViewModel

class StudyGroupHomeFragment : Fragment() {
    private var _binding: FragmentStudygroupHomeBinding? = null
    private val binding get() = _binding!!

    private val goalListViewModel: GoalListViewModel by viewModels()
//    private lateinit var title: TextView
//    private val titleViewModel: TitleViewModel by activityViewModels()
    private var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        initGoalListView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    private lateinit var dDayViewModel: DDayViewModel
    private var view: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        dDayViewModel = ViewModelProvider(requireActivity())[DDayViewModel::class.java]
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = initRecyclerViewStudyChat()

        binding.sendingBtn.setOnClickListener {
            val text = view.findViewById<EditText>(R.id.editTextSendMessage).text.toString()
            adapter.addItem(Chat(null, null, text))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerViewStudyChat(): ChattingAdapter {
        val itemList = ArrayList<Chat>()
        val adapter = ChattingAdapter(itemList)

        binding.studyGroupChatView.layoutManager = LinearLayoutManager(requireContext())
        binding.studyGroupChatView.adapter = adapter

        return adapter
    }
}

class StudyGroupFunc4Fragment : Fragment() {
    private var _binding: FragmentStudygroupFunc4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunc4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_studygroup_funclist 으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.studyGroupFunc4ContainerView, StudyGroupFuncListFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class StudyGroupFuncListFragment : Fragment() { // 4번째 리스트 출력 프래그먼트
    private var _binding: FragmentStudygroupFunclistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupFunclistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTimerMenu.setOnClickListener { changeFragmentFunc4(parentFragment, StudyGroupTimerFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeFragmentFunc4(parentFragment: Fragment?, fragment: Fragment) {
        (parentFragment as StudyGroupFunc4Fragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.studyGroupFunc4ContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}

class StudyGroupTimerFragment : Fragment() {
    private var _binding: FragmentStudygroupTimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        initList(view, title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initList(v: View, title: String?) {
        val announceFragment = StudyGroupAnnounceFixFragment()

        val bundle = Bundle()
        bundle.putString("titleName", title)
        announceFragment.arguments = bundle

        binding.apply {
            manageAnnounce.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(announceFragment) }
            manageGoal.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupGoalFragment()) }
            manageDday.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupDDayFragment()) }
            manageTitle.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(StudyGroupSetTitleFragment()) }
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

        val title = arguments?.getString("titleName")
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
        val adapter = initGoalRecyclerView(view)

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

    private fun initGoalRecyclerView(v: View): GoalAdapter {
        val items = goalViewModel.goalList.value ?: ArrayList()
        val adapter = GoalAdapter(items, false)

        binding.goalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.goalRecyclerView.adapter = adapter

        return adapter
    }
}

class StudyGroupDDayFragment : Fragment(R.layout.fragment_studygroup_dday) {
    private var _binding: FragmentStudygroupDdayBinding? = null
    private val binding get() = _binding!!
    private lateinit var dDayViewModel: DDayViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupDdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dDayViewModel = ViewModelProvider(requireActivity())[DDayViewModel::class.java]
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        val title = binding.editTextDdayTitle
        val date = binding.txtDdayDate

        binding.btnDdaySubmit.setOnClickListener {
            dDayViewModel.setPairString(Pair(title.text.toString(), date.text.toString()))

            Toast.makeText(requireContext(), "디데이를 등록하였습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
        binding.btnResetDday.setOnClickListener {
            title.setText("")
            date.text = "2024년 00월 00일"
            Toast.makeText(requireContext(), "디데이정보를 초기화하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

class StudyGroupSetTitleFragment : Fragment(R.layout.fragment_studygroup_settitle) {
    private var _binding: FragmentStudygroupSettitleBinding? = null
    private val binding get() = _binding!!
    private val titleViewModel: TitleViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudygroupSettitleBinding.inflate(inflater, container, false)
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
        binding.backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        binding.btnTitleSubmit.setOnClickListener {
            titleViewModel.setEditTextValue(binding.editTextStudyTitleTxt.text.toString())

            Toast.makeText(requireContext(), "소개글 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
    }
}