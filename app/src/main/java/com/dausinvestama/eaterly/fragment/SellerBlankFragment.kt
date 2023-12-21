package com.dausinvestama.eaterly.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.dausinvestama.eaterly.SellerRegisCounter
import com.dausinvestama.eaterly.databinding.FragmentSellerBlankBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SellerBlankFragment : Fragment() {
    private lateinit var binding: FragmentSellerBlankBinding
    private lateinit var firebaseAuth: FirebaseAuth

    val db = FirebaseFirestore.getInstance()
    lateinit var btnGo : Button
    lateinit var usernameview: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentSellerBlankBinding.inflate(layoutInflater)
        val view = binding.root

        btnGo = binding.buttonGo
        usernameview = binding.username

        usernameview.text = firebaseAuth.currentUser?.displayName

        btnGo.setOnClickListener {
            val intent = Intent(context, SellerRegisCounter::class.java)
            startActivity(intent)
        }

        return view
    }

}