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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class PopUpFragment(context: Context) : DialogFragment() {

    lateinit var recyclerpopup: RecyclerView
    val db = FirebaseFirestore.getInstance()

    lateinit var arraylokasi: ArrayList<String>
    lateinit var arraylokasiid: ArrayList<Int>

    lateinit var lokasiAdapter: LokasiAdapter

    interface OnPlaceChangedCallback{
        fun onPlaceChanged(location: String, loc_id: Int)
    }

    private lateinit var onPlaceChanged: OnPlaceChangedCallback

    fun setOnLocationChangedCallback(onPlacedChanged: OnPlaceChangedCallback) {
        this.onPlaceChanged = onPlacedChanged
    }

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
                val arraylokasi = document.id
                arraylokasiid.add(arraylokasi.toInt())
                Log.d(TAG, "popupfragment for location ${document.get("name")} ")
            }
            if (isAdded) {
                lokasiAdapter = LokasiAdapter(requireContext(), arraylokasi, arraylokasiid)
                lokasiAdapter.setOnItemClickCallback(object: LokasiAdapter.OnItemClickCallback {
                    override fun onItemClick(location: String, locationId: Int) {
                        onPlaceChanged.onPlaceChanged(location, locationId)
                        dismiss()
                    }

                })
            }
            Log.d(TAG, "popupfragmet for location outside loop: ${arraylokasi[0]} ${arraylokasi[1]} ${arraylokasiid[0]} ${arraylokasiid[1]}" )
            recyclerpopup.adapter = lokasiAdapter
            recyclerpopup.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        }
    }

}