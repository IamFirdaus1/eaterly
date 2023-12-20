package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment() : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

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
    ): View {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root

        searchView = binding.searchall
        kantinviewer = binding.kantin
        ubahkantin = binding.ubahtempat
        usernameview = binding.username
        btnscan = binding.btnscan
        tvmeja = binding.tvmeja

        pre = SharedPreferences(context)

        initAll(pre.location_id)

        usernameview.text = firebaseAuth.currentUser?.displayName

        Log.d(TAG, "onCreateView: pre ${pre.location}")
        if (pre.location != null) {
            kantinviewer.text = pre.location
        }

        tvmeja.text = pre.nomor_meja.toString()

        ubahkantin.setOnClickListener {
            val showPopUp = PopUpFragment(requireContext())
            showPopUp.setOnLocationChangedCallback(object : PopUpFragment.OnPlaceChangedCallback {
                override fun onPlaceChanged(location: String, loc_id: Int) {
                    kantinviewer.text = location
                    initAll(loc_id)
                    adapterjenis.notifyDataSetChanged()
                    adapterkategori.notifyDataSetChanged()
                    adapterkantin.notifyDataSetChanged()
                }

            })
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        btnscan.setOnClickListener {
            val intent = Intent(context, QrScannerActivity::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchListKantin = ArrayList<KantinList>()
                val searchListCategory = ArrayList<CategoryList>()
                val searchListJenis = ArrayList<JenisList>()
                if (query != null) {
                    for (i in listkategori) {
                        if (i.Categorylist.lowercase(Locale.ROOT).contains(query)) {
                            searchListCategory.add(i)
                        }

                    }
                    for (i in listcanteen) {
                        if (i.NamaKantin.lowercase(Locale.ROOT).contains(query)) {
                            searchListKantin.add(i)
                        }
                    }
                    for (i in jenislist) {
                        if (i.nama_jenis.lowercase(Locale.ROOT).contains(query)) {
                            searchListJenis.add(i)
                        }
                    }

                    adapterkategori.onApplySearch(searchListCategory)

                    adapterkantin.onApplySearch(searchListKantin)

                    adapterjenis.OnApplySearch(searchListJenis)

                } else {
                    Toast.makeText(context, "Null", Toast.LENGTH_SHORT).show()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchListKantin = ArrayList<KantinList>()
                val searchListCategory = ArrayList<CategoryList>()
                val searchListJenis = ArrayList<JenisList>()
                if (newText != null) {
                    for (i in listkategori) {
                        if (i.Categorylist.lowercase(Locale.ROOT).contains(newText)) {
                            searchListCategory.add(i)
                            Toast.makeText(
                                context,
                                "Terdapat di jenis Kategori",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    for (i in listcanteen) {
                        if (i.NamaKantin.lowercase(Locale.ROOT).contains(newText)) {
                            searchListKantin.add(i)
                            Toast.makeText(context, "Terdapat di nama Kantin", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    for (i in jenislist) {
                        if (i.nama_jenis.lowercase(Locale.ROOT).contains(newText)) {
                            searchListJenis.add(i)
                            Toast.makeText(context, "Terdapat di jenis makanan", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    if (searchListCategory.isNotEmpty()) {
                        adapterkategori.onApplySearch(searchListCategory)

                    }
                    if (searchListKantin.isNotEmpty()) {
                        adapterkantin.onApplySearch(searchListKantin)


                    }
                    if (searchListJenis.isNotEmpty()) {
                        adapterjenis.OnApplySearch(searchListJenis)


                    }
                } else {
                    Toast.makeText(context, "Null", Toast.LENGTH_SHORT).show()
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

    private fun initAll(locationId: Int) {
        initkategori1(locationId)
        initkantin1(locationId)
        initjenis1(locationId)
    }

    private fun initkategori1(locationId: Int) {
        db.collection("categories").whereArrayContains("location_id", locationId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val x = document.getString("name") as String
                    val y = document.getString("link") as String
                    val z = document.id.toInt()

                    listkategori.add(CategoryList(x, y, z))
                }

                adapterkategori = CategoryAdapter(requireContext(), listkategori)
                binding.apply {
                    listcategory.adapter = adapterkategori
                    listcategory.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }

                adapterkategori.OnItemClick = {
                    val intent = Intent(context, DetailedCategory::class.java)
                    intent.putExtra("categorylist", it)
                    intent.removeExtra("kantinList")
                    startActivity(intent)
                }

            }.addOnFailureListener {

            }
    }

    private fun initjenis1(locationId: Int) {

        db.collection("types").whereArrayContains("location_id", locationId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val x = document.getString("name") as String
                    val y = document.getString("url") as String
                    val z = document.id.toInt()

                    Log.d(TAG, "initjenis in fragmenthome: $x $y $z")

                    jenislist.add(JenisList(y, z, x))
                }
                Log.d(TAG, "initjenis in fragmenthome outerloop: ${result.size()}")
                adapterjenis = AdapterJenis(this, jenislist)
                binding.apply {
                    listJenis.setHasFixedSize(true)
                    listJenis.adapter = adapterjenis
                    val layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
                    listJenis.layoutManager = layoutmanager
                }

                adapterjenis.OnItemClick = {
                    val intent = Intent(context, DetailedCategory::class.java)
                    intent.putExtra("jenisList", it)
                    startActivity(intent)

                }


            }.addOnFailureListener {
                Log.d(TAG, "Error getting documents in homefragment.", it)
            }
    }

    private fun initkantin1(locationId: Int) {
        db.collection("canteens").whereEqualTo("location_id", locationId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val x = document.get("name") as String
                    val y = document.get("url") as String
                    val z = document.id.toInt()

                    listcanteen.add(KantinList(y, x, z, 1))
                }
                adapterkantin = KantinAdapter(this, listcanteen)
                binding.apply {
                    listkantin.adapter = adapterkantin
                    listkantin.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }

                adapterkantin.OnItemClick = {
                    val intent = Intent(context, DetailedCategory::class.java)
                    intent.putExtra("kantinList", it)
                    intent.removeExtra("categorylist")
                    startActivity(intent)
                }

            }.addOnFailureListener {

            }
    }

    override fun onResume() {
        super.onResume()

        kantinviewer.setText(pre.location)
    }
}