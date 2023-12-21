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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnlokasi: Button = itemView.findViewById(R.id.butonlokasi)

    interface OnItemClickCallback {
        fun onItemClick(location: String, locationId: Int)
    }

    private lateinit var onItemClicked: OnItemClickCallback

    fun setOnItemClickCallback(onItemClick: OnItemClickCallback){
        this.onItemClicked = onItemClick
    }

        init {
            Log.d(TAG, "Resource ID in ViewHolder: ${btnlokasi.id}")
            Log.d(TAG, "View Hierarchy: $itemView")
            Log.d(TAG, "Button ID: ${itemView.findViewById<Button>(R.id.butonlokasi)}")
            Log.d(TAG, "View Holder: $adapterPosition")
            Log.d(TAG, "No position: ${RecyclerView.NO_POSITION}")


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

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val lks = lokasi[position]
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
            pre.location = lokasi[position]
            pre.location_id = lokasi_id[position]
            pre.nomor_meja = 0
            val localsdb: AppDatabase = AppDatabase.getInstance(context)
            val localdb: CartDatabase = CartDatabase.getInstance(context)
            localdb.outerCartDao().delete()
            localsdb.cartDao().delete()
            onItemClicked.onItemClick(lokasi[position], lokasi_id[position])
        }
    }

    override fun getItemCount(): Int {
        return lokasi.size
    }
}