package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.MainActivity
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.fragment.HomeFragment

class CategoryAdapter(private val context: Context, private var categoryList:ArrayList<CategoryList>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var OnItemClick: ((CategoryList) -> Unit)? = null



    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val CategoryName: TextView = itemView.findViewById(R.id.categoryname)
        val CategoryPhoto: ImageView = itemView.findViewById(R.id.categoryphoto)
        val CardViewClicker: CardView = itemView.findViewById(R.id.cardviewclicker)

    }

    fun onApplySearch(kategorisearch: ArrayList<CategoryList>){
        this.categoryList = kategorisearch
        notifyDataSetChanged()
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
        val cl = categoryList[position]
        holder.CardViewClicker.setOnClickListener {
            OnItemClick?.invoke(cl)
        }
        holder.CategoryName.text = cl.Categorylist
        Glide.with(context).load(cl.ImageList).into(holder.CategoryPhoto)

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}