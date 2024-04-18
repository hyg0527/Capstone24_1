package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.credential.cubrism.R
import com.credential.cubrism.databinding.FragmentHomeBinding
import com.credential.cubrism.databinding.FragmentHomeUiBinding
import com.credential.cubrism.view.adapter.BannerAdapter
import com.credential.cubrism.view.adapter.BannerData
import com.credential.cubrism.view.adapter.LicenseAdapter
import com.credential.cubrism.view.adapter.QnaBannerEnterListener
import com.credential.cubrism.view.adapter.TodayData
import com.credential.cubrism.view.adapter.TodoAdapter
import com.credential.cubrism.view.adapter.myLicenseData
import com.credential.cubrism.viewmodel.UserViewModel
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

    private val userViewModel: UserViewModel by activityViewModels()

    private val startForRegisterResultSignIn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
            userViewModel.getUserInfo()
        }
    }

    private val startForRegisterResultLogOut = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "로그아웃 성공!", Toast.LENGTH_SHORT).show()
            userViewModel.getUserInfo()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeUiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupView()
        observeViewModel()

        val tdlist = TodayData()
        val lcslist = LCSData()
//        val bnlist = BannerData()

        val td_adapter = TodoAdapter(tdlist)
        val lcs_adapter = LicenseAdapter(lcslist)
        val bn_adapter = BannerAdapter()

        bn_adapter.setBannerListener(object: QnaBannerEnterListener {
            override fun onBannerClicked() {
                startActivity(Intent(requireActivity(), PostActivity::class.java))
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

    private fun setupToolbar() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    private fun setupView() {
        Glide.with(this).load(R.drawable.peopleimage_home).into(binding.backgroundImage)

        binding.txtSignIn.setOnClickListener {
            startForRegisterResultSignIn.launch(Intent(requireActivity(), SignInActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startForRegisterResultLogOut.launch(Intent(requireActivity(), MyPageActivity::class.java))
        }

        binding.btnNoti.setOnClickListener {
            startActivity(Intent(requireActivity(), NotiActivity::class.java))
        }
    }

    private fun observeViewModel() {
        userViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            if (user != null) {
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