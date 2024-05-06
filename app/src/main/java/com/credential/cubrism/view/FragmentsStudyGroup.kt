package com.credential.cubrism.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentStudygroupFunc2Binding
import com.credential.cubrism.databinding.FragmentStudygroupFunc3Binding
import com.credential.cubrism.databinding.FragmentStudygroupHomeBinding
import com.credential.cubrism.view.adapter.Chat
import com.credential.cubrism.view.adapter.ChattingAdapter
import com.credential.cubrism.view.adapter.GoalAdapter
import com.credential.cubrism.view.adapter.Rank
import com.credential.cubrism.view.adapter.StudyGroupRankAdapter
import com.credential.cubrism.viewmodel.DDayViewModel
import com.credential.cubrism.viewmodel.GoalListViewModel

class StudyGroupHomeFragment : Fragment() {
    private var _binding: FragmentStudygroupHomeBinding? = null
    private val binding get() = _binding!!

    private val goalListViewModel: GoalListViewModel by activityViewModels()
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

        if (items.isEmpty()) {
            binding.textView50.visibility = View.VISIBLE
        }
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
        binding.button.setOnClickListener { goToCBT() }
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

    private fun goToCBT() { // 자격증 기출문제 홈페이지 이동 함수
        val url = "https://m.comcbt.com/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
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
