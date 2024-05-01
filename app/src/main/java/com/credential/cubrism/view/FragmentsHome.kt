package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.LicenseAdapter
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.view.adapter.myLicenseData
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.CalendarViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels { ViewModelFactory(AuthRepository()) }
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private var currentPage = 0
    private val timer = Timer()

    // 로그인 성공
    private val startForRegisterResultSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.getUserInfo()
        }
    }

    // 로그아웃 성공
    private val startForRegisterResultLogOut = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.getUserInfo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        observeViewModel()

        val tdList = filterItem(calendarViewModel.calMonthList.value ?: ArrayList())
        val lcslist = lcsData()

        val tdAdapter = TodoAdapter(tdList)
        val lcsAdapter = LicenseAdapter(lcslist)
        val bnAdapter = BannerAdapter()

        bnAdapter.setBannerListener(object: QnaBannerEnterListener {
            override fun onBannerClicked() {
                startActivity(Intent(requireActivity(), PostActivity::class.java))
            }

            override fun onBannerStudyClicked() {
                startActivity(Intent(requireActivity(), MyStudyListActivity::class.java))
            }
        })

        binding.recyclerSchedule.adapter = tdAdapter
        binding.recyclerQualification.adapter = lcsAdapter
        isNoSchedule(tdAdapter)

        binding.viewPager.apply {
            adapter = bnAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        // 3초마다 자동으로 viewpager2가 스크롤되도록 타이머 설정
        timer.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (currentPage == bnAdapter.itemCount) {
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

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupView() {
        Glide.with(this).load(R.drawable.peopleimage_home).into(binding.backgroundImage)

        // 로그인
        binding.txtSignIn.setOnClickListener {
            startForRegisterResultSignIn.launch(Intent(requireActivity(), SignInActivity::class.java))
        }

        // 프로필
        binding.btnProfile.setOnClickListener {
            startForRegisterResultLogOut.launch(Intent(requireActivity(), MyPageActivity::class.java))
        }

        // 알림
        binding.btnNoti.setOnClickListener {
            startActivity(Intent(requireActivity(), NotiActivity::class.java))
        }
    }

    private fun observeViewModel() {
        // DataStore에 이메일이 저장되어 있는지 확인 (로그인 상태)
        dataStore.getEmail().asLiveData().observe(viewLifecycleOwner) { email ->
            if (email != null) {
                binding.btnProfile.visibility = View.VISIBLE
                binding.btnNoti.visibility = View.VISIBLE
                binding.txtSignIn.visibility = View.GONE
                binding.txtArrow.visibility = View.GONE
            } else {
                binding.btnProfile.visibility = View.GONE
                binding.btnNoti.visibility = View.GONE
                binding.txtSignIn.visibility = View.VISIBLE
                binding.txtArrow.visibility = View.VISIBLE
            }
        }
    }

    private fun isNoSchedule(todoAdapter: TodoAdapter) {
        if (todoAdapter.itemCount == 0) {
            binding.txtNoSchedule.visibility = View.VISIBLE
        }
        else {
            binding.txtNoSchedule.visibility = View.VISIBLE
        }
    }

    private fun lcsData(): ArrayList<myLicenseData> {
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