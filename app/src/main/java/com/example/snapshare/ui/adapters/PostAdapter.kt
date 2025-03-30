package com.example.snapshare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.data.model.Post
import com.example.snapshare.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // ViewHolder class using ViewBinding
    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            // Set the title and content
            binding.tvTitle.text = post.title
            binding.tvContent.text = post.content

            // Load the image using Picasso
            if (!post.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(post.imageUrl) // Load the image URL
                    .placeholder(com.example.snapshare.R.drawable.ic_image_placeholder) // Placeholder image
                    .error(com.example.snapshare.R.drawable.ic_image_placeholder) // Error image
                    .into(binding.ivPostImage) // Target ImageView
            } else {
                // Set a placeholder if the image URL is empty or null
                binding.ivPostImage.setImageResource(com.example.snapshare.R.drawable.ic_image_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}