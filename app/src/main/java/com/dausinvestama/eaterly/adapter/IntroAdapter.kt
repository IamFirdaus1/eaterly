package com.dausinvestama.eaterly.adapter

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

class IntroAdapter(public var context: IntroActivity, private val explanationlist: ArrayList<String>, private val subtitlelist: ArrayList<String>, private val titleslist: ArrayList<String>, private val imageList: ArrayList<String>, private val viewPager2: ViewPager2)
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
        Log.d(titleslist[position], "dariku: ")
        holder.titleslider.text = titleslist[position]
        holder.subtitleslidier.text = subtitlelist[position]
        holder.explanationslider.text = explanationlist[position]
        Glide.with(context).load(imageList[position]).into(holder.imageView)
        if (position == imageList.size-1){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    private val runnable = Runnable{
        imageList.addAll(imageList)
        titleslist.addAll(titleslist)
        subtitlelist.addAll(subtitlelist)
        explanationlist.addAll(explanationlist)
        notifyDataSetChanged()
    }
}