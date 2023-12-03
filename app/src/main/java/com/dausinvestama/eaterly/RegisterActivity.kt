package com.dausinvestama.eaterly

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dausinvestama.eaterly.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {
            binding.tvLogIn.setOnClickListener {
                Intent(this@RegisterActivity, SignInActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun registerWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail: success")
                    val user = auth.currentUser

                    updateUI(user)
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail: failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateProfile(){
        auth.currentUser?.let { user ->
            val username = binding.edtUsername.text.toString()
            val setName = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(setName).await()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity, "Updated user name", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            updateProfile()
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }
}