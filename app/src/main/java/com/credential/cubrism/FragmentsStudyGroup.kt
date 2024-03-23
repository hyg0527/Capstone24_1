package com.credential.cubrism

import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class StudyGroupHomeFragment : Fragment(R.layout.fragment_studygroup_home) {}

class StudyGroupFunc2Fragment : Fragment(R.layout.fragment_studygroup_func2) {
    private lateinit var adapter: StudyGroupRankAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRankList(view)
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

        if (!hidden) {
            adapter.reloadItems()
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

        val fragment = StudyGroupAnnounceFixFragment()
        val bundle = Bundle()
        bundle.putString("titleName", title)
        fragment.arguments = bundle

        manageAnnounce.setOnClickListener {
            (activity as StudyManageActivity).changeFragmentManage(fragment)
        }
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