package com.example.snapshare

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.snapshare.viewmodel.AuthViewModel
import com.example.snapshare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels() // Using ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observe authentication state
        authViewModel.authState.observe(this, Observer { user ->
            if (user != null) {
                binding.statusTextView.text = "Logged in as: ${user.email}"
            } else {
                binding.statusTextView.text = "Not logged in"
            }
        })

        // Register button click
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.registerUser(email, password)
        }

        // Login button click
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.loginUser(email, password)
        }

        // Logout button click
        binding.logoutButton.setOnClickListener {
            authViewModel.logoutUser()
        }
    }
}