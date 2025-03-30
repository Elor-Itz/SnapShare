package com.example.snapshare.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapshare.R
import com.example.snapshare.adapter.PostAdapter
import com.example.snapshare.data.model.Post
import com.example.snapshare.data.model.User
import com.example.snapshare.databinding.FragmentHomeBinding
import com.example.snapshare.viewmodel.PostViewModel
import com.example.snapshare.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()
    private lateinit var postViewModel: PostViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Define the getUserById function using UserViewModel
        val getUserById: (String) -> LiveData<User?> = { userId ->
            userViewModel.getUserById(userId)
        }

        // Initialize the adapter
        postAdapter = PostAdapter(
            posts = posts,
            getUserById = getUserById, // Pass the getUserById function
            onEditPost = { post ->
                // Navigate to EditPostFragment with the post ID
                val bundle = Bundle().apply {
                    putString("id", post.id)
                }
                findNavController().navigate(R.id.action_homeFragment_to_editPostFragment, bundle)
            },
            onDeletePost = { post ->
                // Handle post deletion
                postViewModel.deletePost(post).observe(viewLifecycleOwner) { success ->
                    if (success) {
                        Toast.makeText(requireContext(), "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                        loadPosts() // Refresh the posts
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete post.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        // Set up RecyclerView
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

    recyclerViewPosts.addItemDecoration(object : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        // Add extra space only at the bottom of the last item
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            outRect.bottom = 100 // Adjust the value as needed
        }
    }
})
}