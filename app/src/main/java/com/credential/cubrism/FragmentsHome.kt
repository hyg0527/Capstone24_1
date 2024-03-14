package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_home_ui로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.homeFragmentContainerView, HomeUiFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class HomeUiFragment : Fragment(R.layout.fragment_home_ui) {

//    private var view: View? = null
//    private lateinit var tdlistviewModel: TodoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val login = view.findViewById<LinearLayout>(R.id.loginBtnLayout)
        val notify = view.findViewById<ImageButton>(R.id.btnNotify)
        val todoRCV = view.findViewById<RecyclerView>(R.id.todoRCV)
        val mylicenseRCV = view.findViewById<RecyclerView>(R.id.mylicenseRCV)
        val bannerVP = view.findViewById<ViewPager2>(R.id.bannerViewPager)
        val tdlist = TodayData()
        val lcslist = LCSData()
        val bnlist = BannerData()


        login.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }
        notify.setOnClickListener { // 알림 화면 출력
            changeFragment(parentFragment, NotifyFragment())
        }

        val qnaEnter = view.findViewById<Button>(R.id.btnQnaEnter)
        qnaEnter.setOnClickListener {
            changeFragment(parentFragment, QnaFragment())
        }


        val td_adapter = TodoAdapter(tdlist)
        val lcs_adapter = LicenseAdapter(lcslist)
        val bn_adapter = BannerAdapter(bnlist)


        todoRCV.layoutManager = LinearLayoutManager(requireActivity())
        todoRCV.adapter = td_adapter



        mylicenseRCV.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        mylicenseRCV.adapter = lcs_adapter

        bannerVP.adapter = bn_adapter

    }
    private fun TodayData(): ArrayList<TodayData> {
        return ArrayList<TodayData>().apply {
            add(TodayData(false, "미팅 준비하기!!"))
            add(TodayData(true, "수업 듣기"))
            add(TodayData(false, "친구랑 약속"))
        }
    }

    private fun LCSData(): ArrayList<myLicenseData> {
        return ArrayList<myLicenseData>().apply {
            add(myLicenseData("정보처리기사"))
            add(myLicenseData("한식조리기능사"))
            add(myLicenseData("직업상담사1급"))
        }
    }

    private fun BannerData(): ArrayList<BannerData> {
        return ArrayList<BannerData>().apply {
            add(BannerData("궁금한 것이 있을 땐?\nQ&amp;A 게시판에 질문하세요!"))
        }
    }



}
// 알림 화면
class NotifyFragment : Fragment(R.layout.fragment_home_notification) {
    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val backBtn = view.findViewById<ImageButton>(R.id.backBtnNotify)
        backBtn.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}
// Qna 메인 화면
class QnaFragment : Fragment(R.layout.fragment_qna) {
    private var view: View? = null
    private lateinit var qnaListViewModel: QnaListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        val writeFragment = QnaWriteFragment()
        val viewPostFragment = QnaViewPostFragment()

        val rcv = view.findViewById<RecyclerView>(R.id.qnaListView)
        val postList = ArrayList<QnaData>()

        qnaListViewModel = ViewModelProvider(requireActivity())[QnaListViewModel::class.java]
        val adapter = QnaAdapter(postList)

        updateViewModel(adapter)

        rcv.layoutManager = LinearLayoutManager(requireActivity())
        rcv.adapter = adapter

        val btnAddPost = view.findViewById<ImageView>(R.id.btnAddPost)
        val totalBtn = view.findViewById<TextView>(R.id.textView6)
        val wholeBtn = view.findViewById<TextView>(R.id.textView7)
        val totalLine = view.findViewById<ImageView>(R.id.totalLine)
        val wholeLine = view.findViewById<ImageView>(R.id.wholeLine)

        btnAddPost.setOnClickListener {
            changeFragment(writeFragment)
        }

        adapter.setQnaClickListener(object: QnaClickListener {
            override fun onQnaClick(item: QnaData) {
                val bundle = Bundle()
                bundle.putParcelable("qnaInfo", item)
                viewPostFragment.arguments = bundle

                changeFragment(viewPostFragment)
            }
        })

        totalBtn.setOnClickListener {
            totalBtn.setTextColor(resources.getColor(R.color.blue))
            wholeBtn.setTextColor(resources.getColor(R.color.lightblue))
            totalLine.setImageResource(R.drawable.blue_line)
            wholeLine.setImageResource(R.drawable.lightblue_line)
            changeTotalOrWhole(adapter, "total")
        }
        wholeBtn.setOnClickListener {
            totalBtn.setTextColor(resources.getColor(R.color.lightblue))
            wholeBtn.setTextColor(resources.getColor(R.color.blue))
            totalLine.setImageResource(R.drawable.lightblue_line)
            wholeLine.setImageResource(R.drawable.blue_line)
            changeTotalOrWhole(adapter, "whole")
        }

