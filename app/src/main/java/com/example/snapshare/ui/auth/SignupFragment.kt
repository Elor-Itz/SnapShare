package com.example.snapshare.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val email = binding.etSignupEmail.text.toString()
            val password = binding.etSignupPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("SignupFragment", "Signup successful")
                        if (findNavController().currentDestination?.id != R.id.homeFragment) {
                            findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                        }
                    }
                    .addOnFailureListener { exception ->
                        when (exception) {
                            is FirebaseAuthWeakPasswordException -> {
                                binding.etSignupPassword.error = "Password is too weak"
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                binding.etSignupEmail.error = "Invalid email format"
                            }
                            is FirebaseAuthUserCollisionException -> {
                                binding.etSignupEmail.error = "Email is already in use"
                            }
                            else -> {
                                Log.e("SignupFragment", "Signup failed", exception)
                                Toast.makeText(requireContext(), "Signup Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                if (email.isEmpty()) {
                    binding.etSignupEmail.error = "Email is required"
                }
                if (password.isEmpty()) {
                    binding.etSignupPassword.error = "Password is required"
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}