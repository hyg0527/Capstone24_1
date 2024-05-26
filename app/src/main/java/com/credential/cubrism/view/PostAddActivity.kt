package com.credential.cubrism.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityPostingBinding
import com.credential.cubrism.databinding.DialogCategoryBinding
import com.credential.cubrism.model.dto.PostAddDto
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.PostRepository
import com.credential.cubrism.model.repository.QualificationRepository
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.view.adapter.PostPhotoAdapter
import com.credential.cubrism.view.adapter.QualificationAdapter
import com.credential.cubrism.view.utils.FileNameFromUri.getFileNameFromUri
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

class PostAddActivity : AppCompatActivity(), PostPhotoAdapter.OnViewClickListener {
    private val binding by lazy { ActivityPostingBinding.inflate(layoutInflater) }

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(PostRepository()) }
    private val qualificationViewModel: QualificationViewModel by viewModels { ViewModelFactory(QualificationRepository()) }
    private val s3ViewModel: S3ViewModel by viewModels { ViewModelFactory(S3Repository()) }

    private val qualificationAdapter = QualificationAdapter()
    private lateinit var postPhotoAdapter: PostPhotoAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private val boardId = 1
    private var addImageList = mutableListOf<String>()
    private var presignedUrlList = mutableListOf<String>()
    private var s3UrlList = mutableListOf<String>()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            AlertDialog.Builder(this@PostAddActivity).apply {
                setMessage("게시글 작성을 취소하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    finish()
                }
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        setupToolbar()
        setupBottomSheetDialog()
        setupRecyclerView()
        setupView()
        observeViewModel()
    }

    override fun setOnViewClick(position: Int) {
        addImageList.removeAt(position)
        postPhotoAdapter.removeItem(position)
        binding.txtPhotoCount.text = "${postPhotoAdapter.itemCount}/10"
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            AlertDialog.Builder(this@PostAddActivity).apply {
                setMessage("게시글 작성을 취소하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    finish()
                }
                show()
            }
        }
    }

    private fun setupBottomSheetDialog() {
        val bottomSheetBinding = DialogCategoryBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.recyclerView.apply {
            adapter = qualificationAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(this@PostAddActivity, 0, 0, 0, 0, 2, 0, Color.GRAY))
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
        postPhotoAdapter = PostPhotoAdapter(this)

        binding.recyclerView.apply {
            adapter = postPhotoAdapter
            itemAnimator = null
            addItemDecoration(ItemDecoratorDivider(this@PostAddActivity, 0, 0, 0, 6, 0, 0, null))
            setHasFixedSize(true)
        }

        postPhotoAdapter.setOnItemClickListener { _, position ->
            val intent = Intent(this, PhotoViewActivity::class.java)
            intent.putStringArrayListExtra("url", postPhotoAdapter.getItemList() as ArrayList<String>)
            intent.putExtra("position", position)
            intent.putExtra("download", false)
            startActivity(intent)
        }
    }

    private fun setupView() {
        qualificationViewModel.getQualificationList()

        binding.txtTitle.text = "글 작성"
        binding.btnAdd.text = "등록"

        // 이미지 선택
        binding.layoutPhoto.setOnClickListener {
            TedImagePicker.with(this)
                .title("사진 선택")
                .max(10, "사진은 최대 10장까지 선택 가능합니다.")
                .buttonBackground(R.drawable.button_rounded_corner)
                .showCameraTile(false)
                .mediaType(MediaType.IMAGE)
                .selectedUri(postPhotoAdapter.getItemList().map { Uri.parse(it) }) // 이미지 선택 시 기존 선택한 이미지 유지
                .startMultiImage { uriList ->
                    postPhotoAdapter.setItemList(uriList.map { it.toString() })
                    binding.txtPhotoCount.text = "${postPhotoAdapter.itemCount}/10"

                    addImageList.clear()
                    uriList.map { uri ->
                        val fileName = getFileNameFromUri(this, uri)
                        addImageList.add(fileName.toString())
                    }
                }
        }

        // 카테고리 선택
        binding.imgDropDown.setOnClickListener {
            bottomSheetDialog.show()
        }

        // 게시글 등록
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

            if (addImageList.isNotEmpty()) { // 선택한 이미지가 있으면 PresignedUrl 요청
                s3ViewModel.getPresignedUrl(addImageList.map { PresignedUrlRequestDto("post_images", it) })
            } else {
                postViewModel.addPost(PostAddDto(title, content, boardId, category, emptyList()))
            }
        }
    }

    private fun observeViewModel() {
        postViewModel.apply {
            addPost.observe(this@PostAddActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            postView.observe(this@PostAddActivity) {
                binding.editTitle.setText(it.title)
                binding.editContent.setText(it.content)
                binding.txtCategory.text = it.category
            }

            errorMessage.observe(this@PostAddActivity) { event ->
                binding.progressIndicator.visibility = View.GONE
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(this@PostAddActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        qualificationViewModel.apply {
            qualificationList.observe(this@PostAddActivity) { result ->
                binding.txtCategory.text = result.random().name
                qualificationAdapter.setItemList(result)
            }

            errorMessage.observe(this@PostAddActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(this@PostAddActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        s3ViewModel.apply {
            presignedUrl.observe(this@PostAddActivity) { result ->
                val uploadedImageCount = AtomicInteger(0) // 이미지 업로드 완료 카운트

                s3UrlList.addAll(result.map { it.fileUrl }) // S3에 업로드된 이미지 URL
                presignedUrlList.addAll(result.map { it.presignedUrl }).also {
                    CoroutineScope(Dispatchers.Main).launch {
                        val jobs = presignedUrlList.mapIndexed { index, url ->
                            val uri = Uri.parse(postPhotoAdapter.getItemList()[index])
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
                            postViewModel.addPost(PostAddDto(binding.editTitle.text.toString(), binding.editContent.text.toString(), boardId, binding.txtCategory.text.toString(), s3UrlList))
                        }
                    }
                }
            }

            errorMessage.observe(this@PostAddActivity) { event ->
                binding.progressIndicator.visibility = View.GONE
                event.getContentIfNotHandled()?.let { message ->
                    if (!message.lowercase().contains("jwt"))
                        Toast.makeText(this@PostAddActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}