        handleBackStack(view, parentFragment)
    }

    private fun updateViewModel(adapter: QnaAdapter) {
        qnaListViewModel.questionList.observe(viewLifecycleOwner) { questionList ->
            adapter.clearItem()
            questionList.forEach { qnaList ->
                adapter.addItem(qnaList)
            }
        }
    }

    private fun sampleData(): ArrayList<QnaData> {
        return ArrayList<QnaData>().apply {
            add(QnaData("정보처리기사", "제목1", R.drawable.qna_photo,
                "글 1입니다.", "11:00", "안해연"))
            add(QnaData("제빵왕기능사", "제목2", R.drawable.qna_photo,
                "글 2입니다. 근데 이런 자격증이 있나요?\n글쎄요. 제가 만들면 있는 겁니다.\n- 익명의 사나이 -", "12:00", "황윤구"))
        }
    }

    private fun changeTotalOrWhole(adapter: QnaAdapter, value: String) { // 전체리스트 <-> 관심분야 리스트 전환 함수
        if (value.equals("total")) { // 전체 리스트
            val data = qnaListViewModel.questionList.value
            adapter.addAll(data!!)
        }
        else if (value.equals("whole")) { // 관심분야 리스트
            // 현재는 샘플로 정처기만 필터링하도록 설정. 나중에 관심분야를 QnaFragment 클래스에서 받아오는 로직이 필요할 것임.
            adapter.filterList("정보처리기사")
        }
        else return
    }

    // HomeFragment 내부 전환 함수
    private fun changeFragment(fragment: Fragment) {
        (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
            .replace(R.id.homeFragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}
// qna 글보기 fragment
class QnaViewPostFragment : Fragment(R.layout.fragment_qna_viewpost) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backBtn_qnaview = view.findViewById<ImageButton>(R.id.backBtn_qnaview)
        backBtn_qnaview.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }

        val receivedData = arguments?.getParcelable<QnaData>("qnaInfo")

        val medalName = view.findViewById<Button>(R.id.button2)
        val title = view.findViewById<TextView>(R.id.textView36)
        val info = view.findViewById<TextView>(R.id.textView37)
        val userName = view.findViewById<TextView>(R.id.textView33)

        medalName.setText(receivedData?.medalName)
        title.text = receivedData?.title
        info.text = receivedData?.postIn
        userName.text = receivedData?.userName
        // 시간 필드 추가 요망

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }
}

class QnaWriteFragment : Fragment(R.layout.fragment_qna_posting) { // 글등록 프래그먼트
    private var view: View? = null
    private lateinit var qnaViewModel: QnaListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val postingBtn = view.findViewById<Button>(R.id.postingBtn)
        val backBtnPosting = view.findViewById<ImageButton>(R.id.backBtnPosting)
        val dropDown = view.findViewById<ImageView>(R.id.medalDropDown)
        qnaViewModel = ViewModelProvider(requireActivity())[QnaListViewModel::class.java]

        postingBtn.setOnClickListener {
            val data = postData(view)
            qnaViewModel.addQuestion(data)

            Toast.makeText(requireContext(), "질문이 등록되었습니다", Toast.LENGTH_SHORT).show()
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }

        backBtnPosting.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
        }
        dropDown.setOnClickListener {
            // 관심 카테고리 설정 화면 나올 예정.
            // bottomdialog or dropdown
        }

        handleBackStack(view, parentFragment)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }

    private fun postData(v: View): QnaData { // 등록화면에서 작성한 글 정보 리턴 함수
        val medalName = v.findViewById<Button>(R.id.button2).text.toString()
        val title = v.findViewById<EditText>(R.id.postTitle).text.toString()
        val postWriting = v.findViewById<EditText>(R.id.postWriting).text.toString()

        return QnaData(medalName, title, R.drawable.qna_photo, postWriting, "13:00", "user123")
    }
}

// fragment 전환 함수
private fun changeFragment(parentFragment: Fragment?, fragment: Fragment) {
    (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
        .replace(R.id.homeFragmentContainerView, fragment)
        .addToBackStack(null)
        .commit()
}

// 백스택 호출 함수 선언
private fun handleBackStack(v: View, parentFragment: Fragment?) {
    v.isFocusableInTouchMode = true
    v.requestFocus()
    v.setOnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
            return@setOnKeyListener true
        }
        false
    }
}