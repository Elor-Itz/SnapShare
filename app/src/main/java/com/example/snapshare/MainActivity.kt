package com.example.snapshare

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.snapshare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    // Cached user data
    private var cachedFirstName: String? = null
    private var cachedLastName: String? = null
    private var cachedProfilePictureUrl: String? = null

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
            // Fetch user data once and cache it
            fetchAndCacheUserData(currentUser.uid)

            // Navigate to HomeFragment if the user is logged in
            navController.navigate(R.id.homeFragment)
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
    }

    // Fetch and cache user data from Firestore
    private fun fetchAndCacheUserData(userId: String) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    cachedFirstName = document.getString("firstName") ?: "No Name"
                    cachedLastName = document.getString("lastName") ?: ""
                    cachedProfilePictureUrl = document.getString("profilePictureUrl") ?: ""

                    // Update the toolbar profile image
                    updateToolbarProfileImage()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Update the toolbar profile image
    private fun updateToolbarProfileImage() {
        val profileImageView = findViewById<ImageView>(R.id.topBarProfileImageView)

        if (cachedProfilePictureUrl?.isNotEmpty() == true) {
            Picasso.get()
                .load(cachedProfilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
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

        // Use cached data to populate the popup
        profileNameTextView.text = "${cachedFirstName ?: "No Name"} ${cachedLastName ?: ""}"
        if (cachedProfilePictureUrl?.isNotEmpty() == true) {
            Picasso.get()
                .load(cachedProfilePictureUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // Handle "Logout" button click
        logoutButton.setOnClickListener {
            auth.signOut() // Log out the user
            popupWindow.dismiss() // Close the popup
            navController.navigate(R.id.loginFragment)
        }

        // Show the popup anchored to the profile menu item
        popupWindow.showAsDropDown(findViewById(R.id.topBarProfileImageView), 0, 0)
    }
}