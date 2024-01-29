package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileFix = view.findViewById<CircleImageView>(R.id.circle1)

        profileFix.setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.custom_fade_in, R.anim.custon_fade_out)

            // ProfileFixFragment를 생성하여 추가 또는 교체
            val profileFixFragment = ProfileFixFragment()
            fragmentTransaction.replace(R.id.fragmentContainerView, profileFixFragment)

            // 백 스택에 추가, Transaction을 커밋
            //fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}
class ProfileFixFragment : Fragment(R.layout.fragment_mypage_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnProfile)
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}



