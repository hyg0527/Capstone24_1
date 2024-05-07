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
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.FavoriteRepository
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.FavoriteAdapter2
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.CalendarViewModel
import com.credential.cubrism.viewmodel.FavoriteViewModel
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
    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory(FavoriteRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private val favoriteAdapter2 = FavoriteAdapter2()

    private var currentPage = 0
    private val timer = Timer()
    private var loggedIn = false

    // 로그인 성공
    private val startForRegisterResultSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.getUserInfo()
            favoriteViewModel.getFavoriteList()
        }
    }

    // 로그아웃 성공
    private val startForRegisterResultLogOut = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.getUserInfo()
            favoriteViewModel.getFavoriteList()
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
        setupRecyclerView()
        observeViewModel()

        val tdList = filterItem(calendarViewModel.calMonthList.value ?: ArrayList())

        val tdAdapter = TodoAdapter(tdList)
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

    override fun onResume() {
        super.onResume()
        favoriteViewModel.getFavoriteList()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupView() {
        Glide.with(this).load(R.drawable.peopleimage_home).into(binding.backgroundImage)

        // 로그인
        binding.txtSignIn.setOnClickListener {
            if (!loggedIn)
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

    private fun setupRecyclerView() {
        binding.recyclerQualification.apply {
            adapter = favoriteAdapter2
            itemAnimator = null
            setHasFixedSize(true)
        }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerQualification)
    }

    private fun observeViewModel() {
        // DataStore에 닉네임이 저장되어 있는지 확인 (로그인 상태)
        dataStore.getNickname().asLiveData().observe(viewLifecycleOwner) { nickname ->
            if (nickname != null) {
                binding.btnProfile.visibility = View.VISIBLE
                binding.btnNoti.visibility = View.VISIBLE
                binding.txtSignIn.text = "${nickname}님 환영합니다."
                binding.txtArrow.visibility = View.GONE
                loggedIn = true
            } else {
                binding.btnProfile.visibility = View.GONE
                binding.btnNoti.visibility = View.GONE
                binding.txtSignIn.text = "로그인 하세요"
                binding.txtArrow.visibility = View.VISIBLE
                loggedIn = false
            }
        }

        favoriteViewModel.apply {
            favoriteList.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.layoutNoFavorite.visibility = View.VISIBLE
                    binding.recyclerQualification.visibility = View.GONE
                } else {
                    binding.layoutNoFavorite.visibility = View.GONE
                    binding.recyclerQualification.visibility = View.VISIBLE
                }
                favoriteAdapter2.setItemList(it)
            }

            errorMessage.observe(viewLifecycleOwner) {
                binding.layoutNoFavorite.visibility = View.VISIBLE
                binding.recyclerQualification.visibility = View.GONE
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

    private fun getTodayData(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    private fun filterItem(items: ArrayList<CalMonth>): ArrayList<CalMonth> {
        val newList = ArrayList<CalMonth>()
        for (item in items) {
            if ((item.startDate ?: "").contains(getTodayData())) {
                newList.add(item)
            }
        }
        return newList
    }
}