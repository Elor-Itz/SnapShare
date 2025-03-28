package com.example.snapshare

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.snapshare.databinding.ActivityMainBinding
import com.example.snapshare.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.example.snapshare.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    // Get a reference to the UserViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Material Toolbar as ActionBar
        setSupportActionBar(binding.toolbar)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Fetch user data once through ViewModel
            userViewModel.fetchCurrentUser()

            // Navigate to HomeFragment and clear the back stack
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()

            navController.navigate(R.id.homeFragment, null, navOptions)
        }

        // Set up BottomNavigationView with NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide Toolbar and navigation bar for specific fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.signupFragment) {
                supportActionBar?.hide()
                binding.bottomNavigation.visibility = View.GONE // Hide BottomNavigationView
            } else {
                supportActionBar?.show()
                binding.bottomNavigation.visibility = View.VISIBLE // Show BottomNavigationView
            }
        }

        // Add click listener to the profile image in the toolbar
        val profileImageView = findViewById<ImageView>(R.id.topBarProfileImageView)
        profileImageView.setOnClickListener {
            showProfilePopup()
        }

        // Observe the current user LiveData to update the profile image in the top bar
        userViewModel.currentUser.observe(this, { user ->
            if (user != null) {
                // Update the top bar profile image
                updateToolbarProfileImage(user)
            }
        })
    }

    // Show profile popup
    private fun showProfilePopup() {
        // Inflate the popup layout
        val popupView = layoutInflater.inflate(R.layout.profile_popup, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set up the profile picture and name
        val profileImageView = popupView.findViewById<ImageView>(R.id.profileImageView)
        val profileNameTextView = popupView.findViewById<TextView>(R.id.profileNameTextView)
        val logoutButton = popupView.findViewById<Button>(R.id.logoutButton)

        // Observe the current user LiveData from the ViewModel
        userViewModel.currentUser.observe(this, { user ->
            if (user != null) {
                // Populate profile name
                profileNameTextView.text = "${user.firstName} ${user.lastName}"

                // Load profile picture with Picasso
                if (user.profilePictureUrl?.isNotEmpty() == true) {
                    Picasso.get()
                        .load(user.profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(profileImageView)
                } else {
                    profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
                }
            } else {
                // Handle the case where the user is null
                profileNameTextView.text = "No Name"
                profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
            }
        })

        // Handle "Logout" button click
        logoutButton.setOnClickListener {
            auth.signOut() // Log out the user
            popupWindow.dismiss() // Close the popup
            navController.navigate(R.id.loginFragment)
        }

        // Show the popup anchored to the profile menu item
        popupWindow.showAsDropDown(findViewById(R.id.topBarProfileImageView), 0, 0)
    }

    // Update the toolbar profile image
    private fun updateToolbarProfileImage(user: User) {
        val profileImageView = findViewById<ImageView>(R.id.topBarProfileImageView)

        if (user.profilePictureUrl?.isNotEmpty() == true) {
            Picasso.get()
                .load(user.profilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }
}