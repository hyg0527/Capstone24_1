package com.credential.cubrism

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.View
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit


// 추가한 코드 설명 : 인증 요청 버튼을 누르면 인증 안내 문구가 뜨며 카운트다운 시작.
// 유효 시간은 5분이며 인증 코드와 동일한 문구를 입력해야만 인증 미완료 문구가 인증 완료 문구로 바뀐다.
// 유효 시간이 다 되어도 인증 미완료 시 재인증 버튼이 활성화됨.
// 원활한 테스트를 위해 "A"라는 문구 입력 시 인증이 완료되게끔 임의로 코드를 작성해 두었음.
// 인증이 완료되었다면 재인증 버튼이 활성화되지 않음.


class RegisterActivity : AppCompatActivity() {

    private lateinit var countTxt: TextView
    private lateinit var validBtn: Button
    private lateinit var noticeTxt : TextView
    private var countDown: CountDownTimer? = null
    private var isTimerRunning = false
    private var isValidEmail = false

    /*
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

     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        validBtn = findViewById(R.id.codeBtn)
        countTxt = findViewById(R.id.countDown)
        noticeTxt = findViewById(R.id.isvalidCode)
        val checkEmailTxt = findViewById<EditText>(R.id.emailCode)

        backBtn.setOnClickListener {
            finish()
        }

        validBtn.setOnClickListener {
            checkEmailTxt.setBackgroundResource(R.drawable.edittext_rounded_corner_red)
            validBtn.setBackgroundResource(R.drawable.button_rounded_corner_lightblue)
            validBtn.setEnabled(false);
            startCountdownTimer()
            noticeTxt.text = "✓ 유효하지 않은 인증 코드입니다."

            checkEmailTxt.setHint("인증 코드");

            if (countTxt.getVisibility() == View.INVISIBLE) {
                countTxt.setVisibility(View.VISIBLE);
            }

            if (noticeTxt.getVisibility() == View.GONE) {
                noticeTxt.setVisibility(View.VISIBLE);
            }

        }

        checkEmailTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                // 입력 전에 수행할 작업
            }

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                // 입력이 변경될 때마다 수행할 작업
                if (charSequence.toString() == "A") {
                    noticeTxt.text = "✓ 인증이 완료되었습니다."
                    noticeTxt.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.green))
                    checkEmailTxt.setBackgroundResource(R.drawable.edittext_rounded_corner)
                    isValidEmail = true
                } else {
                    // "B"가 입력되지 않았다면 기본 텍스트와 기본 색상으로 변경
                    noticeTxt.text = "✓ 유효하지 않은 인증 코드입니다."
                    noticeTxt.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.red2))
                    checkEmailTxt.setBackgroundResource(R.drawable.edittext_rounded_corner_red)
                    isValidEmail = false
                }
            }

            override fun afterTextChanged(editable: Editable) {
                // 입력 후에 수행할 작업
            }
        })



        /*
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_profile_pick, null)

        profileImg = findViewById(R.id.changeImage)
        bottomProfileDialog = BottomSheetDialog(this)
        bottomProfileDialog.setContentView(bottomSheetView)

        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        val profilePick = findViewById<ImageButton>(R.id.chooseProfile)
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
        */
    }


    private fun startCountdownTimer() {
        countDown?.cancel() // 기존의 타이머가 있다면 취소
        
        countDown = object : CountDownTimer(300000, 1000) {
            // 유효시간 5분
            override fun onTick(millisUntilFinished: Long) {
                val minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(minute)

                val displayTime = String.format("%02d:%02d", minute, second)
                countTxt.text = "$displayTime"
            }

            override fun onFinish() {
                isTimerRunning = false
                if (isValidEmail==false) {
                    validBtn.text="재인증"
                    validBtn.setBackgroundResource(R.drawable.button_rounded_corner)
                    validBtn.setEnabled(true);
                    noticeTxt.text = "✓ 재인증이 필요합니다."
                }
            }
        }
        
        countDown?.start()
        isTimerRunning = true
    }

}