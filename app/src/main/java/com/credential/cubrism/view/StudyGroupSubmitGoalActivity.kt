package com.credential.cubrism.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivitySubmitGoalBinding
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.dto.StudyGroupGoalSubmitDto
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.model.repository.StudyGroupRepository
import com.credential.cubrism.viewmodel.S3ViewModel
import com.credential.cubrism.viewmodel.StudyGroupViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class StudyGroupSubmitGoalActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySubmitGoalBinding.inflate(layoutInflater) }

    private val s3ViewModel: S3ViewModel by viewModels { ViewModelFactory(S3Repository()) }
    private val studyGroupViewModel: StudyGroupViewModel by viewModels { ViewModelFactory(StudyGroupRepository()) }

    private val imm by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    private val groupId by lazy { intent.getIntExtra("groupId", -1)}
    private val goalId by lazy { intent.getIntExtra("goalId", -1)}

    private var preSignedUrl: String? = null
    private var fileUrl: String? = null
    private var filePath: String? = null
    private var fileName: String? = null

    // 이미지 크롭
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result !is CropImage.CancelledResult) {
            val uriContent = result.uriContent

            filePath = result.getUriFilePath(this) // 파일 경로
            fileName = uriContent?.lastPathSegment // 파일 이름

            Glide.with(this)
                .load(uriContent)
                .into(binding.imgPhoto)

            binding.imgPhoto.visibility = View.VISIBLE
            binding.layoutAddPhoto.visibility = View.GONE
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            AlertDialog.Builder(this@StudyGroupSubmitGoalActivity).apply {
                setMessage("목표 인증을 취소하시겠습니까?")
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
        setupView()
        observeViewwModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            AlertDialog.Builder(this@StudyGroupSubmitGoalActivity).apply {
                setMessage("목표 인증을 취소하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    finish()
                }
                show()
            }
        }
    }

    private fun setupView() {
        binding.layoutAddPhoto.setOnClickListener {
            cropImage.launch(
                CropImageContractOptions(
                    uri = null,
                    cropImageOptions = CropImageOptions(
                        imageSourceIncludeCamera = true,
                        imageSourceIncludeGallery = true,
                        fixAspectRatio = false,
                        autoZoomEnabled = true,
                        cropShape = CropImageView.CropShape.RECTANGLE,
                        activityBackgroundColor = ResourcesCompat.getColor(resources, R.color.black, theme),
                        outputCompressFormat = Bitmap.CompressFormat.PNG // PNG로 추출
                    )
                )
            )
        }

        binding.btnSubmit.setOnClickListener {
            if (fileName == null) {
                Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.editContent.text?.trim()?.isBlank() == true) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fileName?.let {
                binding.progressIndicator.show()
                imm.hideSoftInputFromWindow(binding.editContent.windowToken, 0)
                s3ViewModel.getPresignedUrl(listOf(PresignedUrlRequestDto("studygroup_images", it)))
            }
        }
    }

    private fun observeViewwModel() {
        s3ViewModel.apply {
            presignedUrl.observe(this@StudyGroupSubmitGoalActivity) { result ->
                preSignedUrl = result.first().presignedUrl
                fileUrl = result.first().fileUrl

                preSignedUrl?.let { url ->
                    filePath?.let { path ->
                        val requestBody = File(path).asRequestBody("image/*".toMediaTypeOrNull())
                        s3ViewModel.uploadImage(url, requestBody)
                    }
                }
            }

            uploadImage.observe(this@StudyGroupSubmitGoalActivity) {
                if (goalId != -1 && groupId != -1 && fileUrl != null)
                    studyGroupViewModel.submitGoal(StudyGroupGoalSubmitDto(goalId, groupId, binding.editContent.text.toString(), fileUrl!!))
            }

            errorMessage.observe(this@StudyGroupSubmitGoalActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this@StudyGroupSubmitGoalActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        studyGroupViewModel.apply {
            submitGoal.observe(this@StudyGroupSubmitGoalActivity) {
                Toast.makeText(this@StudyGroupSubmitGoalActivity, it.message, Toast.LENGTH_SHORT).show()
                finish()
            }

            errorMessage.observe(this@StudyGroupSubmitGoalActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this@StudyGroupSubmitGoalActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}