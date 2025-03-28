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
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

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
        firestore = FirebaseFirestore.getInstance()

        binding.btnSignup.setOnClickListener {
            val firstName = binding.etSignupFirstName.text.toString()
            val lastName = binding.etSignupLastName.text.toString()
            val email = binding.etSignupEmail.text.toString()
            val password = binding.etSignupPassword.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d("SignupFragment", "Signup successful")

                        // Update the user's display name with first and last name
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = "$firstName $lastName"
                        }
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("SignupFragment", "User profile updated")
                                }
                            }

                        // Store user data in Firestore
                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "email" to email,
                            "uid" to user?.uid
                        )
                        firestore.collection("users").document(user!!.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("SignupFragment", "User data saved to Firestore")
                            }
                            .addOnFailureListener { e ->
                                Log.e("SignupFragment", "Error saving user data", e)
                                Toast.makeText(requireContext(), "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }

                        // Navigate to HomeFragment
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
                if (firstName.isEmpty()) {
                    binding.etSignupFirstName.error = "First Name is required"
                }
                if (lastName.isEmpty()) {
                    binding.etSignupLastName.error = "Last Name is required"
                }
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