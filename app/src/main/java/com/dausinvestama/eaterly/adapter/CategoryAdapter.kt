package com.dausinvestama.eaterly.adapter

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.MainActivity
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.fragment.HomeFragment

class CategoryAdapter( private val CategoryList:ArrayList<CategoryList>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val CategoryName: TextView = itemView.findViewById(R.id.categoryname)
        val CategoryPhoto: ImageView = itemView.findViewById(R.id.categoryphoto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.category_item,
                parent,
                false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cl = CategoryList[position]
        holder.CategoryName.text = cl.Categorylist
        cl.context?.let { Glide.with(it).load(cl.ImageList).into(holder.CategoryPhoto) }
    }

    override fun getItemCount(): Int {
        return CategoryList.size
    }
}