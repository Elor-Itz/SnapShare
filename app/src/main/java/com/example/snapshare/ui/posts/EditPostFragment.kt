package com.example.snapshare.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.snapshare.databinding.FragmentEditPostBinding
import com.example.snapshare.utils.MenuUtils.hideMenus
import com.example.snapshare.utils.MenuUtils.showMenus

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the top and bottom menus
        hideMenus()

        // Access views using binding
        binding.btnSavePost.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            // Handle the save post logic here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showMenus()
        _binding = null // Avoid memory leaks
    }
}