package com.example.snapshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentPostListBinding
import com.example.snapshare.viewmodel.PostViewModel
import com.example.snapshare.viewmodel.PostViewModelFactory

class PostListFragment : Fragment() {
    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val adapter = PostAdapter { post ->
            // Navigate to EditPostFragment with the selected post's ID
            val action = PostListFragmentDirections.actionPostListFragmentToEditPostFragment(post.postId)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Observe posts from ViewModel
        postViewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            posts?.let { adapter.submitList(it) }
        }

        // Navigate to CreatePostFragment when FAB is clicked
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_postListFragment_to_createPostFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}