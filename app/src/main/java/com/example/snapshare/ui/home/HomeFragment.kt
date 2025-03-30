package com.example.snapshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapshare.adapter.PostAdapter
import com.example.snapshare.R
import com.example.snapshare.data.model.Post
import com.example.snapshare.databinding.FragmentHomeBinding
import com.example.snapshare.viewmodel.PostViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Initialize RecyclerView
        postAdapter = PostAdapter(posts)
        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Observe posts LiveData from ViewModel
        postViewModel.allPosts.observe(viewLifecycleOwner) { fetchedPosts ->
            posts.clear()
            if (fetchedPosts.isEmpty()) {
                // Show "No posts available" message
                binding.tvNoPosts.visibility = View.VISIBLE
                binding.recyclerViewPosts.visibility = View.GONE
            } else {
                // Show posts in RecyclerView
                binding.tvNoPosts.visibility = View.GONE
                binding.recyclerViewPosts.visibility = View.VISIBLE
                posts.addAll(fetchedPosts)
                postAdapter.notifyDataSetChanged()
            }
        }

        // Load posts from Firestore and cache them in the local database
        loadPosts()

        // Set up Floating Action Button to navigate to CreatePostFragment
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
        }
    }

    private fun loadPosts() {
        // Fetch posts from Firestore and cache them in the local database
        postViewModel.fetchPostsFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}