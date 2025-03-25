package com.example.snapshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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

        // Set up BottomNavigationView with NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide Toolbar for specific fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.signupFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        // Define HomeFragment as a top-level destination
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment) // Add other top-level destinations here if needed
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}