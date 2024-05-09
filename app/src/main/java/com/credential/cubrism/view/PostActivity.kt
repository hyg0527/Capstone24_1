package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityPostBinding
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.PostAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout

class PostActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPostBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    private val postAdapter = PostAdapter()

    private lateinit var searchView: SearchView

    private val isLoggedIn = MyApplication.getInstance().getUserData().getLoginStatus()
    private val boardId = 1
    private var loadingState = false
    private var refreshState = false
    private var searchQuery: String? = null
    private var favorites = false

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            postViewModel.getPostList(boardId, 0, 10, searchQuery, true)
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupTabLayout()
        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            if (!searchView.isIconified) {
                postViewModel.getPostList(boardId, 0, 10, null, true)
                binding.toolbar.collapseActionView()
                searchView.setQuery("", false)
                searchQuery = null
                binding.swipeRefreshLayout.isRefreshing = true
            } else {
                finish()
            }
        }

        // 메뉴 추가
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchItem = menu.findItem(R.id.search)
                searchView = searchItem.actionView as SearchView

                searchView.queryHint = "게시글 검색"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchQuery = query
                        postViewModel.getPostList(boardId, 0, 10, query, true)
                        searchView.clearFocus()
                        binding.tabLayout.getTabAt(0)?.select()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })

                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        postViewModel.getPostList(boardId, 0, 10, null, true)
                        searchQuery = null
                        binding.swipeRefreshLayout.isRefreshing = true
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }

    private fun setupTabLayout() {
        val tabTitles = listOf("전체 글 목록", "관심 자격증")

        for (title in tabTitles) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.toolbar.collapseActionView()

                if (isLoggedIn) {
                    when (tab?.position) {
                        0 -> {
                            // 전체 글 목록
                            favorites = false
                            postViewModel.getPostList(boardId, 0, 10, searchQuery, true)
                        }
                        1 -> {
                            // 관심 자격증
                            favorites = true
                            postViewModel.getFavoritePostList(boardId, 0, 10, true)
                        }
                    }
                } else {
                    Toast.makeText(this@PostActivity, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = postAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.parseColor("#E0E0E0")))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)

                    // 스크롤을 끝까지 내렸을 때
                    if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && !loadingState) {
                        postViewModel.page.value?.let { page ->
                            // 다음 페이지가 존재하면 다음 페이지 데이터를 가져옴
                            page.nextPage?.let {
                                if (favorites) {
                                    postViewModel.getFavoritePostList(boardId, it, 10)
                                } else {
                                    postViewModel.getPostList(boardId, it, 10, searchQuery)
                                }
                            }
                        }
                    }
                }
            })
        }

        postAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, PostViewActivity::class.java)
            intent.putExtra("postId", item.postId)
            startForRegisterResult.launch(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (favorites) {
                postViewModel.getFavoritePostList(boardId, 0, 10, true)
            } else {
                postViewModel.getPostList(boardId, 0, 10, null, true)
            }
            binding.toolbar.collapseActionView()
            searchView.setQuery("", false)
            searchQuery = null
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun setupView() {
        if (isLoggedIn)
            binding.floatingActionButton.show()
        else
            binding.floatingActionButton.hide()

        postViewModel.getPostList(boardId, 0, 10, searchQuery, true)
        binding.swipeRefreshLayout.isRefreshing = true

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, PostAddActivity::class.java)
            intent.putExtra("postState", "add")
            startForRegisterResult.launch(intent)
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            postList.observe(this@PostActivity) {
                postAdapter.setItemList(it ?: emptyList())
                binding.swipeRefreshLayout.isRefreshing = false
                setLoading(false)
                if (refreshState) binding.recyclerView.scrollToPosition(0)
            }

            isLoading.observe(this@PostActivity) {
                postAdapter.setLoading(it)
                loadingState = it
            }

            isRefreshed.observe(this@PostActivity) {
                refreshState = it
            }

            errorMessage.observe(this@PostActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@PostActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}