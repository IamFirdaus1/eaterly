package com.dausinvestama.eaterly.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
        }
    }


}