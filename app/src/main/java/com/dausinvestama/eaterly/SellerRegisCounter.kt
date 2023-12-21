package com.dausinvestama.eaterly

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.Manifest
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dausinvestama.eaterly.databinding.ActivitySellerRegisCounterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import android.media.ExifInterface
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream


class SellerRegisCounter : AppCompatActivity() {
    private lateinit var binding: ActivitySellerRegisCounterBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var buttonBack: ImageButton
    private lateinit var imagePreview: ImageView
    private lateinit var buttonImage: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerRegisCounterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        buttonBack = binding.buttonBack
        imagePreview = binding.imagePreview
        buttonImage = binding.buttonImage
        buttonSubmit = binding.buttonSubmit
        editTextName = binding.editTextCounterName
        editTextDesc = binding.editTextCounterDesc

        buttonBack.setOnClickListener {
            Intent(this@SellerRegisCounter, SellerActivity::class.java).also {
                startActivity(it)
            }
        }

        setupRadioGroup()

        buttonImage.setOnClickListener {
            checkCameraPermission()
        }

        buttonSubmit.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val sbhChecked = checkedId == R.id.radioButtonSBH
            val nbhChecked = checkedId == R.id.radioButtonNBH

            if (sbhChecked) {
                binding.radioButtonNBH.isChecked = false
            } else if (nbhChecked) {
                binding.radioButtonSBH.isChecked = false
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            showImagePickDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickDialog()
            } else {
                val pickPhotoIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent, PICK_IMAGE)
            }
        }
    }

    private fun showImagePickDialog() {
        val items = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(items) { _, which ->
            when (which) {
                0 -> {
                    // Take Photo option selected
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
                1 -> {
                    // Choose from Gallery option selected
                    val pickPhotoIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, PICK_IMAGE)
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Check if the data Intent is not null
                    if (data != null) {
                        // Get the captured image data
                        val extras: Bundle? = data.extras
                        if (extras != null && extras.containsKey("data")) {
                            // Get the bitmap from the data
                            val bitmap: Bitmap = extras.get("data") as Bitmap
                            // Crop the image to 1:1
                            val croppedBitmap = cropToSquare(bitmap)
                            // Set the cropped image to the preview
                            setPreviewImage(croppedBitmap)
                        }
                    }
                }
                PICK_IMAGE -> {
                    val selectedImageUri: Uri? = data?.data
                    // Crop the image to 1:1
                    val croppedBitmap = cropImageToSquare(selectedImageUri)
                    // Set the cropped image to the preview
                    setPreviewImage(croppedBitmap)
                }
            }
        }
    }

    private fun cropImageToSquare(imageUri: Uri?): Bitmap? {
        imageUri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                val exif = ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                // Rotate the bitmap based on the orientation
                val rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }

                return cropToSquare(rotatedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val size = if (width > height) height else width
        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    private fun setPreviewImage(bitmap: Bitmap?) {
        bitmap?.let {
            imagePreview.setImageBitmap(it)
        }
    }

    private fun storeCounterData() {
        val name = editTextName.text.toString()
        val desc = editTextDesc.text.toString()
        val locationId = if (binding.radioButtonSBH.isChecked) 1 else 2 // Change as needed
        val uid = auth.currentUser?.uid

        if (uid != null) {
            getNextDocumentName { nextDocumentName ->
                // Create a map with the data
                val counterData = mapOf(
                    "name" to name,
                    "desc" to desc,
                    "seller" to uid,
                    "location_id" to locationId,
                    "order_queue" to "0",
                    "url" to ""
                )

                // Store data in Firestore
                FirebaseFirestore.getInstance().collection("canteens")
                    .document(nextDocumentName)
                    .set(counterData)
                    .addOnSuccessListener {
                        Log.d("nice", "Data stored successfully")

                        if (imagePreview.drawable.constantState == resources.getDrawable(R.drawable.handput)?.constantState) {
                            // Default image is present, meaning no image selected by the user
                            val imageUrl = "https://firebasestorage.googleapis.com/v0/b/eaterlytestapi.appspot.com/o/handput.png?alt=media&token=b9a02019-0a36-41f9-bc9c-86f7963f422d" // Replace with the actual URL
                            updateUrlField(nextDocumentName, imageUrl)
                        } else {
                            // User selected an image, proceed with uploading
                            storeCounterImage(nextDocumentName)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("error", "Failed to store data: ${e.message}")
                        e.printStackTrace()
                        // You can show an error message to the user if needed
                    }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Function to get the next document name
    private fun getNextDocumentName(callback: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("canteens")
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // There are existing documents
                    val highestDocumentName = documents.documents[0].id
                    val nextDocumentName = (highestDocumentName.toInt() + 1).toString()
                    callback.invoke(nextDocumentName)
                } else {
                    // No existing documents, start with 1
                    callback.invoke("1")
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
                e.printStackTrace()
                // You can show an error message to the user if needed
            }
    }

    private fun storeCounterImage(documentName: String) {
        Log.d("progress", "Inside storeCounterImage")

        // Get the cropped bitmap from the imagePreview
        val croppedBitmap = (imagePreview.drawable as BitmapDrawable).bitmap

        // Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("canteen_img/${documentName}.png")

        // Convert the bitmap to bytes
        val baos = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos)
        val data = baos.toByteArray()

        // Upload the image
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                // Update the Firestore document with the download URL
                updateUrlField(documentName, uri.toString())
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

    private fun updateUrlField(documentName: String, url: String) {
        // Update the URL field in Firestore
        FirebaseFirestore.getInstance().collection("canteens")
            .document(documentName)
            .update("url", url)
            .addOnSuccessListener {
                Toast.makeText(this, "Counter registered.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SellerActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Handle the error
                e.printStackTrace()
                // You can show an error message to the user if needed
            }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to submit this data?")

        builder.setPositiveButton("Yes") { _, _ ->
            storeCounterData()
        }

        builder.setNegativeButton("No") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PICK_IMAGE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
}