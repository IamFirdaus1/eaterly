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
import com.dausinvestama.eaterly.fragment.HomeFragment

class CategoryAdapter( var context: HomeFragment, private var Categorylist: ArrayList<String>, private var ImageList: ArrayList<String>) :
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
        Log.d(Categorylist[0], "darimu")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(Categorylist[position], "darimu")
        holder.CategoryName.text = Categorylist[position]
        Glide.with(context).load(ImageList[position]).into(holder.CategoryPhoto)
    }

    override fun getItemCount(): Int {
        Log.d(Categorylist.size.toString(), "darimu")
        return Categorylist.size
    }
}