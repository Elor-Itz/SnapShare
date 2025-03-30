package com.example.snapshare.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.example.snapshare.R

object MenuUtils {

    // Hide the Toolbar and BottomNavigationView
    fun Fragment.hideMenus() {
        requireActivity().findViewById<View>(R.id.toolbar)?.visibility = View.GONE
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.GONE
    }

    // Show the Toolbar and BottomNavigationView
    fun Fragment.showMenus() {
        requireActivity().findViewById<View>(R.id.toolbar)?.visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.bottomNavigation)?.visibility = View.VISIBLE
    }
}