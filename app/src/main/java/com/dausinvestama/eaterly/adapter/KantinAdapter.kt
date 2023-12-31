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
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.data.KantinList
import com.dausinvestama.eaterly.fragment.HomeFragment

class KantinAdapter(private var context: HomeFragment, var kantinList: ArrayList<KantinList>) :
 RecyclerView.Adapter<KantinAdapter.MyHolder>(){

    var OnItemClick: ((KantinList) -> Unit)? = null


    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val CanteenName: TextView = itemView.findViewById(R.id.categoryname)
        val CanteenLogo: ImageView = itemView.findViewById(R.id.categoryphoto)
        val CardViews: CardView = itemView.findViewById(R.id.cardviewclicker)

    }

    fun onApplySearch(kantisearch: ArrayList<KantinList>){
        this.kantinList = kantisearch
        notifyDataSetChanged()
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
        var kl = kantinList[position]
        holder.CanteenName.text = kl.NamaKantin
        Glide.with(context).load(kl.ImageList).into(holder.CanteenLogo)
        holder.CardViews.setOnClickListener {
            OnItemClick?.invoke(kl)
        }
    }

    override fun getItemCount(): Int {
        return kantinList.size
    }
}