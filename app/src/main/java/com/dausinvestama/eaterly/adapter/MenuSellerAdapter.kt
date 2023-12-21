package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.MenuData

class MenuSellerAdapter(private val menus: MutableList<MenuData>, private val context: Context) : RecyclerView.Adapter<MenuSellerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName = itemView.findViewById<TextView>(R.id.namamakanan)
        private val tvQuantity = itemView.findViewById<TextView>(R.id.quantity_1)
        private val tvHarga = itemView.findViewById<TextView>(R.id.harga)
        private val imgMenu = itemView.findViewById<ImageView>(R.id.gambarcartorder)

        fun bind(menuData: MenuData, context: Context) {
            tvName.text = menuData.name.toString()
            tvQuantity.visibility = View.GONE
            tvHarga.text = menuData.price.toString()
            Glide.with(context)
                .load(menuData.url)
                .into(imgMenu)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cartfororderhistory, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menus.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menus[position], context)
    }
}