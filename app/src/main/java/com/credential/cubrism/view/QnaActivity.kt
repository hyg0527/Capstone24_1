package com.credential.cubrism.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaBinding
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.PostAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class QnaActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }

    private val postAdapter = PostAdapter()

    private var loadingState = false
    private val board = "QnA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupTabLayout()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 메뉴 추가
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchItem = menu.findItem(R.id.search)
                val searchView = searchItem.actionView as SearchView

                searchView.queryHint = "게시글 검색"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        // TODO: 검색 기능 구현 예정
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
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
                when (tab?.position) {
                    0 -> {
                        // 전체 글 목록
                    }
                    1 -> {
                        // 관심 자격증
                    }
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
                            page.nextPage?.let { postViewModel.getPostList(it, 10, board) }
                        }
                    }
                }
            })
        }

        postAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, QnaViewActivity::class.java)
            intent.putExtra("postId", item.postId)
            intent.putExtra("boardName", item.boardName)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            postViewModel.getPostList(0, 10, board, true)
            binding.swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            getPostList(0, 10, board)
            binding.swipeRefreshLayout.isRefreshing = true

            postList.observe(this@QnaActivity) {
                postAdapter.setItemList(it ?: emptyList())
                binding.swipeRefreshLayout.isRefreshing = false
            }

            isLoading.observe(this@QnaActivity) {
                postAdapter.setLoading(it)
                loadingState = it
            }

            errorMessage.observe(this@QnaActivity) {
                it.getContentIfNotHandled()?.let { message -> Toast.makeText(this@QnaActivity, message, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}