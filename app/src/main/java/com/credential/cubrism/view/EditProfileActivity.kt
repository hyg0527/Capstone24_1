package com.credential.cubrism.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.credential.cubrism.R
import com.credential.cubrism.databinding.ActivityEditProfileBinding
import com.credential.cubrism.databinding.DialogProfilePickBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    private lateinit var bottomProfileDialog: BottomSheetDialog
    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 이미지가 성공적으로 선택되었을 때의 처리
            val data: Intent? = result.data
            val selectedImageUri = data?.data // 선택된 이미지의 URI
            // 선택된 이미지에 대한 작업 수행
            binding.imgProfile.setImageURI(selectedImageUri)
            bottomProfileDialog.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bottomSheetBinding = DialogProfilePickBinding.inflate(layoutInflater)
        bottomProfileDialog = BottomSheetDialog(this)
        bottomProfileDialog.setContentView(bottomSheetBinding.root)

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 회원정보 수정
        binding.txtEdit.setOnClickListener {
            // TODO: 회원정보 수정
            Toast.makeText(this, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 프로필 이미지 변경
        binding.btnProfile.setOnClickListener {
            bottomProfileDialog.show()
        }

        // 회원 탈퇴
        binding.btnWithdrawal.setOnClickListener {
            // TODO: 회원탈퇴
        }

        // 프로필 이미지 선택
        bottomSheetBinding.btnGallery.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            pickImagesLauncher.launch(intent)
        }
        // 기본 프로필 이미지로 변경
        bottomSheetBinding.btnResetImage.setOnClickListener {
            binding.imgProfile.setImageResource(R.drawable.profile)
            bottomProfileDialog.dismiss()
        }
    }
}