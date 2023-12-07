package com.dausinvestama.eaterly.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = firebaseAuth.currentUser

        binding.apply {
//            setEditTextFocus(edtEmail)
//            setEditTextFocus(edtUsername)

            edtEmail.setText(user?.email)
            edtUsername.setText(user?.displayName)

            if (user?.photoUrl != null) {
                Glide.with(this@EditProfileActivity)
                    .load(user.photoUrl)
                    .centerCrop()
                    .into(imgPp)
            } else {
                imgPp.setImageResource(R.drawable.user_icon)
            }

            if (edtEmail.text.toString().isEmpty() || edtUsername.text.toString().isEmpty()){
                btnSave.isEnabled = false
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setEditTextFocus(et: EditText) {
        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            ViewCompat.setBackgroundTintList(
                et,
                if (hasFocus) getColorStateList(R.color.green) else getColorStateList(R.color.black_trs)
            )
        }
    }
}