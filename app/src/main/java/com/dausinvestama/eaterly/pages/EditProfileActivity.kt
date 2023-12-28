package com.dausinvestama.eaterly.pages

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.SellerRegisCounter
import com.dausinvestama.eaterly.databinding.ActivityEditProfileBinding
import com.dausinvestama.eaterly.utils.DialogUtils.showConfirmationDialog
import com.dausinvestama.eaterly.utils.ImagePickerUtils
import com.dausinvestama.eaterly.utils.ImagePickerUtils.checkCameraPermission
import com.dausinvestama.eaterly.utils.ImagePickerUtils.cropImageToSquare
import com.dausinvestama.eaterly.utils.ImagePickerUtils.cropToSquare
import com.dausinvestama.eaterly.utils.ImagePickerUtils.setPreviewImage
import com.dausinvestama.eaterly.utils.ImageStoreUtils.bitmapToUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initProfile()

        binding.apply {
            if (edtEmail.text.toString().isEmpty() || edtUsername.text.toString().isEmpty()) {
                btnSave.isEnabled = false
            }

            btnBack.setOnClickListener {
                finish()
            }

            imgPp.setOnClickListener {
                checkCameraPermission(this@EditProfileActivity)
            }

            btnSave.setOnClickListener {
                showConfirmationDialog(this@EditProfileActivity) {
                    storeNewProfileData(
                        newPp = uri,
                        newName = edtUsername.text.toString(),
                        newEmail = edtEmail.text.toString()
                    )
                }
            }
        }
    }

    private fun initProfile() {
        val user = firebaseAuth.currentUser

        binding.apply {
            edtEmail.setText(user?.email)
            edtUsername.setText(user?.displayName)

            if (user?.photoUrl != null) {
                Glide.with(this@EditProfileActivity)
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(imgPp)

                uri = user.photoUrl
            } else {
                imgPp.setImageResource(R.drawable.user_icon)
            }
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
                ImagePickerUtils.openImagePicker(this)
            } else {
                val pickPhotoIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent, PICK_IMAGE)
            }
        }
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
                            Log.d(TAG, "Photo taken! ")
                            setPreviewImage(binding.imgPp, croppedBitmap)

                            uri = bitmapToUri(this, croppedBitmap)
                        }
                    }
                }

                PICK_IMAGE -> {
                    val selectedImageUri: Uri? = data?.data
                    // Crop the image to 1:1
                    val croppedBitmap =
                        cropImageToSquare(contentResolver, selectedImageUri)
                    // Set the cropped image to the preview

                    if (croppedBitmap != null){
                        setPreviewImage(binding.imgPp, croppedBitmap)
                        uri = bitmapToUri(this, croppedBitmap)
                    }
                }
            }
        }
    }

    private fun storeNewProfileData(newPp: Uri?, newName: String, newEmail: String) {
        firebaseAuth.currentUser?.let {
            val profileUpdates: UserProfileChangeRequest = if (newPp == null) {
                UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
            } else {
                UserProfileChangeRequest.Builder()
                    .setPhotoUri(newPp)
                    .setDisplayName(newName)
                    .build()
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    it.updateProfile(profileUpdates).await()
                    it.updateEmail(newEmail)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Successfully Updated Profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditProfileActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PICK_IMAGE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }
}