package com.credential.cubrism

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudyGroupHomeFragment : Fragment(R.layout.fragment_studygroup_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

class StudyGroupFunc2Fragment : Fragment(R.layout.fragment_studygroup_func2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRankList(view)
    }

    private fun initRankList(v: View) {
        val items = ArrayList<String>().apply {
            for (i in 1..5) {
                add("user $i")
            }
        }
        val recyclerView = v.findViewById<RecyclerView>(R.id.studyGroupRankView)
        val adapter = StudyGroupRankAdapter(items)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }
}

class StudyGroupFunc3Fragment : Fragment(R.layout.fragment_studygroup_func3) {}

class StudyGroupFunc4Fragment : Fragment(R.layout.fragment_studygroup_func4) {}