package com.dausinvestama.eaterly.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.dausinvestama.eaterly.SellerActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageStoreUtils {
    fun storeImage(imagePreview: ImageView, imagePath: String, documentName: String, context: Context, activity: Activity) {
        Log.d("progress", "Inside $imagePath")

        // Get the cropped bitmap from the imagePreview
        val croppedBitmap = (imagePreview.drawable as BitmapDrawable).bitmap

        // Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("${imagePath}/${documentName}.png")

        // Convert the bitmap to bytes
        val baos = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos)
        val data = baos.toByteArray()

        // Upload the image
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                // Update the Firestore document with the download URL
                updateUrlField(documentName, uri.toString(), context, activity)
            }.addOnFailureListener { e ->
                // Handle the error getting download URL
                e.printStackTrace()
            }
        }.addOnFailureListener { e ->
            // Handle the error
            e.printStackTrace()
            // You can show an error message to the user if needed
        }
    }

    fun updateUrlField(documentName: String, url: String, context: Context, activity: Activity) {
        // Update the URL field in Firestore
        FirebaseFirestore.getInstance().collection("canteens")
            .document(documentName)
            .update("url", url)
            .addOnSuccessListener {
                Toast.makeText(context, "Counter registered.", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, SellerActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            .addOnFailureListener { e ->
                // Handle the error
                e.printStackTrace()
                // You can show an error message to the user if needed
            }
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        // Use application context to avoid leaks on older versions of Android
        val mContext = context.applicationContext
        // Create a random file name using UUID
        val filename = "temp_image_${UUID.randomUUID()}.png"

        // Get the cache directory
        val cacheDir = mContext.cacheDir
        val imageFile = File(cacheDir, filename)

        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(imageFile)
            // Compress the bitmap to PNG format and save to the file output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fileOutputStream?.flush()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Get the Uri using FileProvider
        return FileProvider.getUriForFile(context, "${mContext.packageName}.provider", imageFile)
    }
}