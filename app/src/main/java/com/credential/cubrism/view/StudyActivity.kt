package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyBinding
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.dto.MembersDto
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.model.service.StompClient
import com.credential.cubrism.viewmodel.StudyFragmentType
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.StudyViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout

class StudyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val studyViewModel: StudyViewModel by viewModels()
    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val stompClient = StompClient()

    private val studyGroupId by lazy { intent.getIntExtra("studyGroupId", -1) }
    private var ddayTitle: String? = null
    private var dday: String? = null
    private var firstLoad = true

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.drawerLayout.isDrawerOpen(binding.navigation)) {
                binding.drawerLayout.closeDrawer(binding.navigation)
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        if (savedInstanceState == null) { setupFragment() }
        setupToolbar()
        setupTabLayout()
        setupView()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }

    override fun onPause() {
        super.onPause()
        stompClient.disconnect()
    }

    override fun onResume() {
        super.onResume()
        setupChat()
        studyGroupViewModel.getStudyGroupEnterData(studyGroupId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navigation.itemIconTintList = null
        toggle.syncState()
    }

    private fun setupView() {
        studyGroupViewModel.setGroupId(studyGroupId)

        binding.txtManage.setOnClickListener {
            val intent = Intent(this@StudyActivity, StudyManageActivity::class.java)
            intent.putExtra("titleName", binding.txtTitle.text.toString())
            intent.putExtra("groupId", studyGroupId)
            intent.putExtra("ddayTitle", ddayTitle)
            intent.putExtra("dday", dday)
            startActivity(intent)
        }
    }

    private fun setupTabLayout() {
        val tabIcons = listOf(R.drawable.func1, R.drawable.func2, R.drawable.func3)

        for (icon in tabIcons) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(icon))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.toolbar.collapseActionView()

                studyViewModel.setCurrentFragment(tab?.position ?: 0)

                if (tab?.position == 2) {
                    tab.badge?.number = 0
                    tab.removeBadge()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFragment() {
        supportFragmentManager.beginTransaction().apply {
            add(binding.fragmentContainerView.id, StudyGroupHomeFragment(), StudyFragmentType.HOME.tag)
            add(binding.fragmentContainerView.id, StudyGroupFunc2Fragment(), StudyFragmentType.LEARN_RATE.tag)
            add(binding.fragmentContainerView.id, StudyGroupFunc3Fragment(), StudyFragmentType.CHAT.tag)
            commit()
        }
    }

    private fun setupChat() {
        if (studyGroupId != -1)
            studyGroupViewModel.getChatList(studyGroupId)

        stompClient.apply {
            connect()
            subscribe(studyGroupId) { chat ->
                studyGroupViewModel.addChat(chat)
                if (studyViewModel.currentFragmentType.value != StudyFragmentType.CHAT) {
                    val badge = binding.tabLayout.getTabAt(2)?.orCreateBadge
                    badge?.number = (badge?.number ?: 0) + 1
                }
            }
        }
    }

    private fun observeViewModel() {
        studyViewModel.currentFragmentType.observe(this) { fragmentType ->
            val currentFragment = supportFragmentManager.findFragmentByTag(fragmentType.tag)
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.fragments.forEach { fragment ->
                    if (fragment == currentFragment)
                        show(fragment)
                    else
                        hide(fragment)
                }
            }.commit()

            binding.tabLayout.getTabAt(fragmentType.ordinal)?.select()
        }

        studyGroupViewModel.apply {
            studyGroupEnterData.observe(this@StudyActivity) { group ->
                if (firstLoad) {
                    val myEmail = myApplication.getUserData().getEmail()

                    binding.txtTitle.text = group.groupName

                    // 그룹의 관리자인 경우 관리 텍스트 표시
                    if (group.members.any { it.email == myEmail && it.admin }) {
                        binding.txtManage.visibility = View.VISIBLE
                        setIsAdmin(true)
                    } else {
                        binding.txtManage.visibility = View.GONE
                        setIsAdmin(false)
                    }

                    // 관리자를 가장 위에 놓고 나머지는 닉네임 순으로 정렬
                    group.members.sortedWith(compareByDescending<MembersDto> { it.admin }.thenBy { it.nickname }).forEach { member ->
                        // Navigation Drawer에 멤버 목록 추가
                        if (member.email == myEmail) {
                            // 내 닉네임에 색상 적용
                            val spannable = SpannableString(member.nickname)
                            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@StudyActivity, R.color.blue)), 0, spannable.length, 0)
                            binding.navigation.menu.add(spannable)
                        } else {
                            binding.navigation.menu.add(member.nickname)
                        }.apply {
                            isEnabled = false // 클릭 비활성화
                            if (member.admin) setIcon(R.drawable.crown) // 관리자인 경우 아이콘 추가
                        }
                    }

                    firstLoad = false
                }

                ddayTitle = group.day.title
                dday = group.day.day
            }

            errorMessage.observe(this@StudyActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@StudyActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendMessage(chatRequestDto: ChatRequestDto) {
        stompClient.sendMessage(studyGroupId, chatRequestDto)
    }
}