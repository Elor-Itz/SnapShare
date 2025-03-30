package com.example.snapshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.snapshare.databinding.FragmentEditPostBinding
import com.example.snapshare.viewmodel.PostViewModel
import com.example.snapshare.viewmodel.PostViewModelFactory

class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val args: EditPostFragmentArgs by navArgs()

    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the post to edit
        postViewModel.fetchPostById(args.postId)
        postViewModel.currentPost.observe(viewLifecycleOwner) { post ->
            post?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
            }
        }

        binding.btnSavePost.setOnClickListener {
            val updatedTitle = binding.etTitle.text.toString()
            val updatedContent = binding.etContent.text.toString()

            postViewModel.currentPost.value?.let { post ->
                val updatedPost = post.copy(
                    title = updatedTitle,
                    content = updatedContent
                )
                postViewModel.updatePost(updatedPost).observe(viewLifecycleOwner) { success ->
                    if (success) {
                        findNavController().navigateUp() // Navigate back to PostListFragment
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}