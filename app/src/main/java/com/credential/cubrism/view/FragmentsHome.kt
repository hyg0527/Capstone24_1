package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.databinding.FragmentHomeUiBinding
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.BannerData
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.LicenseAdapter
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.view.adapter.myLicenseData
import com.credential.cubrism.viewmodel.CalendarViewModel
import com.credential.cubrism.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    private var currentPage = 0
    private val timer = Timer()

    private val userViewModel: UserViewModel by activityViewModels()
    private val calendarViewModel: CalendarViewModel by activityViewModels()

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
            userViewModel.getUserInfo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeUiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tdList = filterItem(calendarViewModel.calMonthList.value ?: ArrayList())
        val lcslist = LCSData()

        binding.backgroundImage.setImageResource(R.drawable.peopleimage_home)

        binding.txtSignIn.setOnClickListener {
            startForRegisterResult.launch(Intent(requireActivity(), SignInActivity::class.java))
        }

        binding.btnNotify.setOnClickListener { // 알림 화면 출력
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        val td_adapter = TodoAdapter(tdList)
        val lcs_adapter = LicenseAdapter(lcslist)
        val bn_adapter = BannerAdapter()

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
        isNoSchedule(td_adapter)

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

    private fun isNoSchedule(todoAdapter: TodoAdapter) {
        if (todoAdapter.itemCount == 0) {
            binding.txtNoSchedule.visibility = View.VISIBLE
        }
        else {
            binding.txtNoSchedule.visibility = View.VISIBLE
        }
    }

    private fun LCSData(): ArrayList<myLicenseData> {
        return ArrayList<myLicenseData>().apply {
            add(myLicenseData("정보처리기사"))
            add(myLicenseData("한식조리기능사"))
            add(myLicenseData("직업상담사1급"))
        }
    }

    private fun getTodayData(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy - MM - dd")
        return currentDate.format(formatter)
    }

    private fun filterItem(items: ArrayList<CalMonth>): ArrayList<CalMonth> {
        val newList = ArrayList<CalMonth>()
        for (item in items) {
            if ((item.startTime ?: "").contains(getTodayData())) {
                newList.add(item)
            }
        }
        return newList
    }
}