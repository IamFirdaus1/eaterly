package com.dausinvestama.eaterly.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.databinding.FragmentSellerHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class SellerHomeFragment : Fragment() {
    private lateinit var binding: FragmentSellerHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    val db = FirebaseFirestore.getInstance()
    lateinit var usernameview: TextView
    lateinit var counterNameView: TextView
    lateinit var counterDescView: TextView
    lateinit var counterImageView: ImageView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentSellerHomeBinding.inflate(layoutInflater)
        val view = binding.root
        Picasso.get().setLoggingEnabled(true)

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        usernameview = binding.username
        counterNameView = binding.counterName
        counterDescView = binding.counterDesc
        counterImageView = binding.counterImage

        usernameview.text = firebaseAuth.currentUser?.displayName
        loadSellerData()

        return view
    }

    private fun loadSellerData() {
        val currentUserUid = firebaseAuth.currentUser?.uid

        currentUserUid?.let { uid ->
            db.collection("canteens")
                .whereEqualTo("seller", uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Seller has a counter, update the UI with counter data
                        val counterData = documents.documents[0]

                        val counterName = counterData.getString("name")
                        val counterDesc = counterData.getString("desc")
                        val counterImageUrl = counterData.getString("url")

                        saveDataToSharedPreferences(counterName, counterDesc)

                        // Set data to UI elements
                        counterNameView.text = counterName
                        counterDescView.text = counterDesc

                        // Load counter image using Picasso or your preferred image loading library
                        Picasso.get()
                            .load(counterImageUrl)
                            .into(counterImageView)

                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    e.printStackTrace()
                }
        }
    }

    private fun saveDataToSharedPreferences(counterName: String?, counterDesc: String?) {
        // Save data to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("counterName", counterName)
        editor.putString("counterDesc", counterDesc)
        editor.apply()
    }
}