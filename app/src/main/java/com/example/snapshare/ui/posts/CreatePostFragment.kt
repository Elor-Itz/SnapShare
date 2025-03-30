package com.example.snapshare.ui.posts

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
import com.example.snapshare.databinding.FragmentCreatePostBinding
import com.example.snapshare.utils.CloudinaryUploader
import com.example.snapshare.utils.MenuUtils.hideMenus
import com.example.snapshare.utils.MenuUtils.showMenus
import com.example.snapshare.viewmodel.PostViewModel
import com.example.snapshare.data.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val postViewModel: PostViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var cloudinaryUploader: CloudinaryUploader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        cloudinaryUploader = CloudinaryUploader(requireContext()) // Initialize CloudinaryUploader
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the top and bottom menus
        hideMenus()

        // Handle image selection
        binding.btnSelectImage.setOnClickListener {
            pickImage()
        }

        // Handle post saving
        binding.btnSavePost.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                // Upload the image and save the post
                uploadImageAndSavePost(title, content)
            } else {
                // Save the post without an image
                savePost(title, content, null)
            }
        }

        // Navigate back to HomeFragment
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
        }
    }

    // Image picker
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
                binding.ivSelectedImage.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }

    // Upload image and save post
    private fun uploadImageAndSavePost(title: String, content: String) {
        if (selectedImageUri == null) {
            // If no image is selected, save the post without an image
            savePost(title, content, null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the CloudinaryUploader's uploadImage function
                uploadedImageUrl = cloudinaryUploader.uploadImage(selectedImageUri!!)

                withContext(Dispatchers.Main) {
                    if (uploadedImageUrl != null) {
                        // Save the post with the uploaded image URL
                        savePost(title, content, uploadedImageUrl!!)
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

    // Save post
    private fun savePost(title: String, content: String, imageUrl: String?) {
        val userId = "currentUserId" // Replace with the actual user ID from FirebaseAuth or your app logic
        val postId = "" // Firestore will generate the ID
        val timestamp = System.currentTimeMillis() // Use the current system time

        val post = Post(
            id = postId,
            userId = userId,
            title = title,
            content = content,
            imageUrl = imageUrl,
            timestamp = timestamp
        )

        postViewModel.insertPost(post).observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createPostFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Failed to create post.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the top and bottom menus
        showMenus()
        _binding = null // Avoid memory leaks
    }
}