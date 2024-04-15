package com.credential.cubrism.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.credential.cubrism.MyApplication
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityEditProfileBinding
import com.credential.cubrism.databinding.DialogProfilePickBinding
import com.credential.cubrism.model.dto.PresignedUrlRequestDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.S3ViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val s3ViewModel: S3ViewModel by viewModels { ViewModelFactory(S3Repository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private lateinit var bottomProfileDialog: BottomSheetDialog

    private var profileImage: String? = null
    private var preSignedUrl: String? = null
    private var filePath: String? = null
    private var fileName: String? = null
    
    // 이미지 크롭
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result !is CropImage.CancelledResult) {
            val uriContent = result.uriContent

            filePath = result.getUriFilePath(this) // 파일 경로
            fileName = uriContent?.lastPathSegment // 파일 이름

            Glide.with(this).load(uriContent).dontAnimate().into(binding.imgProfile)

            bottomProfileDialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupView()
        viewModelObserve()
    }

    private fun setupToolbar() {
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 메뉴 추가
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.edit_profile_menu, menu)
                val editItem = menu.findItem(R.id.edit)
                editItem.setOnMenuItemClickListener {
                    binding.progressIndicator.show()
                    // 2. PreSignedUrl 요청
                    if (fileName != null)
                        s3ViewModel.getPresignedUrl(listOf(PresignedUrlRequestDto("profile_images", fileName!!)))
                    else
                        authViewModel.editUserInfo(binding.editNickname.text.toString(), profileImage)

                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }

    private fun setupView() {
        val bottomSheetBinding = DialogProfilePickBinding.inflate(layoutInflater)
        bottomProfileDialog = BottomSheetDialog(this)
        bottomProfileDialog.setContentView(bottomSheetBinding.root)

        // 1. 프로필 이미지 선택
        bottomSheetBinding.apply {
            btnGallery.setOnClickListener {
                cropImage.launch(
                    CropImageContractOptions(
                        uri = null,
                        cropImageOptions = CropImageOptions(
                            // Camera와 Gallery를 모두 true로 설정하면 선택창 표시
                            imageSourceIncludeCamera = false,
                            imageSourceIncludeGallery = true,
                            fixAspectRatio = true, // 이미지 비율 고정 (1:1)
                            autoZoomEnabled = true,
                            cropShape = CropImageView.CropShape.OVAL,
                            activityBackgroundColor = ResourcesCompat.getColor(resources, R.color.black, theme)
                        )
                    )
                )
            }

            // 기본 프로필 이미지로 변경
            btnResetImage.setOnClickListener {
                Glide.with(this@EditProfileActivity).load(R.drawable.profile)
                    .dontAnimate()
                    .into(binding.imgProfile)
                bottomProfileDialog.dismiss()
            }
        }

        // 프로필 이미지 변경
        binding.imgProfile.setOnClickListener {
            bottomProfileDialog.show()
        }

        // 회원 탈퇴
        binding.btnWithdrawal.setOnClickListener {
            // TODO: 회원탈퇴
        }

        lifecycleScope.launch {
            Glide.with(this@EditProfileActivity).load(dataStore.getProfileImage().first())
                .error(R.drawable.account_circle)
                .fallback(R.drawable.account_circle)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.editEmail.setText(dataStore.getEmail().first())
            binding.editNickname.setText(dataStore.getNickname().first())

            profileImage = dataStore.getProfileImage().first()
        }
    }

    private fun viewModelObserve() {
        s3ViewModel.apply {
            presignedUrl.observe(this@EditProfileActivity) { result ->
                preSignedUrl = result.first().presignedUrl
                profileImage = result.first().fileUrl

                // 3. S3 버킷에 이미지 업로드
                preSignedUrl?.let { url ->
                    filePath?.let { path ->
                        val requestBody = File(path).asRequestBody("image/*".toMediaTypeOrNull())
                        s3ViewModel.uploadImage(url, requestBody)
                    }
                }
            }

            // 4. 유저 정보 수정
            uploadImage.observe(this@EditProfileActivity) {
                authViewModel.editUserInfo(binding.editNickname.text.toString(), profileImage)
            }

            errorMessage.observe(this@EditProfileActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        authViewModel.apply {
            // 5. 유저 정보 수정 성공
            editUserInfo.observe(this@EditProfileActivity) {
                setResult(RESULT_OK).also { finish() }
            }

            errorMessage.observe(this@EditProfileActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}