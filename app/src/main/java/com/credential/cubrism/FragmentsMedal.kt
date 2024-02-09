package com.credential.cubrism

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MedalFragment : Fragment(R.layout.fragment_medal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_medal_home 으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.medalFragmentContainerView, MedalHomeFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class MedalHomeFragment : Fragment(R.layout.fragment_medal_home) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_medal_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.gridRecyclerView)
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener {  // 검색 버튼 클릭
            val bottomSheetDialog = MedalSearchFragment()
            bottomSheetDialog.show(requireActivity().supportFragmentManager, bottomSheetDialog.tag)
        }

        val nameList = ArrayList<String>().apply { // 아이템 임의로 추가(카테고리)
            for (i in 1..30) { add("item $i") }
        }
        val gridListAdapter = GridListAdapter(nameList)
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView.adapter = gridListAdapter

        val space = 15
        recyclerView.addItemDecoration(ItemSpaceMargin(space))

        gridListAdapter.setItemClickListener(object : ItemClickListener {
            override fun onItemClick(item: String) {
                showCategoryFragment(item)
            }
        })

        return view
    }

    private fun showCategoryFragment(itemName: String) {
        val fragment = MedalListFragment()
        val bundle = Bundle()
        bundle.putString("category", itemName) // 원하는 데이터 전달 (여기선 카테고리 이름)
        fragment.arguments = bundle

        (parentFragment as MedalFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.medalFragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}
// 카테고리 중간 리스트 fragment
class MedalListFragment : Fragment(R.layout.fragment_medal_category) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_medal_category, container, false)

        val categoryTitle = view.findViewById<TextView>(R.id.txtListName)
        val receivedData = arguments?.getString("category") // 데이터 수신 (여기선 카테고리 이름)
        categoryTitle.text = receivedData

        val categoryView = view.findViewById<RecyclerView>(R.id.categoryView)
        val nameList = ArrayList<String>().apply { // 아이템 임의로 추가(세부 항목)
            for (i in 1 .. 10) { add(receivedData + " - " + i) }
        }

        val categoryAdapter = GridCategoryAdapter(nameList)
        categoryView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoryView.adapter = categoryAdapter

        categoryAdapter.setInfoClickListener(object: InfoClickListener {
            override fun onInfoClick(item: String) {
                showInfoFragment(item)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnMedalList)

        backBtn.setOnClickListener {
            (parentFragment as MedalFragment).childFragmentManager.popBackStack()
        }
    }

    private fun showInfoFragment(itemName: String) {
        val fragment = MedalInfoFragment()
        val bundle = Bundle()
        bundle.putString("name", itemName) // 원하는 데이터 전달 (여기선 카테고리 이름)
        fragment.arguments = bundle

        (parentFragment as MedalFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.medalFragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}
// 자격증 상세 정보 fragment
class MedalInfoFragment : Fragment(R.layout.fragment_medal_info) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_medal_info, container, false)

        val infoTitle = view.findViewById<TextView>(R.id.txtMedalName)
        val receivedData = arguments?.getString("name")
        infoTitle.text = receivedData

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnMedalInfo)

        backBtn.setOnClickListener {
            (parentFragment as MedalFragment).childFragmentManager.popBackStack()
        }
    }
}

// 자격증 검색 dialogfragment
class MedalSearchFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_medal_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // dialogfragment의 높이, 길이 조절 여부 설정
        val bottomDialogBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomDialogBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomDialogBehavior.peekHeight = resources.displayMetrics.heightPixels
        bottomDialogBehavior.isDraggable = true
    }
}