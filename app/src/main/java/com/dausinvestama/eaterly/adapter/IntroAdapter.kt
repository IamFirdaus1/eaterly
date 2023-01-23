package com.dausinvestama.eaterly.adapter

import android.content.Intent
import android.icu.number.IntegerWidth
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.IntroActivity
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.IntroList

class IntroAdapter(var context: IntroActivity, private var introList:ArrayList<IntroList>, var viewPager2: ViewPager2)
    :RecyclerView.Adapter<IntroAdapter.MyHolder>(){

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.viewpageslider)
        val titleslider: TextView = itemView.findViewById(R.id.titleslider)
        val subtitleslidier: TextView = itemView.findViewById(R.id.subtitleslider)
        val explanationslider: TextView = itemView.findViewById(R.id.explanationslider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.intro_screen,
                    parent,
                    false)

        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val ils = introList[position]
        holder.titleslider.text = ils.titleslist
        holder.subtitleslidier.text = ils.subtitlelist
        holder.explanationslider.text = ils.explanationlist
        Glide.with(context).load(ils.imageList).into(holder.imageView)
        if (position == introList.size-1){
            viewPager2.post(runnable)
        }

    }

    override fun getItemCount(): Int {
        return introList.size
    }

    private val runnable = Runnable{
        introList.addAll(introList)
    }
}