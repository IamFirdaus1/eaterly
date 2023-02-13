package com.dausinvestama.eaterly

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dausinvestama.eaterly.adapter.IntroAdapter
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.data.IntroList
import com.dausinvestama.eaterly.databinding.ActivityMainBinding
import com.dausinvestama.eaterly.databinding.IntroHolderBinding
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore

class IntroActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var adapter: IntroAdapter
    val db = FirebaseFirestore.getInstance()
    lateinit var pre: SharedPreferences

    private lateinit var binding: IntroHolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IntroHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager2 = binding.viewPager2

        pre = SharedPreferences(this)

        supportActionBar?.hide()



        if (pre.firstinstall) {
            initintent()
        }else{
            init()
        }


    }

    override fun onStart() {
        super.onStart()

        binding.btnstart.setOnClickListener {
            pre.firstinstall = true
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable, 2000)
    }

    private val runnable = Runnable{
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun initintent(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun init(){
        var listintro: ArrayList<IntroList> = ArrayList()
        handler = Handler(Looper.myLooper()!!)


        db.collection("eaterlytesting").get().addOnSuccessListener {result ->
            var ar: ArrayList<Int>
            for (document in result) {

                var x: String = document.get("explain") as String
                var y: String = document.get("title") as String
                var z: String = document.get("subtitle") as String
                var u: String = document.get("link") as String

                listintro.add(IntroList( x, z, y, u))
            }
            Log.d(result.toString(), "dbkusss")
            adapter = IntroAdapter(this , listintro , viewPager2)
            viewPager2.adapter = adapter
            viewPager2.offscreenPageLimit = 3
            viewPager2.clipToPadding = false
            viewPager2.clipChildren = false
            viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })
    }


}