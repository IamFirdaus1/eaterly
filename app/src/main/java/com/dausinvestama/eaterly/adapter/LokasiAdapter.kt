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

    var OnItemClick: (() -> Unit)? = null

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
        var lks = lokasi[position]
        holder.btnlokasi.text = lks
        holder.btnlokasi.setOnClickListener {
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