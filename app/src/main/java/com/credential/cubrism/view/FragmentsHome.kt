package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.databinding.FragmentHomeNotificationBinding
import com.credential.cubrism.databinding.FragmentHomeUiBinding
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.BannerData
import com.credential.cubrism.view.adapter.LicenseAdapter
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodayData
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.view.adapter.myLicenseData
import java.util.Timer
import java.util.TimerTask

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
        binding.backgroundImage.setImageResource(R.drawable.peopleimage_home)

        bn_adapter.setBannerListener(object: QnaBannerEnterListener {
            override fun onBannerClicked() {
                startActivity(Intent(requireActivity(), QnaActivity::class.java))
            }

            override fun onBannerStudyClicked() {
                startActivity(Intent(requireActivity(), MyStudyListActivity::class.java))
            }
        })

        binding.recyclerSchedule.adapter = td_adapter
        binding.recyclerQualification.adapter = lcs_adapter

        binding.viewPager.apply {
            adapter = bn_adapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        // 3초마다 자동으로 viewpager2가 스크롤되도록 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (currentPage == bn_adapter.itemCount) {
                        currentPage = 0
                    }
                    binding.viewPager.setCurrentItem(currentPage++, true)
                }
            }
        }, 0, 5000) // 3초마다 실행, 첫 실행 까지 3초 대기
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