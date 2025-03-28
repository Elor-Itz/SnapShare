package com.example.snapshare.utils

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryUploader(private val context: Context) {

    suspend fun uploadImage(imageUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Use suspendCancellableCoroutine to handle the asynchronous upload
                suspendCancellableCoroutine { continuation ->
                    MediaManager.get().upload(imageUri)
                        .option("folder", "snapshare") // Optional: Specify a folder in Cloudinary
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String?) {
                                // Upload started
                            }

                            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                                // Upload progress
                            }

                            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                                // Upload successful, return the secure URL
                                val secureUrl = resultData?.get("secure_url") as? String
                                continuation.resume(secureUrl)
                            }

                            override fun onError(requestId: String?, error: ErrorInfo?) {
                                // Upload failed, resume with an exception
                                continuation.resumeWithException(Exception(error?.description))
                            }

                            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                                // Upload rescheduled
                                continuation.resumeWithException(Exception("Upload rescheduled: ${error?.description}"))
                            }
                        })
                        .dispatch()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}