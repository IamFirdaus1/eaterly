package com.dausinvestama.eaterly.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.CategoryDetailData
import com.dausinvestama.eaterly.pages.DetailedCategory
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import java.lang.Override as Override1

class DetailMakananAdapter(private val context: Context, private var categoryDetailData: ArrayList<CategoryDetailData>)
    : RecyclerView.Adapter<DetailMakananAdapter.ViewHolder>(){

    var onItemClick: ((CategoryDetailData) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama_makanan: TextView = itemView.findViewById(R.id.nama_makanan)
        val deskripsi_makanan: TextView = itemView.findViewById(R.id.deskripsi_makanan)
        val nama_kantin: TextView = itemView.findViewById(R.id.namakantin)
        val antrian: TextView = itemView.findViewById(R.id.antrian)
        val linearbackground: LinearLayout = itemView.findViewById(R.id.linearbackground)
        val cardviewClicker: CardView = itemView.findViewById(R.id.cardviewclicker)
        val hargatampilan : TextView = itemView.findViewById(R.id.harga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.makanandetail,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dma = categoryDetailData[position]
        Log.d(TAG, "ombindadapter: " + dma.namamakanan)
        Glide.with(context).load(dma.gambar_makanan).into(object: CustomTarget<Drawable>(){
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                holder.linearbackground.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })
        holder.cardviewClicker.setOnClickListener {
            onItemClick!!.invoke(dma)
        }


        holder.nama_makanan.text = dma.namamakanan
        holder.deskripsi_makanan.text = dma.deskripsimakanan
        holder.nama_kantin.text = dma.namakantin
        holder.antrian.text = dma.jumlahantrian.toString()
        holder.hargatampilan.text = dma.hargamakanan.toString()

    }

    override fun getItemCount(): Int {
        return categoryDetailData.size
    }
}