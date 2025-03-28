package com.example.snapshare.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.snapshare.R
import com.example.snapshare.databinding.FragmentEditProfileBinding
import com.example.snapshare.ui.viewmodel.UserViewModel
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the top and bottom menus
        hideMenus()

        // Observe the user data
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.etFirstName.setText(user.firstName)
                binding.etLastName.setText(user.lastName)
                if (!user.profilePictureUrl.isNullOrEmpty()) {
                    Picasso.get().load(user.profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(binding.profileImageView)
                } else {
                    binding.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                }
            }
        }

        // Save profile changes
        binding.btnSaveProfile.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.updateUserProfile(firstName, lastName).observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navigate back to ProfileFragment
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore the top and bottom menus
        showMenus()
        _binding = null
    }

    private fun hideMenus() {
        // Hide the Toolbar
        requireActivity().findViewById<View>(R.id.toolbar)?.visibility = View.GONE
        // Hide the BottomNavigationView
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.GONE
    }

    private fun showMenus() {
        // Show the Toolbar
        requireActivity().findViewById<View>(R.id.toolbar)?.visibility = View.VISIBLE
        // Show the BottomNavigationView
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE
    }
}