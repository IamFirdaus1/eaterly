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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.MainActivity
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.AdapterJenis
import com.dausinvestama.eaterly.adapter.CategoryAdapter
import com.dausinvestama.eaterly.adapter.KantinAdapter
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.pages.DetailedCategory
import com.dausinvestama.eaterly.data.JenisList
import com.dausinvestama.eaterly.data.KantinList
import com.dausinvestama.eaterly.databinding.FragmentHomeBinding
import com.dausinvestama.eaterly.pages.QrScannerActivity
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    val db = FirebaseFirestore.getInstance()
    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var kantinviewer: TextView
    lateinit var ubahkantin: TextView
    lateinit var usernameview: TextView
    lateinit var btnscan: Button
    lateinit var tvmeja: TextView

    lateinit var adapterkategori: CategoryAdapter
    var listkategori: ArrayList<CategoryList> = ArrayList()

    lateinit var adapterkantin: KantinAdapter
    var listcanteen: ArrayList<KantinList> = ArrayList()

    lateinit var adapterjenis: AdapterJenis
    var jenislist: ArrayList<JenisList> = ArrayList()

    lateinit var pre: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root

        searchView = binding.searchall
        kantinviewer = binding.kantin
        ubahkantin = binding.ubahtempat
        usernameview = binding.username
        btnscan = binding.btnscan
        tvmeja = binding.tvmeja

        //initialisasi recyclerview
        //init(view)
        initkategori2(view)
        initkantin(view)
        initjenis(view)

        pre = SharedPreferences(context)

        if (pre.first_name == "Null" || pre.first_name == null){
            val ShowPopUp = NamePopUpFragment(context)
            ShowPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }else {
            usernameview.setText(pre.first_name)
        }

        Log.d(TAG, "onCreateView: pre ${pre.location}")
        if (pre.location != null){
            kantinviewer.setText(pre.location)
        }

        tvmeja.text = pre.nomor_meja.toString()

        ubahkantin.setOnClickListener{
            val showPopUp = PopUpFragment(requireContext())
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        btnscan.setOnClickListener {
            val intent = Intent(context, QrScannerActivity::class.java)
            startActivity(intent)
        }

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

        pre = SharedPreferences(context)

        db.collection("jenis").document(pre.location.toString()).collection("jenis").get().addOnSuccessListener {result ->
            for (document in result) {

                var x: String = document.get("nama_jenis") as String
                var y: String = document.get("link") as String
                var z: Long = document.get("id_jenis") as Long

                jenislist.add(JenisList(y, z.toInt(), x))

            }
            Log.d(TAG, "inikepanggil4")
            adapterjenis = AdapterJenis(this, jenislist)
            listjenis.setHasFixedSize(true)
            listjenis.adapter = adapterjenis
            var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
            listjenis.layoutManager = layoutmanager

            adapterjenis.OnItemClick = {
                val intent = Intent(context, DetailedCategory::class.java)
                intent.putExtra("jenisList", it)
                startActivity(intent)
            }


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initkantin(view: View) {
        var listkantin:RecyclerView = view.findViewById(R.id.listkantin)

        pre = SharedPreferences(context)
        db.collection("kantintesting").document(pre.location.toString()).collection("Kantin").get().addOnSuccessListener { result ->
            for (document in result) {

                var x: String = document.get("nama_kantin") as String
                var y: String = document.get("link") as String
                var z: Long = document.get("id_kantin") as Long
                //var u: Long = document.get("order_id") as Long

                listcanteen.add(KantinList(y, x, z.toInt(), 1))
                Log.d(y, "dbku")
            }
            Log.d(TAG, "inikepanggil4")
            adapterkantin = KantinAdapter(this, listcanteen)
            listkantin.adapter = adapterkantin
            listkantin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)

            adapterkantin.OnItemClick = {
                val intent = Intent(context, DetailedCategory::class.java)
                intent.putExtra("kantinList", it)
                intent.removeExtra("categorylist")
                startActivity(intent)
            }

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
                var id: Long = document.get("id_kategori") as Long
                listkategori.add(CategoryList( x, y, id.toInt()))
            }
            Log.d(TAG, "inikepanggil4")
            adapterkategori = context?.let { CategoryAdapter(it, listkategori) }!!
            listcategory.adapter = adapterkategori
            listcategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


            adapterkategori.OnItemClick = {
                val intent = Intent(context, DetailedCategory::class.java)
                intent.putExtra("categorylist", it)
                startActivity(intent)
            }


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }

    }

    private fun initkategori2(view: View){
        var listcategory: RecyclerView = view.findViewById(R.id.listcategory)


        pre = SharedPreferences(context)
        db.collection("kategoritesting").document(pre.location.toString()).collection("kategori").get().addOnSuccessListener { result ->
            for (document in result) {

                var x: String = document.get("nama_kategori") as String
                var y: String = document.get("link") as String
                var z: Long = document.get("id_kategori") as Long
                listkategori.add(CategoryList( x, y, z.toInt()))
            }
            Log.d(TAG, "inikepanggil4")
            adapterkategori = context?.let { CategoryAdapter(it, listkategori) }!!
            listcategory.adapter = adapterkategori
            listcategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)


            adapterkategori.OnItemClick = {
                val intent = Intent(context, DetailedCategory::class.java)
                intent.putExtra("categorylist", it)
                intent.removeExtra("kantinList")
                startActivity(intent)
            }


        }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents.", exception)
            }

    }

    override fun onResume() {
        super.onResume()

        kantinviewer.setText(pre.location)
    }
    
}