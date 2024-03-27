package com.credential.cubrism

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class StudyGroupHomeFragment : Fragment(R.layout.fragment_studygroup_home) {
    private lateinit var goalListViewModel: GoalListViewModel
    private lateinit var title: TextView
    private val titleViewModel: TitleViewModel by activityViewModels()
    private var view: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        goalListViewModel = ViewModelProvider(requireActivity())[GoalListViewModel::class.java]
        title = view.findViewById(R.id.txtStudyGroupInfoTitle)

        initGoalListView(view)
        titleViewModel.editTextValue.observe(viewLifecycleOwner, Observer { value ->
            title.text = value
        })
    }

    private fun initGoalListView(v: View) {
        val items = goalListViewModel.goalList.value ?: ArrayList()
        val recyclerView = v.findViewById<RecyclerView>(R.id.goalRecyclerViewList)
        val adapter = GoalAdapter(items, true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

    }
}

class StudyGroupFunc2Fragment : Fragment(R.layout.fragment_studygroup_func2) {
    private lateinit var adapter: StudyGroupRankAdapter
    private lateinit var dDayViewModel: DDayViewModel
    private var view: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        dDayViewModel = ViewModelProvider(requireActivity())[DDayViewModel::class.java]
        initRankList(view)
        loadDDayData(view)
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

    private fun loadDDayData(v: View) {
        val noLabel = v.findViewById<TextView>(R.id.txtDdayNotLabel)
        val title = v.findViewById<TextView>(R.id.txtDDayTitleShow)
        val yesLabel = v.findViewById<TextView>(R.id.dDayLabel)
        val date = v.findViewById<TextView>(R.id.txtDdayDateShow)

        val data = dDayViewModel.pairStringLiveData.value
        println(data)
        if (!data?.first.isNullOrEmpty()) {
            noLabel.visibility = View.GONE
            title.visibility = View.VISIBLE
            yesLabel.visibility = View.VISIBLE
            date.visibility = View.VISIBLE

            title.text = data?.first
            date.text = data?.second
        }
        else {
            noLabel.visibility = View.VISIBLE
            title.visibility = View.GONE
            yesLabel.visibility = View.GONE
            date.visibility = View.GONE
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            adapter.reloadItems()
            view?.let { loadDDayData(it) }
        }
    }
}

class StudyGroupFunc3Fragment : Fragment(R.layout.fragment_studygroup_func3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = initRecyclerViewStudyChat(view)


        val sendBtn = view.findViewById<ImageButton>(R.id.sendingBtn)
        sendBtn.setOnClickListener {
            val text = view.findViewById<EditText>(R.id.editTextSendMessage).text.toString()
            adapter.addItem(Chat(null, null, text))
        }
    }

    private fun initRecyclerViewStudyChat(v: View): ChattingAdapter {
        val itemList = ArrayList<Chat>()
        val recyclerView = v.findViewById<RecyclerView>(R.id.studyGroupChatView)
        val adapter = ChattingAdapter(itemList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return adapter
    }
}

class StudyGroupFunc4Fragment : Fragment(R.layout.fragment_studygroup_func4) {
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
}

class StudyGroupFuncListFragment : Fragment(R.layout.fragment_studygroup_funclist) { // 4번째 리스트 출력 프래그먼트
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMenu(view)
    }

    private fun initMenu(v: View) {
        val timerMenu = v.findViewById<TextView>(R.id.txtTimerMenu)

        timerMenu.setOnClickListener { changeFragmentFunc4(parentFragment, StudyGroupTimerFragment()) }
    }

    private fun changeFragmentFunc4(parentFragment: Fragment?, fragment: Fragment) {
        (parentFragment as StudyGroupFunc4Fragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.studyGroupFunc4ContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}

class StudyGroupTimerFragment : Fragment(R.layout.fragment_studygroup_timer) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.timerBar)
        progressBar.isIndeterminate = false
    }
}

class StudyGroupManageFragment : Fragment(R.layout.fragment_studygroup_managehome) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("titleName")
        initList(view, title)
    }

    private fun initList(v: View, title: String?) {
        val manageAnnounce = v.findViewById<LinearLayout>(R.id.manageAnnounce)
        val manageGoal = v.findViewById<LinearLayout>(R.id.manageGoal)
        val manageDday = v.findViewById<LinearLayout>(R.id.manageDday)
        val manageTitle = v.findViewById<LinearLayout>(R.id.manageTitle)

        val announceFragment = StudyGroupAnnounceFixFragment()
        val goalFragment = StudyGroupGoalFragment()
        val dDayFragment = StudyGroupDDayFragment()
        val titleFragment = StudyGroupSetTitleFragment()

        val bundle = Bundle()
        bundle.putString("titleName", title)
        announceFragment.arguments = bundle

        manageAnnounce.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(announceFragment) }
        manageGoal.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(goalFragment) }
        manageDday.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(dDayFragment) }
        manageTitle.setOnClickListener { (activity as StudyManageActivity).changeFragmentManage(titleFragment) }
    }
}

class StudyGroupAnnounceFixFragment : Fragment(R.layout.fragment_mypage_addstudy) {
    private lateinit var studyListViewModel: StudyListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("titleName")

