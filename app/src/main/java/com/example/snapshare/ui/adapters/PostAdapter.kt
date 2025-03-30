package com.example.snapshare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.R
import com.example.snapshare.data.model.Post
import com.example.snapshare.data.model.User
import com.squareup.picasso.Picasso

class PostAdapter(
    private val posts: List<Post>,
    private val getUserById: (String) -> LiveData<User?>, // Function to fetch user details as LiveData
    private val onEditPost: (Post) -> Unit,
    private val onDeletePost: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPosterImage: ImageView = itemView.findViewById(R.id.ivPosterImage)
        val tvPosterName: TextView = itemView.findViewById(R.id.tvPosterName)
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val btnPostMenu: ImageButton = itemView.findViewById(R.id.btnPostMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Observe user details
        getUserById(post.userId).observeForever { user ->
            holder.tvPosterName.text = if (user != null) {
                "${user.firstName} ${user.lastName}".trim()
            } else {
                "Unknown"
            }

            if (!user?.profilePictureUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(user.profilePictureUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.ivPosterImage)
            } else {
                holder.ivPosterImage.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }

        // Load the post image
        if (!post.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .fit()
                .centerCrop()
                .into(holder.ivPostImage)
        } else {
            holder.ivPostImage.setImageResource(R.drawable.ic_image_placeholder)
        }

        // Set the post title and content
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content

        // Handle post menu actions
        holder.btnPostMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.btnPostMenu)
            popupMenu.inflate(R.menu.post_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditPost(post)
                        true
                    }
                    R.id.action_delete -> {
                        onDeletePost(post)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int = posts.size
}