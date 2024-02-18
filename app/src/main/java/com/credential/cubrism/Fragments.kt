package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudyFragment : Fragment(R.layout.fragment_study) {

}

class CalFragment : Fragment(R.layout.fragment_cal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음 화면을 fragment_cal_month 으로 설정
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.calFragmentContainerView, CalMonthFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }
}

class CalMonthFragment : Fragment(R.layout.fragment_cal_month) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val currentDate = view.findViewById<TextView>(R.id.txtCurrentDate)

        // 날짜 누르면 날짜를 textview에 출력
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            currentDate.text = selectedDate
        }

        // 처음 생성시 오늘 날짜 기본 출력
        val today = Calendar.getInstance().time
        val todayDateFormat = SimpleDateFormat("yyyy년 M월 dd일", Locale.getDefault())
        val todayString = todayDateFormat.format(today)

        currentDate.text = todayString
    }
}

class CalWeekFragment : Fragment(R.layout.fragment_cal_week) {

}

class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileFix = view.findViewById<CircleImageView>(R.id.circle1)

        profileFix.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileFixActivity::class.java)
            startActivity(intent)
        }
    }
}