        studyListViewModel = ViewModelProvider(requireActivity())[StudyListViewModel::class.java]
        initPageInfo(view)
        loadData(view, title)
    }

    private fun initPageInfo(v: View) {
        val title = v.findViewById<TextView>(R.id.txtAddStudyTitle)
        val backBtn = v.findViewById<ImageButton>(R.id.backBtn)
        val submitBtn = v.findViewById<Button>(R.id.btnStudyCreate)
        val switch = v.findViewById<Switch>(R.id.switch2)
        val layout = v.findViewById<ConstraintLayout>(R.id.studyAddTemplate)

        title.text = "게시글 정보 수정"
        submitBtn.text = "수정하기"
        switch.visibility = View.VISIBLE

        backBtn.setOnClickListener {
            (activity as StudyManageActivity).popBackStackFragment()
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> layout.visibility = View.GONE
                false -> layout.visibility = View.VISIBLE
            }
        }
        submitBtn.setOnClickListener {
            if (switch.isEnabled) {

                Toast.makeText(requireContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            else {

                Toast.makeText(requireContext(), "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData(v: View, title: String?) {
        val listTitle = v.findViewById<EditText>(R.id.editTextStudyTitle)
        val listInfo = v.findViewById<EditText>(R.id.editTextStudyInfo)
        val numS = v.findViewById<EditText>(R.id.editTextNums)

        val adapter = loadTagRecyclerView(v)

        val data = studyListViewModel.studyList.value ?: ArrayList()
        for (item in data) {
            if (item.title.equals(title)) {
                listTitle.setText(item.title)
                listInfo.setText(item.info)
                numS.setText(item.totalNum.toString())

                adapter.changeTagStatus(item.tagList)
            }
        }
    }

    private fun loadTagRecyclerView(v: View): TagAdapter {
        val recyclerView = v.findViewById<RecyclerView>(R.id.tagRecyclerView)
        val itemList = listOf("#널널함", "#열공", "#일주일", "#자유롭게", "#한달", "#필참", "#뭐가", "#있지", "#음..", "#알아서", "#없음")
        val items = ArrayList<Tags>().apply {
            for (item in itemList)
                add(Tags(item))
        }
        val adapter = TagAdapter(items, true)
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
        recyclerView.adapter = adapter

        return adapter
    }
}

class StudyGroupGoalFragment : Fragment(R.layout.fragment_studygroup_goal) { // 목표 설정 프래그먼트
    private lateinit var goalViewModel: GoalListViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)
        val submit = view.findViewById<TextView>(R.id.txtGoalSubmit)
        val add = view.findViewById<Button>(R.id.btnAddGoal)

        goalViewModel = ViewModelProvider(requireActivity())[GoalListViewModel::class.java]
        val adapter = initGoalRecyclerView(view)

        backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        submit.setOnClickListener {
            for (item in adapter.getItem()) {
                goalViewModel.addList(item)
            }

            Toast.makeText(requireContext(), "목표 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
        add.setOnClickListener {
            if (adapter.getItem().size >= 3) {
                Toast.makeText(requireContext(), "목표 개수는 3개까지 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            } else {
                adapter.addItem(adapter.getItem().size + 1)
            }
        }
    }

    private fun initGoalRecyclerView(v: View): GoalAdapter {
        val items = goalViewModel.goalList.value ?: ArrayList()
        val recyclerView = v.findViewById<RecyclerView>(R.id.goalRecyclerView)
        val adapter = GoalAdapter(items, false)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return adapter
    }
}

class StudyGroupDDayFragment : Fragment(R.layout.fragment_studygroup_dday) {
    private lateinit var dDayViewModel: DDayViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dDayViewModel = ViewModelProvider(requireActivity())[DDayViewModel::class.java]
        initView(view)
    }

    private fun initView(view: View) {
        val submitBtn = view.findViewById<TextView>(R.id.btnDdaySubmit)
        val resetBtn = view.findViewById<Button>(R.id.btnResetDday)

        val title = view.findViewById<EditText>(R.id.editTextDdayTitle)
        val date = view.findViewById<TextView>(R.id.txtDdayDate)

        submitBtn.setOnClickListener {
            dDayViewModel.setPairString(Pair(title.text.toString(), date.text.toString()))

            Toast.makeText(requireContext(), "디데이를 등록하였습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
        resetBtn.setOnClickListener {
            title.setText("")
            date.text = "2024년 00월 00일"
            Toast.makeText(requireContext(), "디데이정보를 초기화하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

class StudyGroupSetTitleFragment : Fragment(R.layout.fragment_studygroup_settitle) {
    private val titleViewModel: TitleViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
    }

    private fun initView(view: View) {
        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)
        val submitBtn = view.findViewById<TextView>(R.id.btnTitleSubmit)
        val text = view.findViewById<EditText>(R.id.editTextStudyTitleTxt)

        backBtn.setOnClickListener { (activity as StudyManageActivity).popBackStackFragment() }
        submitBtn.setOnClickListener {
            titleViewModel.setEditTextValue(text.text.toString())
            println("수정된 값" + titleViewModel.editTextValue.value)

            Toast.makeText(requireContext(), "소개글 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            (activity as StudyManageActivity).popBackStackFragment()
        }
    }
}