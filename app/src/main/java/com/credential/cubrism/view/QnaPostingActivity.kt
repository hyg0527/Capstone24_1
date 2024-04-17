package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.credential.cubrism.databinding.ActivityQnaPostingBinding
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.utils.ResultUtil
import com.credential.cubrism.view.adapter.QnaPhotoAdapter
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory

class QnaPostingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityQnaPostingBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val boardId = 1

    private lateinit var photoAdapter: QnaPhotoAdapter
    private var photoCount = 0
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) { // 이미지가 성공적으로 선택되었을 때의 처리
                val data: Intent? = result.data
                val selectedImageUri = data?.data // 선택된 이미지의 URI

                if (photoCount >= 10) Toast.makeText(this, "사진은 10장까지 첨부 가능합니다.", Toast.LENGTH_SHORT).show()
                else {
                    photoAdapter.addItem(selectedImageUri!!)
                    photoCount++
                    binding.txtPhoto.text = "$photoCount/10"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initPhotoRecyclerView()
        setupToolbar()
        setupView()
        observeViewModel()
        addPhoto()
    }

    private fun initPhotoRecyclerView() {
        photoAdapter = QnaPhotoAdapter(ArrayList())
        binding.recyclerView.adapter = photoAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun addPhoto() { // 사진 추가
        binding.layoutPhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            pickImagesLauncher.launch(intent)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupView() {
        binding.btnAdd.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()
            val category = "정보처리기사" // 임시
            val images = emptyList<String?>() // 임시

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val postAddDto = PostAddDto(title, content, boardId, category, images)
            postViewModel.addPost(postAddDto)
        }
    }

    private fun observeViewModel() {
        postViewModel.addPost.observe(this) { result ->
            when (result) {
                is ResultUtil.Success -> { setResult(RESULT_OK).also { finish() } }
                is ResultUtil.Error -> { Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show() }
                is ResultUtil.NetworkError -> { Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}