package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.Menu

class MenuAdapter(private val menus: List<Menu>, private val context: Context) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cartfororderhistory, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menus[position], context)
    }

    override fun getItemCount(): Int = menus.size

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val menuNameTextView: TextView = itemView.findViewById(R.id.namamakanan)
        private val menuQuantityTxtView: TextView = itemView.findViewById(R.id.quantity_1)
        private val totalTxtView: TextView = itemView.findViewById(R.id.harga)
        private val urlImgVw: ImageView = itemView.findViewById(R.id.gambarcartorder)

        fun bind(menu: Menu, context: Context) {
            menuNameTextView.text = menu.menuName.toString()
            menuQuantityTxtView.text = menu.menuQuantity.toString()
            totalTxtView.text = menu.menuTotalprice.toString()
        }
    }
}