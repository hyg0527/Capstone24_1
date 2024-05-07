package com.credential.cubrism.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityStudyBinding
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.repository.ChatRepository
import com.credential.cubrism.model.service.StompClient
import com.credential.cubrism.viewmodel.ChatViewModel
import com.credential.cubrism.viewmodel.StudyFragmentType
import com.credential.cubrism.viewmodel.StudyViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout

class StudyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStudyBinding.inflate(layoutInflater) }

    private val studyViewModel: StudyViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels { ViewModelFactory(ChatRepository()) }

    private val stompClient = StompClient()

    private val studyGroupId by lazy { intent.getIntExtra("studyGroupId", 100) } // 나중에 -1로 변경
    private val studyGroupName by lazy { intent.getStringExtra("studyGroupName") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) { setupFragment() }
        setupToolbar()
        setupTabLayout()
        setupChat()
        observeViewModel()

        var count = 5 // 참가자 인원수
        drawerInit(count)
    }

    override fun onDestroy() {
        super.onDestroy()
        stompClient.disconnect()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 제거
        binding.txtTitle.text = studyGroupName

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 메뉴 추가
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.study_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.manage -> {
                        val intent = Intent(this@StudyActivity, StudyManageActivity::class.java)
                        intent.putExtra("titleName", binding.txtTitle.text.toString())
                        startActivity(intent)
                    }
                }
                return false
            }
        })
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
            chatViewModel.getChatList(studyGroupId)

        stompClient.apply {
            connect()
            subscribe(studyGroupId) { chat ->
                chatViewModel.addChat(chat)
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
    }

    private fun drawerInit(count: Int) { // 참가자명 drawer에 표시
        val menu = binding.navigation.menu

        for (i in 1..count) { menu.add(R.id.groupList, Menu.NONE, Menu.NONE, "참가자 $i") }
        for (i in 1..count) { menu.getItem(i - 1).isEnabled = false }
    }

    fun sendMessage(chatRequestDto: ChatRequestDto) {
        stompClient.sendMessage(studyGroupId, chatRequestDto)
    }
}