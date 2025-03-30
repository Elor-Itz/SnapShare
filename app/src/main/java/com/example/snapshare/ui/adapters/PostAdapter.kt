package com.example.snapshare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.R
import com.example.snapshare.data.model.Post
import com.example.snapshare.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter(
    private val posts: List<Post>,
    private val onEditPost: (Post) -> Unit,
    private val onDeletePost: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvTitle.text = post.title
            tvContent.text = post.content

            // Load the post image
            if (!post.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(post.imageUrl).into(ivPostImage)
            } else {
                ivPostImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Handle menu button click
            btnPostMenu.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.inflate(R.menu.post_menu) // Create a menu resource file for this
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            onEditPost(post) // Trigger the edit callback
                            true
                        }
                        R.id.action_delete -> {
                            onDeletePost(post) // Trigger the delete callback
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun getItemCount(): Int = posts.size
}