package com.dausinvestama.eaterly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.Settings

class SettingsAdapter(private val listSettings: ArrayList<Settings>) :
    RecyclerView.Adapter<SettingsAdapter.ListViewHolder>() {
    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img: ImageView = itemView.findViewById(R.id.img_icon)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.setting_item, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listSettings.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, img, desc) = listSettings[position]
        holder.img.setImageResource(img)
        holder.tvTitle.text = name
        if (!desc.isNullOrBlank()){
            holder.tvDesc.text = desc
            holder.tvDesc.visibility = View.VISIBLE
        }
    }


}