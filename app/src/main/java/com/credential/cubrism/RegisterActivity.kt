package com.credential.cubrism

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView

class RegisterActivity : AppCompatActivity() {
    private lateinit var profileImg: CircleImageView
    private lateinit var bottomProfileDialog: BottomSheetDialog
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 이미지가 성공적으로 선택되었을 때의 처리
                val data: Intent? = result.data
                val selectedImageUri = data?.data // 선택된 이미지의 URI
                // 선택된 이미지에 대한 작업 수행
                profileImg.setImageURI(selectedImageUri)
                bottomProfileDialog.dismiss()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_profile_pick, null)

        profileImg = findViewById(R.id.profileImgChoose)
        bottomProfileDialog = BottomSheetDialog(this)
        bottomProfileDialog.setContentView(bottomSheetView)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        val profilePick = findViewById<ImageButton>(R.id.imageChoose)
        val galleryPick = bottomProfileDialog.findViewById<Button>(R.id.btnGallery)
        val cameraPick = bottomProfileDialog.findViewById<Button>(R.id.btnCamera)

        backBtn.setOnClickListener {
            finish()
        }
        profilePick.setOnClickListener {
            bottomProfileDialog.show()
        }
        galleryPick?.setOnClickListener { // 갤러리 버튼 누르면 imagepicker 호출
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            pickImagesLauncher.launch(intent)
        }
        cameraPick?.setOnClickListener { // 카메라 버튼 누르면 카메라앱 호출
            Toast.makeText(this, "camera clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}