package com.credential.cubrism.view

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityQnaPostingBinding
import com.credential.cubrism.databinding.DialogCategoryBinding
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PostUpdateDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.view.adapter.OnDeleteClickListener
import com.credential.cubrism.view.adapter.QnaPhotoAdapter
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.ItemDecoratorDivider
import com.credential.cubrism.viewmodel.PostViewModel
import com.credential.cubrism.viewmodel.QualificationViewModel
import com.credential.cubrism.viewmodel.S3ViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicInteger

class QnaPostingActivity : AppCompatActivity(), OnDeleteClickListener {
    private val binding by lazy { ActivityQnaPostingBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private val s3ViewModel: S3ViewModel by viewModels { ViewModelFactory(S3Repository()) }

    private val qualificationAdapter = QualificationAdapter()
    private lateinit var qnaPhotoAdapter: QnaPhotoAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val postState by lazy { intent.getStringExtra("postState") }
    private val postId by lazy { intent.getIntExtra("postId", -1) }

    private val boardId = 1
    private var addImageList = mutableListOf<String>()
    private var presignedUrlList = mutableListOf<String>()
    private var s3UrlList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupBottomSheetDialog()
        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    override fun onDeleteClick(position: Int) {
        qnaPhotoAdapter.removeItem(position)
        addImageList.removeAt(position)
        binding.txtPhotoCount.text = "${qnaPhotoAdapter.itemCount}/10"
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.recyclerView.apply {
            adapter = qualificationAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 0, 2, 0, Color.GRAY))
            setHasFixedSize(true)
        }

        bottomSheetBinding.editSearch.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            qualificationAdapter.filter.filter(text)
        })

        qualificationAdapter.setOnItemClickListener { item, _ ->
            binding.txtCategory.text = item.name
            bottomSheetDialog.dismiss()
        }
    }

    private fun setupRecyclerView() {
        qnaPhotoAdapter = QnaPhotoAdapter(this)

        binding.recyclerView.apply {
            adapter = qnaPhotoAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(0, 0, 0, 28, 0, 0, null))
            setHasFixedSize(true)
        }

        qnaPhotoAdapter.setOnItemClickListener { item, position ->
            // 이미지 확대
        }
    }

    private fun setupView() {
        qualificationViewModel.getQualificationList()

        when (postState) {
            "add" -> {
                binding.txtTitle.text = "글 작성"
                binding.btnAdd.text = "등록"
            }
            "update" -> {
                if (postId != -1) postViewModel.getPostView(postId)
                binding.txtTitle.text = "글 수정"
                binding.btnAdd.text = "수정"
            }
        }

        if (postState == "update" && postId != -1)
            postViewModel.getPostView(postId)

        // 이미지 선택
        binding.layoutPhoto.setOnClickListener {
            TedImagePicker.with(this)
                .title("사진 선택")
                .max(10, "사진은 최대 10장까지 선택 가능합니다.")
                .buttonBackground(R.drawable.button_rounded_corner)
                .showCameraTile(false)
                .mediaType(MediaType.IMAGE)
                .selectedUri(qnaPhotoAdapter.getItemList().map { Uri.parse(it) }) // 이미지 선택 시 기존 선택한 이미지 유지
                .startMultiImage { uriList ->
                    qnaPhotoAdapter.setItemList(uriList.map { it.toString() })
                    binding.txtPhotoCount.text = "${qnaPhotoAdapter.itemCount}/10"

                    uriList.map {
                        val fileName = getFileNameFromUri(it)
                        addImageList.add(fileName.toString())
                    }
                }
        }

        // 카테고리 선택
        binding.imgDropDown.setOnClickListener {
            bottomSheetDialog.show()
        }

        // 게시글 등록 및 수정
        binding.btnAdd.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()
            val category = binding.txtCategory.text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressIndicator.show()
            when (postState) {
                "add" ->  {
                    if (addImageList.isNotEmpty()) { // 선택한 이미지가 있으면 PresignedUrl 요청
                        s3ViewModel.getPresignedUrl(addImageList.map { PresignedUrlRequestDto("post_images", it) })
                    } else {
                        addPost(title, content, category, emptyList())
                    }
                }
                "update" -> updatePost(title, content, category, emptyList(), emptyList())
            }
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            addPost.observe(this@QnaPostingActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            updatePost.observe(this@QnaPostingActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            postView.observe(this@QnaPostingActivity) {
                binding.editTitle.setText(it.title)
                binding.editContent.setText(it.content)
                binding.txtCategory.text = it.category
            }

            errorMessage.observe(this@QnaPostingActivity) { event ->
                binding.progressIndicator.hide()
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@QnaPostingActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        qualificationViewModel.apply {
            qualificationList.observe(this@QnaPostingActivity) { result ->
                if (postState == "add") binding.txtCategory.text = result.random().name
                qualificationAdapter.setItemList(result)
            }

            errorMessage.observe(this@QnaPostingActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@QnaPostingActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        s3ViewModel.apply {
            presignedUrl.observe(this@QnaPostingActivity) { result ->
                val uploadedImageCount = AtomicInteger(0) // 이미지 업로드 완료 카운트

                s3UrlList.addAll(result.map { it.fileUrl }) // S3에 업로드된 이미지 URL
                presignedUrlList.addAll(result.map { it.presignedUrl }).also {
                    CoroutineScope(Dispatchers.Main).launch {
                        val jobs = presignedUrlList.mapIndexed { index, url ->
                            val uri = Uri.parse(qnaPhotoAdapter.getItemList()[index])
                            val inputStream = contentResolver.openInputStream(uri) // Content Provider에 접근하여 Uri로부터 InputStream을 가져옴

                            inputStream?.readBytes()?.toRequestBody("image/*".toMediaTypeOrNull())?.let { requestBody ->
                                // 업로드 시간 단축을 위해 병렬로 이미지 업로드
                                async(Dispatchers.IO) {
                                    s3ViewModel.uploadImage(url, requestBody).also {
                                        uploadedImageCount.incrementAndGet() // 이미지 업로드 완료 시 카운트 증가
                                    }
                                }
                            }
                        }

                        jobs.forEach { it?.await() }

                        // 모든 이미지 업로드 완료 시 게시글 등록
                        if (uploadedImageCount.get() == addImageList.size) {
                            addPost(binding.editTitle.text.toString(), binding.editContent.text.toString(), binding.txtCategory.text.toString(), s3UrlList)
                        }
                    }
                }
            }

            errorMessage.observe(this@QnaPostingActivity) { event ->
                binding.progressIndicator.hide()
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@QnaPostingActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addPost(title: String, content: String, category: String, imageList: List<String>) {
        postViewModel.addPost(PostAddDto(title, content, boardId, category, imageList))
    }

    private fun updatePost(title: String, content: String, category: String, addedImageList: List<String>, removedImageList: List<String>) {
        postViewModel.updatePost(postId, PostUpdateDto(title, content, category, addedImageList,removedImageList))
    }

    // Uri에서 파일명 가져오기
    private fun getFileNameFromUri(uri: Uri): String? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }

        return null
    }
}