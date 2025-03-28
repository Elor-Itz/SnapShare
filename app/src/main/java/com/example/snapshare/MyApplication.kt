package com.example.snapshare

import android.app.Application
import com.cloudinary.android.MediaManager
import java.util.Properties

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Load Cloudinary credentials from local.properties
        val properties = Properties()
        try {
            val inputStream = assets.open("local.properties")
            properties.load(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Cloudinary configuration
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = properties.getProperty("cloudinary.cloud_name", "")
        config["api_key"] = properties.getProperty("cloudinary.api_key", "")
        config["api_secret"] = properties.getProperty("cloudinary.api_secret", "")

        MediaManager.init(this, config)
    }
}