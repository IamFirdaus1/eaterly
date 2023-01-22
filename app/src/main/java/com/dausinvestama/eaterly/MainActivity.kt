package com.dausinvestama.eaterly

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
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

        init()
//        setUpTransformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })
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

//    private fun setUpTransformer(){
//        val transformer = CompositePageTransformer()
//        transformer.addTransformer(MarginPageTransformer(40))
//        transformer.addTransformer { page, position ->
//            val r = 1 - abs(position)
//            position =
//
//        }
//
//        viewPager2.setPageTransformer(transformer)
//    }

    private fun init(){
        viewPager2 = findViewById(R.id.viewPager2)
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()
        titlelist = ArrayList()
        subtitlelist = ArrayList()
        explanationlist = ArrayList()


        db.collection("eaterlytesting").get().addOnSuccessListener {result ->
            for (document in result) {
                var x: String = document.get("explain") as String
                var y: String = document.get("title") as String
                var z: String = document.get("subtitle") as String
                Log.d(TAG, "init: " + x + y + z)
                explanationlist.add(x)
                imageList.add(R.drawable.handput)
                titlelist.add(y)
                subtitlelist.add(z)
                var tesvariable = 0
                var imagetes: String = imageList.get(tesvariable).toString()
                Log.d(imagetes, "inittes: " )
                tesvariable += 1
            }
            adapter = IntroAdapter(explanationlist, subtitlelist, titlelist ,imageList, viewPager2)
            viewPager2.adapter = adapter
            viewPager2.offscreenPageLimit = 3
            viewPager2.clipToPadding = false
            viewPager2.clipChildren = false
            viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            yuhu = true
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents.", exception)
            }

        if (yuhu == true){
            Log.d(TAG, "init: ")

        }






    }
}