package com.credential.cubrism.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
import com.credential.cubrism.model.dto.UserEditDto
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.model.repository.S3Repository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.S3ViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    private val myApplication = MyApplication.getInstance()

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val s3ViewModel: S3ViewModel by viewModels { ViewModelFactory(S3Repository()) }
    private val dataStore = myApplication.getDataStoreRepository()

    private lateinit var bottomProfileDialog: BottomSheetDialog

    private var currentProfileImage: String? = null
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
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.edit -> {
                        binding.progressIndicator.show()

                        // 2. PreSignedUrl 요청
                        if (fileName != null)
                            s3ViewModel.getPresignedUrl(listOf(PresignedUrlRequestDto("profile_images", fileName!!)))
                        else if (profileImage != currentProfileImage)
                            authViewModel.editUserInfo(UserEditDto(binding.editNickname.text.toString(), profileImage, true))
                        else
                            authViewModel.editUserInfo(UserEditDto(binding.editNickname.text.toString(), currentProfileImage, false))
                    }
                }
                return false
            }
        })
    }

    private fun setupView() {
        val bottomSheetBinding = DialogProfilePickBinding.inflate(layoutInflater)
        bottomProfileDialog = BottomSheetDialog(this)
        bottomProfileDialog.setContentView(bottomSheetBinding.root)

        myApplication.getUserData().let {
            Glide.with(this).load(it.getProfileImage())
                .error(R.drawable.profile)
                .fallback(R.drawable.profile)
                .dontAnimate()
                .into(binding.imgProfile)
            binding.editEmail.setText(it.getEmail())
            binding.editNickname.setText(it.getNickname())

            currentProfileImage = it.getProfileImage()
            profileImage = it.getProfileImage()
        }

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
                            activityBackgroundColor = ResourcesCompat.getColor(resources, R.color.black, theme),
                            outputCompressFormat = Bitmap.CompressFormat.PNG // PNG로 추출
                        )
                    )
                )
            }

            // 기본 프로필 이미지로 변경
            btnResetImage.setOnClickListener {
                Glide.with(this@EditProfileActivity)
                    .load(R.drawable.profile)
                    .dontAnimate()
                    .into(binding.imgProfile)
                profileImage = null
                bottomProfileDialog.dismiss()
            }
        }

        // 프로필 이미지 변경
        binding.imgProfile.setOnClickListener {
            bottomProfileDialog.show()
        }

        // 회원 탈퇴
        binding.btnWithdrawal.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("탈퇴 시 모든 정보가 삭제됩니다. 정말 탈퇴하시겠습니까?")
                setNegativeButton("취소", null)
                setPositiveButton("확인") { _, _ ->
                    authViewModel.withdrawal()
                }
                show()
            }
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
                authViewModel.editUserInfo(UserEditDto(binding.editNickname.text.toString(), profileImage, true))
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
                myApplication.getUserData().setUserData(it.email, it.nickname, it.profileImage)
                Toast.makeText(this@EditProfileActivity, "회원 정보를 수정했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }

            withdrawal.observe(this@EditProfileActivity) {
                lifecycleScope.launch {
                    dataStore.deleteAccessToken()
                    dataStore.deleteRefreshToken()

                    myApplication.getUserData().apply {
                        setLoginStatus(false)
                        deleteUserData()
                    }

                    Toast.makeText(this@EditProfileActivity, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            errorMessage.observe(this@EditProfileActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}