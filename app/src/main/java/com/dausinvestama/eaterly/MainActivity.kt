package com.dausinvestama.eaterly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.viewpager2.widget.ViewPager2
import com.dausinvestama.eaterly.adapter.IntroAdapter
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<String>
    private lateinit var titlelist: ArrayList<String>
    private lateinit var subtitlelist: ArrayList<String>
    private lateinit var explanationlist: ArrayList<String>
    private lateinit var adapter: IntroAdapter
    val db = FirebaseFirestore.getInstance()
    var yuhu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()



    }




}