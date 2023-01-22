package com.dausinvestama.eaterly

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.adapter.IntroAdapter
import com.dausinvestama.eaterly.databinding.ActivityMainBinding
import com.dausinvestama.eaterly.fragment.Cart
import com.dausinvestama.eaterly.fragment.HomeFragment
import com.dausinvestama.eaterly.fragment.Orderlist
import com.dausinvestama.eaterly.fragment.Profile
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN


        binding.bottomnav.setOnItemSelectedListener {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN

            when(it.itemId){
                R.id.homes -> replaceFragment(HomeFragment())
                R.id.orderlist -> replaceFragment(Orderlist())
                R.id.cart -> replaceFragment(Cart())
                R.id.profile -> replaceFragment(Profile())

                else -> {

                }
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
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