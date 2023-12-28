package com.dausinvestama.eaterly.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dausinvestama.eaterly.SellerRegisCounter
import java.io.IOException

object ImagePickerUtils {

    fun showImagePickDialog(
        context: Context,
        activity: Activity,
        imageCaptureCode: Int,
        pickImageCode: Int
    ) {
        val items = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add Photo")
        builder.setItems(items) { _, which ->
            when (which) {
                0 -> {
                    // Take Photo option selected
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                        activity.startActivityForResult(takePictureIntent, imageCaptureCode)
                    }
                }

                1 -> {
                    // Choose from Gallery option selected
                    val pickPhotoIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activity.startActivityForResult(pickPhotoIntent, pickImageCode)
                }
            }
        }
        builder.show()
    }

    fun cropImageToSquare(contentResolver: ContentResolver, imageUri: Uri?): Bitmap? {
        imageUri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                inputStream?.use { stream ->
                    val exif = ExifInterface(stream)
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
                }
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

    fun cropToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val size = if (width > height) height else width
        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    fun setPreviewImage(imageContainer: ImageView, bitmap: Bitmap?) {
        bitmap?.let {
            imageContainer.setImageBitmap(it)
        }
    }

    fun checkCameraPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openImagePicker(activity)
        }
    }

    fun openImagePicker(activity: Activity) {
        showImagePickDialog(
            context = activity,
            activity = activity,
            imageCaptureCode = REQUEST_IMAGE_CAPTURE,
            pickImageCode = PICK_IMAGE
        )
    }

    private const val REQUEST_IMAGE_CAPTURE = 1
    private const val PICK_IMAGE = 2
    private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
}