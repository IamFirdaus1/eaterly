package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null // Declaring googleMap at the class level

    lateinit var adapterkategori: CategoryAdapter
    var listkategori: ArrayList<CategoryList> = ArrayList()

    lateinit var adapterkantin: KantinAdapter
    var listcanteen: ArrayList<KantinList> = ArrayList()

    lateinit var adapterjenis: AdapterJenis
    var jenislist: ArrayList<JenisList> = ArrayList()

    lateinit var pre: SharedPreferences

    var selectedLocation: String? = null // Declare selectedLocation as a class-level variable


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
                    handleLocationSelection(location)
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

    private fun handleLocationSelection(location: String?): Boolean {
        if (location != null) {
            val latLng: LatLng? = when (location) {
                "NBH" -> LatLng(-6.29862809919001, 107.16615125585714)
                "SBH" -> LatLng(-6.282660058854142, 107.17077015160136)
                "President University" -> LatLng(-6.284986110758287, 107.17054802028917)
                "City Walk" -> LatLng(-6.28278605122272, 107.16960481486011)
                "Kowandi" -> LatLng(-6.282975839137369, 107.16785332179246)
                else -> null // Handle the case where the location name is not recognized
            }

            if (latLng != null) {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                return true // Successfully handled the selection without overriding
            }
        }
        return false // No location selected or handled
    }

    private val locationCoordinates = mapOf(
        "SBH" to LatLng(-6.282660058854142, 107.17077015160136),
        "NBH" to LatLng(-6.29862809919001, 107.16615125585714),
        "President University" to LatLng(-6.284986110758287, 107.17054802028917),
        "City Walk" to LatLng(-6.28278605122272, 107.16960481486011),
        "Kowandi" to LatLng(-6.282975839137369, 107.16785332179246)
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "inikepanggil2")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().apply {
            replace(binding.mapContainer.id, mapFragment)
            commit()
        }

        Log.d(tag, "SELECTED LOCATION IN HOME FRAGMENT ${selectedLocation}")
        Log.d(tag, "PRE LOCATION ${pre.location}")

        mapFragment.getMapAsync { googleMap ->
            this.googleMap = googleMap
            val uiSettings = googleMap.uiSettings
            uiSettings.isZoomControlsEnabled = true

            // Permanent markers for your locations
            locationCoordinates.forEach { (locationName, locationLatLng) ->
                googleMap.addMarker(MarkerOptions().position(locationLatLng).title(locationName))
            }

            val locationLatLng = convertLocationToLatLng(pre.location)
            if (locationLatLng != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                Log.d(TAG, "Map camera moved to selected location")
            } else {
                selectedLocation = "NBH"

                Log.d(TAG, "No selected location available")
                checkLocationPermission()
                handleLocationSelection(selectedLocation)
            }

        }
    }
    private fun convertLocationToLatLng(locationName: String?): LatLng? {
        return locationCoordinates[locationName]
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, proceed with the location-related task
                enableMyLocation()
            } else {
                // Permission is denied, show a message to the user
                Toast.makeText(context, "Location permission is required for maps", Toast.LENGTH_LONG).show()
            }
        }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showInContextUI()
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            val mapFragment = childFragmentManager.findFragmentById(binding.mapContainer.id) as SupportMapFragment?

            mapFragment?.getMapAsync { googleMap ->
                googleMap.isMyLocationEnabled = true


                if (selectedLocation != null) {
                    // Move the camera to the selected location if available
                    val locationLatLng = convertLocationToLatLng(selectedLocation)
                    locationLatLng?.let {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    }
                } else {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let { userLocation ->
                            // Move camera to the user's current location if no selected location
                            val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                        }
                    }
                }
            }
        } else {
            // Request permissions from the user
            checkLocationPermission()
        }
    }

    private fun showInContextUI() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Location Permission")
            .setMessage("This app needs location permission to show you on the map.")
            .setPositiveButton("Ok") { _, _ ->
                requestPermissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            .setNegativeButton("No Thanks") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
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
                val localListCateg = ArrayList<CategoryList>()
                for (document in result) {
                    val x = document.getString("name") as String
                    val y = document.getString("link") as String
                    val z = document.id.toInt()


                    localListCateg.add(CategoryList(x, y, z))
                    listkategori = localListCateg
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
                val localListJenis = ArrayList<JenisList>()
                for (document in result) {
                    val x = document.getString("name") as String
                    val y = document.getString("url") as String
                    val z = document.id.toInt()

                    Log.d(TAG, "initjenis in fragmenthome: $x $y $z")

                    localListJenis.add(JenisList(y, z, x))

                    jenislist = localListJenis
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
                val localListCanteen = ArrayList<KantinList>()
                for (document in result) {
                    val x = document.get("name") as String
                    val y = document.get("url") as String
                    val z = document.id.toInt()


                    localListCanteen.add(KantinList(y, x, z, 1))
                    listcanteen = localListCanteen
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