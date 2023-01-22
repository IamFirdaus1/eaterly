package com.dausinvestama.eaterly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.dausinvestama.eaterly.adapter.IntroAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("name")





        //findViewById<TextView>(R.id.email).text = email
        //findViewById<TextView>(R.id.username).text = username

//        findViewById<Button>(R.id.btnlogout).setOnClickListener {
//            signOut()
//        }

    }

    fun signOut(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googlesigninclient = GoogleSignIn.getClient(this,gso)
        auth.signOut()
        googlesigninclient.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }




}