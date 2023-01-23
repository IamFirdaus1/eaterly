package com.dausinvestama.eaterly.adapter

import android.content.Intent
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
import com.dausinvestama.eaterly.fragment.HomeFragment

class KantinAdapter(private var context: HomeFragment, private var ImageList: ArrayList<String>, private var NamaKantin: ArrayList<String>, private var idkantin: ArrayList<Int>) :
 RecyclerView.Adapter<KantinAdapter.MyHolder>(){



    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val CanteenName: TextView = itemView.findViewById(R.id.categoryname)
        val CanteenLogo: ImageView = itemView.findViewById(R.id.categoryphoto)
        val CardViews: CardView = itemView.findViewById(R.id.cardviewclicker)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.category_item,
                    parent,
                    false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.CanteenName.text = NamaKantin[position]
        Glide.with(context).load(ImageList[position]).into(holder.CanteenLogo)
    }

    override fun getItemCount(): Int {
        return ImageList.size
    }
}