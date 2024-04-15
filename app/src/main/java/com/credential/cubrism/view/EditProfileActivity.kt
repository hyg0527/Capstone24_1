package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.credential.cubrism.model.repository.AuthRepository
import com.credential.cubrism.viewmodel.AuthViewModel
import com.credential.cubrism.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(AuthRepository()) }
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    private lateinit var bottomProfileDialog: BottomSheetDialog

    private var profileImage: String? = null

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 이미지가 성공적으로 선택되었을 때의 처리
            val data: Intent? = result.data
            val selectedImageUri = data?.data // 선택된 이미지의 URI
            // 선택된 이미지에 대한 작업 수행
            binding.imgProfile.setImageURI(selectedImageUri)
            bottomProfileDialog.dismiss()

            /*
                TODO:
                    이미지를 선택하면 Presigned URL을 요청해서
                    S3에 이미지를 업로드하고 업로드된 이미지 URL을 받아와서
                    프로필 변경 요청시 이미지 URL 포함
             */
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result !is CropImage.CancelledResult) {
            val uriContent = result.uriContent
//            val uriFilePath = result.getUriFilePath(this)
            setProfileImage(uriContent)
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
//                    authViewModel.editUserInfo(
//                        binding.editNickname.text.toString(),
//                        프로필 사진
//                    )
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

        // 프로필 이미지 선택
        bottomSheetBinding.apply {
            btnGallery.setOnClickListener {
                cropImage.launch(
                    CropImageContractOptions(
                        uri = null,
                        cropImageOptions = CropImageOptions(
                            imageSourceIncludeCamera = false,
                            imageSourceIncludeGallery = true,
                            fixAspectRatio = true,
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
        authViewModel.apply {
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

    private fun setProfileImage(uri: Uri?) {
        Glide.with(this).load(uri)
            .dontAnimate()
            .into(binding.imgProfile)
    }
}