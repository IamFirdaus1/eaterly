package com.dausinvestama.eaterly.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
//            setEditTextFocus(edtEmail)
//            setEditTextFocus(edtUsername)

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setEditTextFocus(et: EditText){
        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            ViewCompat.setBackgroundTintList(
                et,
                if (hasFocus) getColorStateList(R.color.green) else getColorStateList(R.color.black_trs)
            )
        }
    }
}