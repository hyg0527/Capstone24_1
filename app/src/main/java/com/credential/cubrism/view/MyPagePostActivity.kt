package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityMypageMypostBinding
import com.credential.cubrism.databinding.DialogMenuBinding
import com.credential.cubrism.model.dto.MenuDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.view.adapter.MenuAdapter
import com.credential.cubrism.view.adapter.PostMenuClickListener
import com.credential.cubrism.view.adapter.PostMyAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyPagePostActivity : AppCompatActivity(), PostMenuClickListener {
    private val binding by lazy { ActivityMypageMypostBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private lateinit var postMyAdapter : PostMyAdapter
    private val menuAdapter = MenuAdapter()

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var loadingState = false
    private var myEmail: String? = null
    private var postId: Int? = null

    private val startForRegisterResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            postViewModel.getMyPostList(0, 10, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupBottomSheetDialog()
        setupRecyclerView()
        observeViewModel()

        postViewModel.getMyPostList(0, 10, true)
    }

    override fun onMenuClick(postId: Int) {
        this.postId = postId
        bottomSheetDialog.show()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogMenuBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        
        val menuList: List<MenuDto> = listOf(
            MenuDto(R.drawable.icon_edit2, "수정하기"),
            MenuDto(R.drawable.icon_delete2, "삭제하기")
        )

        menuAdapter.setItemList(menuList)

        bottomSheetBinding.recyclerView.apply {
            adapter = menuAdapter
            itemAnimator = null
            setHasFixedSize(true)
        }

        menuAdapter.setOnItemClickListener { item, _ ->
            when (item.text) {
                "수정하기" -> {
                    postId?.let {
                        val intent = Intent(this, PostUpdateActivity::class.java)
                        intent.putExtra("postId", it)
                        startForRegisterResult.launch(intent)
                    }
                }
                "삭제하기" -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("게시글 삭제")
                        setMessage("게시글을 삭제하시겠습니까?")
                        setNegativeButton("취소", null)
                        setPositiveButton("삭제") { _, _ ->
                            postId?.let { postViewModel.deletePost(it) }
                        }
                        show()
                    }
                }
            }
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupRecyclerView() {
        postMyAdapter = PostMyAdapter(this)

        binding.recyclerView.apply {
            adapter = postMyAdapter
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
                                postViewModel.getMyPostList(it, 10)
                            }
                        }
                    }
                }
            })
        }

        postMyAdapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, PostViewActivity::class.java)
            intent.putExtra("postId", item.postId)
            intent.putExtra("myEmail", myEmail)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            myPostList.observe(this@MyPagePostActivity) {
                if (it.isNotEmpty()) {
                    binding.txtNoPost.visibility = View.GONE
                    postMyAdapter.setItemList(it)
                }
                setLoading(false)
            }

            deletePost.observe(this@MyPagePostActivity) {
                postViewModel.getMyPostList(0, 10, true)
            }

            isLoading.observe(this@MyPagePostActivity) {
                postMyAdapter.setLoading(it)
                loadingState = it
            }

            errorMessage.observe(this@MyPagePostActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@MyPagePostActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        dataStore.getEmail().asLiveData().observe(this) { email ->
            myEmail = email
        }
    }
}