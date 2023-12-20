package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.utils.SharedPreferences

class LokasiAdapter(var context: Context, var lokasi: ArrayList<String>, var lokasi_id: ArrayList<Int>)
    :RecyclerView.Adapter<LokasiAdapter.viewHolder>(){

    lateinit var pre: SharedPreferences

    interface OnItemClickCallback {
        fun onItemClick(location: String, locationId: Int)
    }

    private lateinit var onItemClicked: OnItemClickCallback

    fun setOnItemClickCallback(onItemClick: OnItemClickCallback){
        this.onItemClicked = onItemClick
    }


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnlokasi: Button = itemView.findViewById(R.id.butonlokasi)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.lokasiitem,
                    parent,
                    false)

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val lks = lokasi[position]
        holder.btnlokasi.text = lks
        holder.btnlokasi.setOnClickListener {
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