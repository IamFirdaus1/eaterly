package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.AdapterJenis
import com.dausinvestama.eaterly.adapter.CategoryAdapter
import com.dausinvestama.eaterly.adapter.KantinAdapter
import com.dausinvestama.eaterly.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var listcategory: RecyclerView
    private lateinit var listkantin: RecyclerView
    private lateinit var listjenis: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var adapterkantin: KantinAdapter
    private lateinit var adapterjenis: AdapterJenis



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        listcategory = view.findViewById(R.id.listcategory)
        listkantin = view.findViewById(R.id.listkantin)
        listjenis = view.findViewById(R.id.listJenis)
        init()
        initkantin()
        initjenis()
        Log.d(TAG, "inikepanggil1")
        // Inflate the layout for this fragment
        return view
    }

    private fun initjenis() {
        Log.d(TAG, "inikepanggil3")
        var imageList: ArrayList<String> = ArrayList()
        var namaJenis: ArrayList<String> = ArrayList()
        var idJenis: ArrayList<Int> = ArrayList()
        Log.d(imageList.size.toString(), "dbkuss")

        db.collection("jenis").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_jenis") as String
                var y: String = document.get("gambar") as String
                var z: Long = document.get("id_jenis") as Long

                imageList.add(y)
                namaJenis.add(x)
                idJenis.add(z.toInt())
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapterjenis = AdapterJenis(this, imageList, idJenis, namaJenis)
            listjenis.adapter = adapterjenis
            var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
            listjenis.layoutManager = layoutmanager


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initkantin() {
        Log.d(TAG, "inikepanggil3")
        var imageList: ArrayList<String> = ArrayList()
        var namakantin: ArrayList<String> = ArrayList()
        var idkantin: ArrayList<Int> = ArrayList()
        Log.d(imageList.size.toString(), "dbkuss")

        db.collection("kantin").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_kantin") as String
                var y: String = document.get("logo_kantin") as String
                var z: Long = document.get("id_kantin") as Long

                imageList.add(y)
                namakantin.add(x)
                idkantin.add(z.toInt())
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapterkantin = KantinAdapter(this, imageList, namakantin, idkantin)
            listkantin.adapter = adapterkantin
            listkantin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "inikepanggil2")
        init()
    }

    private fun init(){

        Log.d(TAG, "inikepanggil3")
        var imageList: ArrayList<String> = ArrayList()
        var CategoryList: ArrayList<String> = ArrayList()
        Log.d(imageList.size.toString(), "dbkuss")

        db.collection("categorymakanan").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("CategoryName") as String
                var y: String = document.get("Link") as String

                CategoryList.add(x)
                imageList.add(y)
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapter = CategoryAdapter(this , CategoryList, imageList)
            listcategory.adapter = adapter
            listcategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }

    }
    
}