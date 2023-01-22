package com.dausinvestama.eaterly.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.fragment.HomeFragment

class AdapterJenis(private var context: HomeFragment, private var imageList: ArrayList<String>, private var id_jenis: ArrayList<Int>, private var nama_jenis: ArrayList<String>) :
RecyclerView.Adapter<AdapterJenis.MyHolder>(){

    class MyHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val NamaJenis: TextView = itemView.findViewById(R.id.categoryname)
        val FotoJenis: ImageView = itemView.findViewById(R.id.categoryphoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.grid_item,
                parent,
                false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.NamaJenis.text = nama_jenis[position]
        Glide.with(context).load(imageList[position]).into(holder.FotoJenis)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}