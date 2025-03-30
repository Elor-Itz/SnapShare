package com.example.snapshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapshare.adapter.PostAdapter
import com.example.snapshare.R
import com.example.snapshare.data.local.AppDatabase
import com.example.snapshare.data.model.Post
import com.example.snapshare.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        postAdapter = PostAdapter(posts)
        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Load posts from the database
        loadPostsFromDatabase()

        // Set up Floating Action Button to navigate to CreatePostFragment
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
        }
    }

    private fun loadPostsFromDatabase() {
        // Use a coroutine to fetch posts from the database
        CoroutineScope(Dispatchers.IO).launch {
            val postDao = AppDatabase.getDatabase(requireContext()).postDao()
            val fetchedPosts = postDao.getAllPosts() // Fetch posts from the database

            withContext(Dispatchers.Main) {
                posts.clear()
                posts.addAll(fetchedPosts)
                postAdapter.notifyDataSetChanged() // Notify the adapter of data changes
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}