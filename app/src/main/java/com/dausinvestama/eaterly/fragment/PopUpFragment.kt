package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.LokasiAdapter
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class PopUpFragment(context: Context) : DialogFragment() {

    // Define an interface to communicate the selected location to the parent fragment
    interface LocationSelectionListener {
        fun onLocationSelected(location: String)
    }

    private var locationSelectionListener: LocationSelectionListener? = null

    // Setter method to set the listener
    fun setLocationSelectionListener(listener: LocationSelectionListener) {
        locationSelectionListener = listener
    }


    lateinit var recyclerpopup: RecyclerView
    val db = FirebaseFirestore.getInstance()

    lateinit var arraylokasi: ArrayList<String>
    lateinit var arraylokasiid: ArrayList<Int>

    lateinit var lokasiAdapter: LokasiAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerpopup = view.findViewById(R.id.recyclerpopup)

        init()



    }



    private fun init() {
        arraylokasi = ArrayList()
        arraylokasiid = ArrayList()
        db.collection("locations").get().addOnSuccessListener { result ->
            for (document in result) {
                arraylokasi.add(document.getString("name").toString())
                var araylokasi = document.id
                arraylokasiid.add(araylokasi.toInt())
                Log.d(TAG, "popupfragment for location ${document.get("name")} ")
            }
            lokasiAdapter = context?.let { LokasiAdapter(it, arraylokasi, arraylokasiid) }!!

            // Set up item click listener for location selection
            lokasiAdapter.OnItemClick = { selectedLocation ->
                Log.d(TAG, "OnItemClick triggered with location: $selectedLocation")
                val latLng = when (selectedLocation) {
                    "SBH" -> LatLng(-6.282660058854142, 107.17077015160136)
                    "NBH" -> LatLng(-6.29862809919001, 107.16615125585714)
                    "President University Canteen" -> LatLng(-6.285365515156995, 107.17007529334688)
                    else -> null // Handle the case where selectedLocationName doesn't match any known locations
                }

                latLng?.let { location ->
                    val homeFragment = requireActivity().supportFragmentManager.findFragmentByTag("HomeFragmentTag") as? HomeFragment
                    homeFragment?.handleLocationSelection(selectedLocation)

                    // Reload the map by finding the SupportMapFragment and calling getMapAsync again
                    val mapFragment = homeFragment?.childFragmentManager?.findFragmentById(R.id.mapContainer) as? SupportMapFragment
                    mapFragment?.getMapAsync { googleMap ->
                        // Refresh the map or update it with new data
                        // This callback will initialize the map again with new data if needed
                    }
                }

                dismiss()
            }




            recyclerpopup.adapter = lokasiAdapter
            recyclerpopup.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

}