package com.credential.cubrism

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView


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