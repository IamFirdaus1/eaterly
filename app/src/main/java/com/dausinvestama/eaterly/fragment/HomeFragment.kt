package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
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
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.DetailedList
import com.dausinvestama.eaterly.data.KantinList
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()

    private lateinit var adapterjenis: AdapterJenis



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)

        //initialisasi recyclerview
        init(view)
        initkantin(view)
        initjenis(view)

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "inikepanggil2")
    }

    private fun initjenis(view: View) {
        var listjenis:RecyclerView = view.findViewById(R.id.listJenis)
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
            listjenis.setHasFixedSize(true)
            listjenis.adapter = adapterjenis
            var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
            listjenis.layoutManager = layoutmanager


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initkantin(view: View) {
        var listkantin:RecyclerView = view.findViewById(R.id.listkantin)

        var listcanteen: ArrayList<KantinList> = ArrayList()

        db.collection("kantin").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_kantin") as String
                var y: String = document.get("logo_kantin") as String
                var z: Long = document.get("id_kantin") as Long

                listcanteen.add(KantinList(y, x, z.toInt()))
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            var adapter = KantinAdapter(this, listcanteen)
            listkantin.adapter = adapter
            listkantin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun init(view: View){
        var listcategory: RecyclerView = view.findViewById(R.id.listcategory)

        var listkategori: ArrayList<CategoryList> = ArrayList()

        db.collection("categorymakanan").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("CategoryName") as String
                var y: String = document.get("Link") as String
                listkategori.add(CategoryList( x, y))
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            var adapter = context?.let { CategoryAdapter(it, listkategori) }
            listcategory.adapter = adapter
            listcategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)

            adapter!!.OnItemClick = {
                val intent = Intent(context, DetailedList::class.java)
                intent.putExtra("categorylist", it)
                startActivity(intent)
            }


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }

    }
    
}