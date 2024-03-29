package com.credential.cubrism.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.databinding.FragmentHomeNotificationBinding
import com.credential.cubrism.databinding.FragmentHomeUiBinding
import com.credential.cubrism.databinding.FragmentQnaBinding
import com.credential.cubrism.databinding.FragmentQnaPostingBinding
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.BannerData
import com.credential.cubrism.view.adapter.LicenseAdapter
import com.credential.cubrism.view.adapter.QnaAdapter
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.QnaData
import com.credential.cubrism.view.adapter.QnaPhotoAdapter
import com.credential.cubrism.view.adapter.TodayData
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.view.adapter.myLicenseData
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.QnaListViewModel
import java.util.Timer

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_home_ui로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, HomeUiFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class HomeUiFragment : Fragment() {
    private var _binding: FragmentHomeUiBinding? = null
    private val binding get() = _binding!!

//    private var view: View? = null
//    private lateinit var tdlistviewModel: TodoViewModel
    private var currentPage = 0
    private val timer = Timer()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeUiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tdlist = TodayData()
        val lcslist = LCSData()
//        val bnlist = BannerData()

        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
        }

        binding.btnNotify.setOnClickListener { // 알림 화면 출력
            changeFragment(parentFragment, NotifyFragment())
        }

        val td_adapter = TodoAdapter(tdlist)
        val lcs_adapter = LicenseAdapter(lcslist)
        val bn_adapter = BannerAdapter()

        bn_adapter.setBannerListener(object: QnaBannerEnterListener {
            override fun onBannerClicked() {
                changeFragment(parentFragment, QnaFragment())
            }

            override fun onBannerStudyClicked() {
                changeFragment(parentFragment, MyPageFragmentMyStudy())
            }
        })

        binding.recyclerSchedule.adapter = td_adapter
        binding.recyclerQualification.adapter = lcs_adapter

        binding.viewPager.apply {
            adapter = bn_adapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        /**
         * Q&A게시판 fragment로 전환되어도 타이머가 종료되지 않아
         * binding이 null이 된 상태에서 viewpager를 참조하려고 해서 앱이 종료되어 임시로 주석처리
         */
        // 3초마다 자동으로 viewpager2가 스크롤되도록 타이머 설정
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                activity?.runOnUiThread {
//                    if (currentPage == bn_adapter.itemCount) {
//                        currentPage = 0
//                    }
//                    binding.viewPager.setCurrentItem(currentPage++, true)
//                }
//            }
//        }, 3000, 5000) // 3초마다 실행, 첫 실행까지 3초 대기
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
//
//    override fun onResume() {
//        super.onResume()
//        // Fragment가 다시 보일 때 타이머 시작
//        startTimer()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Fragment가 숨겨질 때 타이머 중지
//        stopTimer()
//    }
//
//    private fun startTimer() {
//        timer = Timer()
//        timer?.schedule(object : TimerTask() {
//            override fun run() {
//                activity?.runOnUiThread {
//                    if (currentPage == adapter.itemCount) {
//                        currentPage = 0
//                    }
//                    viewPager.setCurrentItem(currentPage++, true)
//                }
//            }
//        }, 3000, 3000) // 3초마다 실행, 첫 실행까지 3초 대기
//    }
//
//    private fun stopTimer() {
//        timer?.cancel()
//        timer = null
//    }

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
        return ArrayList()
    }



}
// 알림 화면
class NotifyFragment : Fragment() {
    private var _binding: FragmentHomeNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var view: View? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        binding.btnBack.setOnClickListener {
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
class QnaFragment : Fragment() {
    private var _binding: FragmentQnaBinding? = null
    private val binding get() = _binding!!

    private var view: View? = null
    private lateinit var qnaListViewModel: QnaListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQnaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        val writeFragment = QnaWriteFragment()

        val postList = ArrayList<QnaData>()

        qnaListViewModel = ViewModelProvider(requireActivity())[QnaListViewModel::class.java]
        val adapter = QnaAdapter(postList)

        updateViewModel(adapter)

        binding.recyclerView.adapter = adapter

        binding.btnAddPost.setOnClickListener {
            changeFragment(writeFragment)
        }

        binding.txtAll.setOnClickListener {
            binding.txtAll.setTextColor(ResourcesCompat.getColor(resources, R.color.blue, null))
            binding.txtFavorite.setTextColor(ResourcesCompat.getColor(resources, R.color.lightblue, null))
            binding.viewAll.background = ResourcesCompat.getDrawable(resources, R.color.blue, null)
            binding.viewFavorite.background = ResourcesCompat.getDrawable(resources, R.color.lightblue, null)
            changeTotalOrWhole(adapter, "total")
        }

        binding.txtFavorite.setOnClickListener {
            binding.txtAll.setTextColor(ResourcesCompat.getColor(resources, R.color.lightblue, null))
            binding.txtFavorite.setTextColor(ResourcesCompat.getColor(resources, R.color.blue, null))
            binding.viewAll.background = ResourcesCompat.getDrawable(resources, R.color.lightblue, null)
            binding.viewFavorite.background = ResourcesCompat.getDrawable(resources, R.color.blue, null)
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
            .replace(R.id.fragmentContainerView, fragment)
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

class QnaWriteFragment : Fragment() { // 글등록 프래그먼트
    private var _binding: FragmentQnaPostingBinding? = null
    private val binding get() = _binding!!

    private var view: View? = null
    private var countphoto: Int = 0
    private lateinit var qnaViewModel: QnaListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQnaPostingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val adapter = initQnaPhotoRCV(view)

        qnaViewModel = ViewModelProvider(requireActivity())[QnaListViewModel::class.java]

        binding.txtCategory.visibility = View.VISIBLE
        binding.btnAdd.setOnClickListener { // 글등록 버튼 클릭 리스너
            val data = postData(view)

            if (data.title.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (data.postIn.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (!binding.txtCategory.isVisible) {
                Toast.makeText(requireContext(), "카테고리를 설정해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                qnaViewModel.addQuestion(data)
                Toast.makeText(requireContext(), "질문이 등록되었습니다", Toast.LENGTH_SHORT).show()

                (parentFragment as HomeFragment).childFragmentManager.popBackStack()
                hideKeyboard(requireContext(), view)
            }
        }

        binding.btnBack.setOnClickListener {
            (parentFragment as HomeFragment).childFragmentManager.popBackStack()
            hideKeyboard(requireContext(), view)
        }
//        dropDown.setOnClickListener {
//            val (adapter, dialog) = showSearchDialog()
//
//            adapter.setItemClickListener(object: SearchItemClickListener {
//                override fun onItemClick(item: DialogItem) {
//                    category.visibility = View.VISIBLE
//                    category.text = item.name
//                    dialog.dismiss()
//                }
//            })
//        }
        binding.layoutPhoto.setOnClickListener {
            if(countphoto>=10)
                Toast.makeText(requireContext(), "사진은 10장까지 첨부 가능합니다.", Toast.LENGTH_SHORT).show()
            else {
                adapter.addItem(0)
                countphoto++
                binding.txtPhoto.text = "$countphoto/10"
            }
        }

        handleBackStack(view, parentFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            // Fragment가 다시 화면에 나타날 때의 작업 수행
            view?.let { handleBackStack(it, parentFragment) }
        }
    }

    private fun initQnaPhotoRCV(view: View): QnaPhotoAdapter {
        val itemsList = ArrayList<Int>()

        val adapter = QnaPhotoAdapter(itemsList)
        binding.recyclerView.apply {
            this.adapter = adapter
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 40, 0, 0, null))
        }

        return adapter
    }

//    private fun showSearchDialog(): Pair<DialogSearchAdapter, Dialog> { // 카테고리 검색 다이얼로그 호출
//        val builder = AlertDialog.Builder(requireActivity())
//        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.dialog_posting_search, null)
//
//        val adapter = initSearchRecyclerView(view)
//        builder.setView(view)
//
//        val searchView = view.findViewById<SearchView>(R.id.searchViewPosting) // searchView 선언
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean { // 검색어 제출시 호출(여기선 안씀)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean { // 검색어 실시간 변경 리스너
//                adapter.filter.filter(newText.orEmpty()) // 글자 변경시마다 리사이클러뷰 갱신
//                return true
//            }
//        })
//
//        val dialog = builder.create()
//        dialog.show()
//
//        return Pair(adapter, dialog)
//    }
//
//    private fun initSearchRecyclerView(v: View): DialogSearchAdapter {
//        val names = listOf("정보처리기사", "네트워크관리사", "정보관리기술사", "정보처리기능사", "청소부", "기능장", "사람", "동물")
//        val itemList = ArrayList<DialogItem>().apply {
//            for (name in names) {
//                add(DialogItem(name, 0))
//            }
//        }
//
//        val recyclerView = v.findViewById<RecyclerView>(R.id.postingSearchView)
//        val adapter = DialogSearchAdapter(itemList)
//        val layoutManager = LinearLayoutManager(requireActivity())
//        val dividerItemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation) // 구분선 추가
//
//        recyclerView.addItemDecoration(dividerItemDecoration)
//        recyclerView.layoutManager = layoutManager
//        recyclerView.adapter = adapter
//
//        return adapter
//    }

    private fun postData(v: View): QnaData { // 등록화면에서 작성한 글 정보 리턴 함수
        val medalName = v.findViewById<TextView>(R.id.txtTitle).text.toString()
        val title = v.findViewById<EditText>(R.id.postTitle).text.toString()
        val postWriting = v.findViewById<EditText>(R.id.editContent).text.toString()

        return QnaData(medalName, title, R.drawable.qna_photo, postWriting, "13:00", "user123")
    }

    // 뷰에 포커스를 주고 키보드를 숨기는 함수
    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

// fragment 전환 함수
private fun changeFragment(parentFragment: Fragment?, fragment: Fragment) {
    (parentFragment as HomeFragment).childFragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.custom_fade_in, R.anim.custom_fade_out)
        .replace(R.id.fragmentContainerView, fragment)
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