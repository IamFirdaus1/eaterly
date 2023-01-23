package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SearchEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.AdapterJenis
import com.dausinvestama.eaterly.adapter.CategoryAdapter
import com.dausinvestama.eaterly.adapter.KantinAdapter
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.DetailedList
import com.dausinvestama.eaterly.data.JenisList
import com.dausinvestama.eaterly.data.KantinList
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    lateinit var searchView: androidx.appcompat.widget.SearchView

    lateinit var adapterkategori: CategoryAdapter
    var listkategori: ArrayList<CategoryList> = ArrayList()

    lateinit var adapterkantin: KantinAdapter
    var listcanteen: ArrayList<KantinList> = ArrayList()

    lateinit var adapterjenis: AdapterJenis
    var jenislist: ArrayList<JenisList> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        searchView = view.findViewById(R.id.searchall)

        //initialisasi recyclerview
        init(view)
        initkantin(view)
        initjenis(view)

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchListKantin = ArrayList<KantinList>()
                val searchListCategory = ArrayList<CategoryList>()
                val searchListJenis = ArrayList<JenisList>()
                if (query != null){
                    for (i in listkategori){
                        if (i.Categorylist.lowercase(Locale.ROOT).contains(query)){
                            searchListCategory.add(i)
                        }

                    }
                    for (i in listcanteen) {
                        if (i.NamaKantin.lowercase(Locale.ROOT).contains(query)){
                            searchListKantin.add(i)
                        }
                    }
                    for (i in jenislist) {
                        if (i.nama_jenis.lowercase(Locale.ROOT).contains(query)){
                            searchListJenis.add(i)
                        }
                    }

                    adapterkategori.onApplySearch(searchListCategory)

                    adapterkantin.onApplySearch(searchListKantin)

                    adapterjenis.OnApplySearch(searchListJenis)

                }else{
                    Toast.makeText(context , "Null", Toast.LENGTH_SHORT).show()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchListKantin = ArrayList<KantinList>()
                val searchListCategory = ArrayList<CategoryList>()
                val searchListJenis = ArrayList<JenisList>()
                if (newText != null){
                    for (i in listkategori){
                        if (i.Categorylist.lowercase(Locale.ROOT).contains(newText)){
                            searchListCategory.add(i)
                            Toast.makeText(context , "Terdapat di jenis Kategori", Toast.LENGTH_SHORT).show()
                        }

                    }
                    for (i in listcanteen) {
                        if (i.NamaKantin.lowercase(Locale.ROOT).contains(newText)){
                            searchListKantin.add(i)
                            Toast.makeText(context , "Terdapat di nama Kantin", Toast.LENGTH_SHORT).show()
                        }
                    }
                    for (i in jenislist) {
                        if (i.nama_jenis.lowercase(Locale.ROOT).contains(newText)){
                            searchListJenis.add(i)
                            Toast.makeText(context , "Terdapat di jenis makanan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (searchListCategory.isNotEmpty()) {
                        adapterkategori.onApplySearch(searchListCategory)

                    }
                    if (searchListKantin.isNotEmpty()){
                        adapterkantin.onApplySearch(searchListKantin)


                    }
                    if (searchListJenis.isNotEmpty()){
                        adapterjenis.OnApplySearch(searchListJenis)


                    }
                }else{
                    Toast.makeText(context , "Null", Toast.LENGTH_SHORT).show()
                }

                return true
            }

        })

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "inikepanggil2")
    }

    private fun initjenis(view: View) {
        var listjenis:RecyclerView = view.findViewById(R.id.listJenis)



        db.collection("jenis").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_jenis") as String
                var y: String = document.get("gambar") as String
                var z: Long = document.get("id_jenis") as Long

                jenislist.add(JenisList(y, z.toInt(), x))

            }
            Log.d(TAG, "inikepanggil4")
            adapterjenis = AdapterJenis(this, jenislist)
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

        db.collection("kantin").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_kantin") as String
                var y: String = document.get("logo_kantin") as String
                var z: Long = document.get("id_kantin") as Long

                listcanteen.add(KantinList(y, x, z.toInt()))
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapterkantin = KantinAdapter(this, listcanteen)
            listkantin.adapter = adapterkantin
            listkantin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)

        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun init(view: View){
        var listcategory: RecyclerView = view.findViewById(R.id.listcategory)





        db.collection("categorymakanan").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("CategoryName") as String
                var y: String = document.get("Link") as String
                listkategori.add(CategoryList( x, y))
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapterkategori = context?.let { CategoryAdapter(it, listkategori) }!!
            listcategory.adapter = adapterkategori
            listcategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


            adapterkategori.OnItemClick = {
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