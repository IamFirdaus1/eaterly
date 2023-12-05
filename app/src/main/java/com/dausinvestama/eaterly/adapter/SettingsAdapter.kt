package com.dausinvestama.eaterly.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.Settings

class SettingsAdapter(private val listSettings: ArrayList<Settings>) :
    RecyclerView.Adapter<SettingsAdapter.ListViewHolder>() {

    interface OnItemClickCallback {
        fun onItemClicked(position: Int)
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img: ImageView = itemView.findViewById(R.id.img_icon)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        var cvSettingItem: CardView = itemView.findViewById(R.id.cv_setting_item)
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

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(position)
        }

        if (!desc.isNullOrBlank()){
            holder.tvDesc.text = desc
            holder.tvDesc.visibility = View.VISIBLE
        }
    }


}