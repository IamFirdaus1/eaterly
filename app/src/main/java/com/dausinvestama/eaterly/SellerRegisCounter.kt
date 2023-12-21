    package com.dausinvestama.eaterly

    import android.Manifest
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.Matrix
    import android.graphics.drawable.BitmapDrawable
    import android.media.ExifInterface
    import android.net.Uri
    import android.os.Bundle
    import android.provider.MediaStore
    import android.util.Log
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageButton
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.dausinvestama.eaterly.databinding.ActivitySellerRegisCounterBinding
    import com.dausinvestama.eaterly.utils.DialogUtils.showConfirmationDialog
    import com.dausinvestama.eaterly.utils.ImagePickerUtils
    import com.dausinvestama.eaterly.utils.ImagePickerUtils.cropImageToSquare
    import com.dausinvestama.eaterly.utils.ImagePickerUtils.cropToSquare
    import com.dausinvestama.eaterly.utils.ImagePickerUtils.setPreviewImage
    import com.dausinvestama.eaterly.utils.ImageStoreUtils.storeImage
    import com.dausinvestama.eaterly.utils.ImageStoreUtils.updateUrlField
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.firestore.FieldPath
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.Query
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.FirebaseStorage
    import java.io.ByteArrayOutputStream
    import java.io.IOException


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
                showConfirmationDialog(this) { storeCounterData() }
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
                openImagePicker()
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
                    openImagePicker()
                } else {
                    val pickPhotoIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, PICK_IMAGE)
                }
            }
        }

        private fun openImagePicker() {
            ImagePickerUtils.showImagePickDialog(
                context = this,
                activity = this,
                imageCaptureCode = REQUEST_IMAGE_CAPTURE,
                pickImageCode = PICK_IMAGE
            )
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
                                setPreviewImage(imagePreview, croppedBitmap)
                            }
                        }
                    }

                    PICK_IMAGE -> {
                        val selectedImageUri: Uri? = data?.data
                        // Crop the image to 1:1
                        val croppedBitmap = cropImageToSquare(contentResolver, selectedImageUri)
                        // Set the cropped image to the preview
                        setPreviewImage(imagePreview, croppedBitmap)
                    }
                }
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
                                updateUrlField(nextDocumentName, imageUrl, this, this)
                            } else {
                                // User selected an image, proceed with uploading
                                storeImage(imagePreview, CANTEEN_PATH, nextDocumentName, this, this)
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


        companion object {
            private const val REQUEST_IMAGE_CAPTURE = 1
            private const val PICK_IMAGE = 2
            private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
            private const val CANTEEN_PATH = "canteen_img"
        }
    }