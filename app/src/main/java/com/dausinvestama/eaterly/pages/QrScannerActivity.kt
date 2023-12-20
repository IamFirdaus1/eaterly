package com.dausinvestama.eaterly.pages

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.dausinvestama.eaterly.MainActivity
import com.dausinvestama.eaterly.databinding.ActivityQrScannerBinding
import com.dausinvestama.eaterly.utils.SharedPreferences

class QrScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrScannerBinding
    lateinit var codeScan : CodeScanner
    lateinit var pre: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pre = SharedPreferences(this)

        codeScanner()
        setPermission()
    }

    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeReq()
        }
    }

    private fun makeReq() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            101
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Izin akses kamera dibutuhkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun codeScanner() {
        codeScan = CodeScanner(this, binding.qrscan)

        codeScan.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.CONTINUOUS
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            codeScan.decodeCallback = DecodeCallback {
                Log.d(TAG, "codeScanner:sebelum " + it.text)
                runOnUiThread {
                    Log.d(TAG, "codeScanner: " + it.text)
                    pre.nomor_meja = it.text.toInt()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            codeScan.errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.d(TAG, "codeScanner:error " + it.message)
                    Toast.makeText(applicationContext, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

            binding.qrscan.setOnClickListener {
                codeScan.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScan.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScan.releaseResources()
    }
}