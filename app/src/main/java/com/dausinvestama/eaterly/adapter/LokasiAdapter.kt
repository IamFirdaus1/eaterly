package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.utils.SharedPreferences
import android.content.ContentValues.TAG

class LokasiAdapter(
    var context: Context,
    var lokasi: ArrayList<String>,
    var lokasi_id: ArrayList<Int>

) : RecyclerView.Adapter<LokasiAdapter.ViewHolder>() {

    lateinit var pre: SharedPreferences

    var OnItemClick: ((String) -> Unit)? = null // Callback function to handle location selection
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnlokasi: Button = itemView.findViewById(R.id.butonlokasi)
        val btnJaya: Button = itemView.findViewById(R.id.butonjaya)

        init {
            Log.d(TAG, "Resource ID in ViewHolder: ${btnlokasi.id}")
            Log.d(TAG, "KPUANG: ${btnJaya}")
            Log.d(TAG, "View Hierarchy: $itemView")
            Log.d(TAG, "Button ID: ${itemView.findViewById<Button>(R.id.butonlokasi)}")
            Log.d(TAG, "View Holder: $adapterPosition")
            Log.d(TAG, "No position: ${RecyclerView.NO_POSITION}")

            // SET CLICK LISTENER FOR KPUANG
            btnJaya.setOnClickListener {
                Log.d(TAG, "JAYA JAYA JAYA")
            }


            val position = adapterPosition
            // Set a click listener for the button
            btnlokasi.setOnClickListener {
                Log.d(TAG, "HELLO DAUS")
                // val position = adapterPosition
                Log.d(TAG, "Button clicked at position: $position")
                if (position != RecyclerView.NO_POSITION) {
                    val selectedLocation = lokasi.getOrNull(position)
                    selectedLocation?.let {
                        Log.d(TAG, "Location clicked: $it")
                        OnItemClick?.invoke(it) // Trigger location selection callback
                    }
                } else {
                    Log.d(TAG, "Invalid position: $position")
                }
            }
        }
    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lokasiitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var lks = lokasi[position]
        holder.btnlokasi.text = lks

        Log.d(TAG, "WOI KAMU")


        Log.d(TAG, "Button clicked at position: $position")

        holder.btnlokasi.setOnClickListener {
            Log.d(TAG, "HELLO DAUS")
            val selectedLocation = lokasi.getOrNull(position)
            Log.d(TAG, "THIS IS SELECTED LOCATION: $selectedLocation")
            selectedLocation?.let {
                Log.d(TAG, "Location clicked: $it")
                OnItemClick?.invoke(it)
            }

            pre = SharedPreferences(context)
            var getlks = lokasi[position]
            var getid = lokasi_id[position]
            pre.location = getlks
            pre.location_id = getid
            pre.nomor_meja = 0
            var localsdb: AppDatabase = AppDatabase.getInstance(context)
            var localdb: CartDatabase = CartDatabase.getInstance(context)
            localdb.outerCartDao().delete()
            localsdb.cartDao().delete()
        }
    }

    override fun getItemCount(): Int {
        return lokasi.size
    }
}