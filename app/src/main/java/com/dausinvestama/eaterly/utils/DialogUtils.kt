package com.dausinvestama.eaterly.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import com.dausinvestama.eaterly.SellerRegisCounter

object DialogUtils {
    fun showConfirmationDialog(context: Context, actionPositive: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to submit this data?")

        builder.setPositiveButton("Yes") { _, _ ->
            actionPositive.invoke()
        }

        builder.setNegativeButton("No") { _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}