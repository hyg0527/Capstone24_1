package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.model.dto.FavoriteListDto
import com.credential.cubrism.model.repository.FavoriteRepository
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.CalMonth
import com.credential.cubrism.view.adapter.FavType
import com.credential.cubrism.view.adapter.FavoriteAdapter2
import com.credential.cubrism.view.adapter.FavoriteItem
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodoAdapter
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

    private val myApplication = MyApplication.getInstance()

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory(FavoriteRepository()) }

    private val favoriteAdapter2 = FavoriteAdapter2()

    private var currentPage = 0
    private val timer = Timer()

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
        setupViewWithLoginStatus()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupView() {
        Glide.with(this).load(R.drawable.peopleimage_home).into(binding.backgroundImage)

        // 로그인
        binding.txtSignIn.setOnClickListener {
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
        }

        // 프로필
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), MyPageActivity::class.java))
        }

        // 알림
        binding.btnNoti.setOnClickListener {
            startActivity(Intent(requireActivity(), NotiActivity::class.java))
        }

        binding.layoutNoFavorite.setOnClickListener {
            val isLoggedIn = myApplication.getUserData().getLoginStatus()
            if (isLoggedIn) {
                val intent = Intent(requireActivity(), QualificationSearchActivity::class.java)
                intent.putExtra("type", "favorite")
                startActivity(intent)
            }
        }
    }

    private fun setupViewWithLoginStatus() {
        favoriteViewModel.getFavoriteList()

        // 로그인 상태에 따라 다른 화면 표시
        val isLoggedIn = myApplication.getUserData().getLoginStatus()
        if (isLoggedIn) {
            binding.btnProfile.visibility = View.VISIBLE
            binding.btnNoti.visibility = View.VISIBLE
            binding.txtSignIn.text = "${myApplication.getUserData().getNickname()}님 환영합니다."
            binding.txtArrow.visibility = View.GONE
            binding.txtAdd.text = "관심 자격증을 추가해 보세요!"
        } else {
            binding.btnProfile.visibility = View.GONE
            binding.btnNoti.visibility = View.GONE
            binding.txtSignIn.text = "로그인 하세요"
            binding.txtArrow.visibility = View.VISIBLE
            binding.txtAdd.text = "로그인 하여 관심 자격증을 추가해 보세요!"
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

        favoriteAdapter2.setOnItemClickListener { item, _ ->
            if (item is FavoriteListDto) {
                val intent = Intent(requireActivity(), QualificationDetailsActivity::class.java)
                intent.putExtra("qualificationName", item.name)
                intent.putExtra("qualificationCode", item.code)
                startActivity(intent)
            } else {
                val intent = Intent(requireActivity(), QualificationSearchActivity::class.java)
                intent.putExtra("type", "favorite")
                startActivity(intent)
            }
        }
    }

    private fun observeViewModel() {
        favoriteViewModel.apply {
            favoriteList.observe(viewLifecycleOwner) { list ->
                val items = mutableListOf<FavoriteItem>()

                if (list.isEmpty()) {
                    binding.layoutNoFavorite.visibility = View.VISIBLE
                    binding.recyclerQualification.visibility = View.GONE
                } else {
                    binding.layoutNoFavorite.visibility = View.GONE
                    binding.recyclerQualification.visibility = View.VISIBLE

                    items.addAll(list.map { FavoriteItem(FavType.FAVORITE, it) })
                    items.add(FavoriteItem(FavType.ADD, null))
                }

                favoriteAdapter2.setItemList(items)
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