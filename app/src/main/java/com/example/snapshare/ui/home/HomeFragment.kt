package com.example.snapshare.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewBinding
        binding = FragmentHomeBinding.bind(view)

        // Display user data or content on the home screen
        binding.welcomeText.text = "Welcome to SnapShare!"
        // Any additional functionality goes here
    }
}