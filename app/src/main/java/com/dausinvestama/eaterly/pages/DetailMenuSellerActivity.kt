package com.dausinvestama.eaterly.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.databinding.ActivityDetailMenuSellerBinding

class DetailMenuSellerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMenuSellerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMenuSellerBinding.inflate(layoutInflater)

        binding.apply {
            displayOrNull(tvName, intent.getStringExtra(NAME))
            displayOrNull(tvPrice, "Rp" + intent.getStringExtra(PRICE))
            displayOrNull(tvDesc, intent.getStringExtra(DESC))
            displayOrNull(tvTime, intent.getStringExtra(TIME))
            Glide.with(this@DetailMenuSellerActivity)
                .load(intent.getStringExtra(URL).toString())
                .into(ivFood)

            btnBack.setOnClickListener {
                finish()
            }
        }

        setContentView(binding.root)
    }

    companion object {
        const val NAME = "name"
        const val PRICE = "price"
        const val DESC = "desc"
        const val TIME = "time"
        const val URL = "url"
    }

    private fun displayOrNull(tv: TextView, text: String?) {
        if (text == null || text == "null") {
            tv.visibility = View.GONE
        } else {
            tv.text = text
            tv.visibility = View.VISIBLE
        }
    }
}