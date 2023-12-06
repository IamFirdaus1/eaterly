package com.dausinvestama.eaterly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.dausinvestama.eaterly.databinding.ActivitySellerMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SellerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_main)

        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.testLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@SellerActivity, SignInActivity::class.java))
            finish()
        }
    }
}