package com.example.snapshare.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentEditProfileBinding
import com.example.snapshare.utils.CloudinaryUploader
import com.example.snapshare.utils.MenuUtils.hideMenus
import com.example.snapshare.utils.MenuUtils.showMenus
import com.example.snapshare.viewmodel.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var cloudinaryUploader: CloudinaryUploader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        cloudinaryUploader = CloudinaryUploader(requireContext()) // Initialize CloudinaryUploader
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the top and bottom menus
        hideMenus()

        // Observe the user data
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.etFirstName.setText(user.firstName)
                binding.etLastName.setText(user.lastName)
                if (!user.profilePictureUrl.isNullOrEmpty()) {
                    Picasso.get().load(user.profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(binding.profileImageView)
                } else {
                    binding.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                }
            }
        }

        // Handle profile image selection
        binding.btnUploadImage.setOnClickListener {
            pickImage()
        }

        // Save profile changes
        binding.btnSaveProfile.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                // Upload the image and save the profile
                uploadImageAndSaveProfile(firstName, lastName)
            } else {
                // Save the profile without uploading an image
                saveProfile(firstName, lastName, null)
            }
        }

        // Navigate back to ProfileFragment
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.profileImageView.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun uploadImageAndSaveProfile(firstName: String, lastName: String) {
        if (selectedImageUri == null) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the CloudinaryUploader's uploadImage function
                uploadedImageUrl = cloudinaryUploader.uploadImage(selectedImageUri!!)

                withContext(Dispatchers.Main) {
                    if (uploadedImageUrl != null) {
                        // Save the profile with the uploaded image URL
                        saveProfile(firstName, lastName, uploadedImageUrl!!)
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfile(firstName: String, lastName: String, imageUrl: String?) {
        userViewModel.updateUserProfile(firstName, lastName, imageUrl).observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
            } else {
                Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the top and bottom menus
        showMenus()
        _binding = null
    }
}