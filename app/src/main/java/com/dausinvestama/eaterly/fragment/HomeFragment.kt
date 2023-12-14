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
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        binding = FragmentHomeBinding.inflate(layoutInflater)
        val view = binding.root

        searchView = binding.searchall
        kantinviewer = binding.kantin
        ubahkantin = binding.ubahtempat
        usernameview = binding.username
        btnscan = binding.btnscan
        tvmeja = binding.tvmeja

        initkategori1()
        initkantin1()
        initjenis1()

        pre = SharedPreferences(context)

        usernameview.text = firebaseAuth.currentUser?.displayName

        Log.d(TAG, "onCreateView: pre ${pre.location}")
        if (pre.location != null) {
            kantinviewer.text = pre.location
        }

        tvmeja.text = pre.nomor_meja.toString()

        ubahkantin.setOnClickListener {
            val showPopUp = PopUpFragment(requireContext())
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


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null // Declaring googleMap at the class level

    fun handleLocationSelection(location: String?): Boolean {
        if (location != null) {
            val latLng: LatLng? = when (location) {
                "NBH" -> LatLng(-6.29862809919001, 107.16615125585714)
                "SBH" -> LatLng(-6.282660058854142, 107.17077015160136)
                // Handle other location names and their respective LatLng coordinates
                else -> null // Handle the case where the location name is not recognized
            }

            if (latLng != null) {
                // Update the map's camera to the selected location
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                return true // Successfully handled the selection without overriding
            }
        }
        return false // No location selected or handled
    }



    // Mapping between location names and LatLng coordinates
    val locationCoordinates = mapOf(
        "SBH" to LatLng(-6.282660058854142, 107.17077015160136),
        "NBH" to LatLng(-6.29862809919001, 107.16615125585714),
        "President University Canteen" to LatLng(-6.285365515156995, 107.17007529334688)
        // Add other location cases here
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

        mapFragment.getMapAsync { googleMap ->
            val uiSettings = googleMap.uiSettings
            uiSettings.isZoomControlsEnabled = true

            // Permanent markers for your locations
            locationCoordinates.forEach { (locationName, locationLatLng) ->
                googleMap.addMarker(MarkerOptions().position(locationLatLng).title(locationName))
            }

            // Inside onViewCreated
            if (selectedLocation != null) {
                Log.d(TAG, "Selected location is not null: $selectedLocation")
                val locationLatLng = convertLocationToLatLng(selectedLocation)
                if (locationLatLng != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
                    Log.d(TAG, "Map camera moved to selected location")
                } else {
                    Log.e(TAG, "Failed to convert selected location to LatLng")
                }
            } else {
                // Fetch the selected location here before calling handleLocationSelection
                // For example, assuming "NBH" is the default selected location
                selectedLocation = "NBH"

                Log.d(TAG, "No selected location available")
                checkLocationPermission()
                handleLocationSelection(selectedLocation)
            }

        }
    }

    fun convertLocationToLatLng(locationName: String?): LatLng? {
        return locationCoordinates[locationName]
    }



    // Function to handle location selection from PopupFragment
    /*
    fun handleLocationSelection(selectedLocation: LatLng) {
        this.selectedLocation = selectedLocation
        // Move the map's camera to the selected location
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f))
    }
    */

    /*
    fun getClosestLocationName(userLocation: Location): String {
        // Define your locations with corresponding names
        val locationsMap = mapOf(
            "SBH" to LatLng(-6.282660058854142, 107.17077015160136),
            "NBH" to LatLng(-6.29862809919001, 107.16615125585714),
            "President University Canteen" to LatLng(-6.285365515156995, 107.17007529334688)
        )

        // Define the maximum distance (in meters) considered to be in bounds
        val maxDistance = 1000 // Example value, defining a 1km scope

        // Find closest location to the user's current location
        var closestLocationName = "Out of bounds"
        var smallestDistance = Float.MAX_VALUE // Change to Float
        for ((name, location) in locationsMap) {
            val result = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude,
                userLocation.longitude,
                location.latitude,
                location.longitude,
                result
            )
            val distance = result[0]
            if (distance < smallestDistance) {
                smallestDistance = distance
                closestLocationName = name
            }
        }

        // Check if the smallest distance is within the defined geographic scope
        return if (smallestDistance > maxDistance) "too far away from Canteen" else closestLocationName
    }*/

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
                // You can use the API that requires the permission.
                enableMyLocation()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                showInContextUI()
            }
            else -> {
                // Directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
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







    /* the focusOnUserLocation function will not be needed if you only want to show a default location on the map and do not need to move the camera to the user's current location automatically.
    private fun focusOnUserLocation(googleMap: GoogleMap) {
        // Assuming you have already obtained the location elsewhere and have it stored:
        val userLatLong = LatLng(pre.lastKnownLocationLatitude, pre.lastKnownLocationLongitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 15f))
        // Add marker or additional functionality as needed
    }
    */


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





    private fun initkategori1() {
        val listCategory:RecyclerView = binding.listcategory

        pre = SharedPreferences(context)
        db.collection("categories").whereArrayContains("location_id", pre.location_id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    var x = document.getString("name") as String
                    var y = document.getString("link") as String
                    var z = document.id.toInt()

                    listkategori.add(CategoryList(x, y, z))
                }

                adapterkategori = CategoryAdapter(requireContext(), listkategori)
                listCategory.adapter = adapterkategori
                listCategory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                adapterkategori.OnItemClick = {
                    val intent = Intent(context, DetailedCategory::class.java)
                    intent.putExtra("categorylist", it)
                    intent.removeExtra("kantinList")
                    startActivity(intent)
                }

            }.addOnFailureListener {

            }
    }

    private fun initjenis1() {
        var listjenis: RecyclerView = binding.listJenis




        pre = SharedPreferences(context)

        db.collection("types").whereArrayContains("location_id", pre.location_id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    var x = document.getString("name") as String
                    var y = document.getString("url") as String
                    var z = document.id.toInt()

                    Log.d(TAG, "initjenis in fragmenthome: $x $y $z")

                    jenislist.add(JenisList(y, z, x))
                }
                Log.d(TAG, "initjenis in fragmenthome outerloop: ${result.size()}")
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


            }.addOnFailureListener {
                Log.d(TAG, "Error getting documents in homefragment.", it)
            }
    }

    private fun initkantin1(){
        var listkantin: RecyclerView = binding.listkantin

        pre = SharedPreferences(context)

        db.collection("canteens").whereEqualTo("location_id", pre.location_id)
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    val x = document.get("name") as String
                    val y = document.get("url") as String
                    val z = document.id.toInt()

                    listcanteen.add(KantinList(y, x, z, 1))
                }
                adapterkantin = KantinAdapter(this, listcanteen)
                listkantin.adapter = adapterkantin
                listkantin.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

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