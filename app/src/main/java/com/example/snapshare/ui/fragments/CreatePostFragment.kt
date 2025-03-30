package com.example.snapshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.snapshare.databinding.FragmentCreatePostBinding
import com.example.snapshare.viewmodel.PostViewModel
import com.example.snapshare.viewmodel.PostViewModelFactory
import com.example.snapshare.data.model.Post

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSavePost.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()
            val imageUrl = "" // Add logic to upload image to Cloudinary and get URL

            val post = Post(
                postId = System.currentTimeMillis().toString(), // Generate unique ID
                userId = "currentUserId", // Replace with actual user ID
                title = title,
                content = content,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )

            postViewModel.insertPost(post).observe(viewLifecycleOwner) { success ->
                if (success) {
                    findNavController().navigateUp() // Navigate back to PostListFragment
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}