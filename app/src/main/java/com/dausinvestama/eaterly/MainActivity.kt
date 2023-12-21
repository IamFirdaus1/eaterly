package com.dausinvestama.eaterly

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dausinvestama.eaterly.databinding.ActivityMainBinding
import com.dausinvestama.eaterly.fragment.Cart
import com.dausinvestama.eaterly.fragment.HomeFragment
import com.dausinvestama.eaterly.fragment.Orderlist
import com.dausinvestama.eaterly.fragment.Profile
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
        auth = FirebaseAuth.getInstance()

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

        // Call replaceFragment() with HomeFragment when the activity starts
        replaceFragment(HomeFragment())
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment, "HomeFragmentTag") // Add the fragment with tag
        fragmentTransaction.commit()
    }
}