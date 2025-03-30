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
import com.example.snapshare.databinding.FragmentEditPostBinding
import com.example.snapshare.utils.CloudinaryUploader
import com.example.snapshare.utils.MenuUtils.hideMenus
import com.example.snapshare.utils.MenuUtils.showMenus
import com.example.snapshare.viewmodel.PostViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private val postViewModel: PostViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var cloudinaryUploader: CloudinaryUploader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        cloudinaryUploader = CloudinaryUploader(requireContext()) // Initialize CloudinaryUploader
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the top and bottom menus
        hideMenus()

        // Retrieve the "id" argument from the Bundle
        val id = arguments?.getString("id") // Retrieve the "id" argument
        if (id != null) {
            // Load the post details
            postViewModel.fetchPostById(id)
            postViewModel.currentPost.observe(viewLifecycleOwner) { post ->
                if (post != null) {
                    binding.etTitle.setText(post.title)
                    binding.etContent.setText(post.content)

                    // Load the current image using Picasso
                    if (!post.imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(post.imageUrl) // Load the image URL
                            .placeholder(R.drawable.ic_image_placeholder) // Placeholder while loading
                            .error(R.drawable.ic_image_placeholder) // Error image if loading fails
                            .into(binding.ivSelectedImage) // Set the image into the ImageView
                    } else {
                        binding.ivSelectedImage.setImageResource(R.drawable.ic_image_placeholder) // Default placeholder
                    }

                    // Store the current image URL in selectedImageUri
                    selectedImageUri = post.imageUrl?.let { Uri.parse(it) }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Error: Post ID is missing", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp() // Navigate back if no ID is provided
        }

        // Handle image selection
        binding.btnSelectImage.setOnClickListener {
            pickImage()
        }

        // Handle saving the updated post
        binding.btnSavePost.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null && selectedImageUri.toString() != postViewModel.currentPost.value?.imageUrl) {
                // If a new image is selected, upload it and save the updated post
                uploadImageAndSavePost(id!!, title, content)
            } else {
                // Save the updated post without uploading a new image
                savePost(id!!, title, content, postViewModel.currentPost.value?.imageUrl)
            }
        }

        // Navigate back to HomeFragment
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editPostFragment_to_homeFragment)
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
    private fun uploadImageAndSavePost(id: String, title: String, content: String) {
        if (selectedImageUri == null) {
            savePost(id, title, content, null)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the CloudinaryUploader's uploadImage function
                uploadedImageUrl = cloudinaryUploader.uploadImage(selectedImageUri!!)

                withContext(Dispatchers.Main) {
                    if (uploadedImageUrl != null) {
                        // Save the updated post with the uploaded image URL
                        savePost(id, title, content, uploadedImageUrl!!)
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
    private fun savePost(id: String, title: String, content: String, imageUrl: String?) {
        val updatedPost = postViewModel.currentPost.value?.copy(
            title = title,
            content = content,
            imageUrl = imageUrl ?: postViewModel.currentPost.value?.imageUrl
        )

        if (updatedPost != null) {
            postViewModel.updatePost(updatedPost).observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Post updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_editPostFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Failed to update post.", Toast.LENGTH_SHORT).show()
                }
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