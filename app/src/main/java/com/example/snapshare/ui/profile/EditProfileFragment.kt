package com.example.snapshare.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentEditProfileBinding
import com.example.snapshare.utils.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cloudinaryUploader: CloudinaryUploader


    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileImageView.setImageURI(it)
                uploadImageToCloudinaryAndSave(it)
            } ?: Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cloudinaryUploader = CloudinaryUploader(requireContext())

        val user = auth.currentUser
        user?.let {
            loadUserProfile(it.uid)
        }

        binding.btnUploadImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSaveProfile.setOnClickListener {
            saveUserProfile()
        }
    }

    // Load user profile data
    private fun loadUserProfile(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val profilePictureUrl = document.getString("profilePictureUrl") ?: ""

                    binding.etFirstName.setText(firstName)
                    binding.etLastName.setText(lastName)
                    if (profilePictureUrl.isNotEmpty()) {
                        Picasso.get().load(profilePictureUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(binding.profileImageView)
                    }
                }
            }
    }

    // Upload image to Cloudinary and save URL to Firestore
    private fun uploadImageToCloudinaryAndSave(imageUri: Uri) {
        lifecycleScope.launch {
            val user = auth.currentUser ?: return@launch
            try {
                // Use CloudinaryUploader to upload the image
                val imageUrl = cloudinaryUploader.uploadImage(imageUri)
                if (imageUrl != null) {
                    saveImageUrlToFirestore(user.uid, imageUrl)
                } else {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save image URL
    private fun saveImageUrlToFirestore(userId: String, imageUrl: String) {
        firestore.collection("users").document(userId)
            .update("profilePictureUrl", imageUrl)
            .addOnSuccessListener {
                Picasso.get().load(imageUrl).into(binding.profileImageView)
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show()
            }
    }

    // Save user profile
    private fun saveUserProfile() {
        val user = auth.currentUser ?: return
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()

        firestore.collection("users").document(user.uid)
            .update(
                mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}