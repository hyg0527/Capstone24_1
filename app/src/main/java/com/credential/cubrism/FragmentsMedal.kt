package com.credential.cubrism

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val view = inflater.inflate(R.layout.fragment_medal_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.gridRecyclerView)
        val floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        floatingActionButton.setOnClickListener {  // 검색 버튼 클릭
            showSearchFragment()
        }
        // 카테고리 추가. 자격항목이 없는 것은 제외하였음.(사업관리, 금융보험, 법률소방..)
        val categoryText = listOf("경영.회계.사무", "교육.자연.과학", "보건.의료", "사회복지.종교", "디자인.방송", "운전.운송",
            "영업.판매", "이용.여행.스포츠", "음식서비스", "건설", "광업자원", "기계", "재료", "화학", "섬유.의복", "전기.전자", "정보통신",
            "식품.가공", "인쇄.목재.가구.공예", "농림어업", "안전관리", "환경.에너지") // - 총 22가지
        val nameList = ArrayList<GridItems>().apply {
            for (i in 1..22) {
                val icon = "icon_" + String.format("%02d", i)
                val iconResourceID = context?.resources?.getIdentifier(icon, "drawable", context?.packageName)
                val categoryTextName = categoryText[i - 1]

                add(GridItems(categoryTextName, iconResourceID))
            }
        }
        val gridListAdapter = GridListAdapter(nameList)
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView.adapter = gridListAdapter

        val space = 15
        recyclerView.addItemDecoration(ItemSpaceMargin(space))

        gridListAdapter.setItemClickListener(object : ItemClickListener {
            override fun onItemClick(item: GridItems) {
                showCategoryFragment(item.name ?: "")
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
    private fun showSearchFragment() {
        (parentFragment as MedalFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.medalFragmentContainerView, MedalSearchFragment())
            .addToBackStack(null)
            .commit()
    }
}
// 카테고리 중간 리스트 fragment
class MedalListFragment : Fragment(R.layout.fragment_medal_category) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_medal_category, container, false)

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

    private var sView: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnMedalList)

        backBtn.setOnClickListener {
            (parentFragment as MedalFragment).childFragmentManager.popBackStack()
        }
        // 뒤로가기 버튼도 동일하게 popstack
        sView = view
        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            sView?.let { handleBackStack(it, parentFragment) }
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
    private var iView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_medal_info, container, false)

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

        iView = view
        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            iView?.let { handleBackStack(it, parentFragment) }
        }
    }
}

// 자격증 검색 dialogfragment
class MedalSearchFragment : Fragment(R.layout.fragment_medal_search) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_medal_search, container, false)
        val medalSearchView = view.findViewById<RecyclerView>(R.id.medalSearchView)

        // 검색 기능 테스트 데이터 몇개 임의로 추가했음. 개수 늘리고 글자 변경해서 테스트(영어도 될거임)
        // 왼쪽에 사진은 카테고리별 아이콘으로 넣을 예정. 현재는 한가지로 통일함.
        val names = listOf("가마우지", "고양이", "고라니", "나무늘보", "다람쥐", "라마", "말", "바다표범", "사자", "아나콘다", "호랑이")
        val imageRes = R.drawable.ellipse_1
        val itemList = ArrayList<DialogItem>().apply {
            for (name in names) {
                add(DialogItem(name, imageRes))
            }
        }

        val listAdapter = DialogSearchAdapter(itemList)
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        medalSearchView.layoutManager = layoutManager
        medalSearchView.adapter = listAdapter

        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation) // 구분선 추가
        medalSearchView.addItemDecoration(dividerItemDecoration)

        listAdapter.setItemClickListener(object: SearchItemClickListener { // 아이템 클릭하면 상세정보로 이동
            override fun onItemClick(item: DialogItem) {
                showInfoFragmentSearch(item)
                hideKeyboard(requireContext(), view)
            }
        })

        val searchView = view.findViewById<SearchView>(R.id.searchViewMedal) // searchView 선언
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { // 검색어 제출시 호출(여기선 안씀)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { // 검색어 실시간 변경 리스너
                listAdapter.filter.filter(newText.orEmpty()) // 글자 변경시마다 리사이클러뷰 갱신
                return true
            }
        })

        return view
    }

    private var sView: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backBtn = view.findViewById<ImageButton>(R.id.backBtnMedalSearchInfo)

        backBtn.setOnClickListener {
            (parentFragment as MedalFragment).childFragmentManager.popBackStack()
        }

        sView = view
        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            sView?.let { handleBackStack(it, parentFragment) }
        }
    }


    private fun showInfoFragmentSearch(itemName: DialogItem) {
        val fragment = MedalInfoFragment()
        val bundle = Bundle()
        bundle.putString("name", itemName.name ?: "null")
        fragment.arguments = bundle

        (parentFragment as MedalFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.medalFragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 뷰에 포커스를 주고 키보드를 숨기는 함수
    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

// 백스택 호출 함수 선언 (뒤로 가기)
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as MedalFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}
