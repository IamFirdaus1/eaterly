package com.dausinvestama.eaterly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dausinvestama.eaterly.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            binding.tvLogIn.setOnClickListener {
                Intent(this@RegisterActivity, SignInActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }
}