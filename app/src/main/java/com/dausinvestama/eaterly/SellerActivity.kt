package com.dausinvestama.eaterly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.dausinvestama.eaterly.databinding.ActivitySellerMainBinding
import com.dausinvestama.eaterly.fragment.Cart
import com.dausinvestama.eaterly.fragment.HomeFragment
import com.dausinvestama.eaterly.fragment.MenuFragment
import com.dausinvestama.eaterly.fragment.Orderlist
import com.dausinvestama.eaterly.fragment.Profile
import com.dausinvestama.eaterly.fragment.SellerBlankFragment
import com.dausinvestama.eaterly.fragment.SellerHomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SellerActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySellerMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN

        checkIfSellerHasCounter()
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutSeller, fragment)
        fragmentTransaction.commit()
    }

    private fun checkIfSellerHasCounter() {
        val currentUserUid = auth.currentUser?.uid
        currentUserUid?.let { uid ->
            db.collection("canteens")
                .whereEqualTo("seller", uid)
                .get()
                .addOnSuccessListener { documents ->
                    val hasCounter = !documents.isEmpty
                    if (hasCounter) {
                        replaceFragment(SellerHomeFragment())
                    } else {
                        replaceFragment(SellerBlankFragment())
                    }
                    setupBottomNavigation(hasCounter)
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    e.printStackTrace()
                    // Replace with SellerBlankFragment in case of an error
                    setupBottomNavigation(false)
                    replaceFragment(SellerBlankFragment())
                }
        }
    }

    private fun setupBottomNavigation(hasCounter: Boolean) {
        binding.bottomnav.setOnItemSelectedListener {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN

            when (it.itemId) {
                R.id.homes -> replaceFragment(if (hasCounter) SellerHomeFragment() else SellerBlankFragment())
                R.id.orderlist -> replaceFragment(Orderlist())
                R.id.counter -> replaceFragment(MenuFragment())
                R.id.profile -> replaceFragment(Profile())
                else -> {
                }
            }
            true
        }
    }
}