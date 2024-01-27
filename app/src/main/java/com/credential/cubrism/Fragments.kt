package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_home, container, false)
        val login = view.findViewById<Button>(R.id.loginButtonHome)
        login.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}

class StudyFragment : Fragment(R.layout.fragment_study) {

}
class CalFragment : Fragment(R.layout.fragment_cal) {

}
class MedalFragment : Fragment(R.layout.fragment_medal) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_medal, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.gridRecyclerView)

        val nameList = ArrayList<String>().apply {
            for (i in 1..30) { add("item $i") }
        }
        val gridListAdapter = GridListAdapter(nameList)
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView.adapter = gridListAdapter

        val space = 15
        recyclerView.addItemDecoration(ItemSpaceMargin(space))

        return view
        //return super.onCreateView(inflater, container, savedInstanceState)
    }
}
class MyPageFragment : Fragment(R.layout.fragment_mypage) {

}




