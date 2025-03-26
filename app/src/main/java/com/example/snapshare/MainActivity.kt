package com.example.snapshare

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.snapshare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Material Toolbar as ActionBar
        setSupportActionBar(binding.toolbar) // Ensure this is called

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()        

        // Set up Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Check if the user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
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

        // Define HomeFragment as a top-level destination
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment) // Add other top-level destinations here if needed
        )
    }

    // Navigate up
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Profile menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    // Profile options select
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                showProfilePopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

        // Set the user's profile picture and name (replace with actual user data)
        profileImageView.setImageResource(R.drawable.ic_profile_placeholder) // Replace with actual image loading logic
        profileNameTextView.text = auth.currentUser?.displayName ?: "User Name"

        // Handle "Logout" button click
        logoutButton.setOnClickListener {
            auth.signOut() // Log out the user
            popupWindow.dismiss() // Close the popup
            navController.navigate(R.id.loginFragment)
        }

        // Show the popup anchored to the profile menu item
        popupWindow.showAsDropDown(findViewById(R.id.action_profile), 0, 0)
    }
}