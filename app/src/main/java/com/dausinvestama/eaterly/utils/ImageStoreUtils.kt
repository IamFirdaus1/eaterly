package com.dausinvestama.eaterly.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.dausinvestama.eaterly.SellerActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

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
}