package com.dausinvestama.eaterly.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.JenisList
import com.dausinvestama.eaterly.fragment.HomeFragment

class AdapterJenis(private var context: HomeFragment, var jenisList: ArrayList<JenisList>) :
RecyclerView.Adapter<AdapterJenis.MyHolder>(){

    var OnItemClick: ((JenisList) -> Unit)? = null

    class MyHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val NamaJenis: TextView = itemView.findViewById(R.id.categoryname)
        val FotoJenis: ImageView = itemView.findViewById(R.id.categoryphoto)
        val cardviewclicker: CardView = itemView.findViewById(R.id.cardviewclicker)
    }

    fun OnApplySearch(jenissearch: ArrayList<JenisList>){
        this.jenisList = jenissearch
        notifyDataSetChanged()
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
        var jl = jenisList[position]
        holder.NamaJenis.text = jl.nama_jenis
        Glide.with(context).load(jl.imageList).into(holder.FotoJenis)
        holder.cardviewclicker.setOnClickListener {
            OnItemClick?.invoke(jl)
        }
    }

    override fun getItemCount(): Int {
        return jenisList.size

    }
}