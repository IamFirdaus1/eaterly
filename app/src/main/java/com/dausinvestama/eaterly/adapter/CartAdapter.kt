package com.dausinvestama.eaterly.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.CartData
import com.dausinvestama.eaterly.data.CartOrderData
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb


class CartAdapter(private var contex: Context?, var cartData: ArrayList<CartDb>)
    :RecyclerView.Adapter<CartAdapter.myHolder>(){

    var arraycartorder = mutableListOf<CartItemDb>()
    private lateinit var localdb: AppDatabase

    var arraycart = mutableListOf<CartDb>()
    private lateinit var Cartlocaldb: CartDatabase

    interface OnCartContentChangedCallback{
        fun onCartContentChange()
    }

    private lateinit var onContentChange: OnCartContentChangedCallback

    fun setOnCartContentChangedCallback(onContentChange: OnCartContentChangedCallback){
        this.onContentChange = onContentChange
    }

    class myHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nama_kantin: TextView = itemView.findViewById(R.id.namakantincart)
        val recylercart: RecyclerView = itemView.findViewById(R.id.recylercart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.cart_item,
                parent,
                false)

        return myHolder(view)
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val ct = cartData[position]
        holder.nama_kantin.text = ct.nama_kantin

        Log.d(TAG, "onBindViewHolder: elsejalan" + ct.id_kantins)

        getData(ct.id_kantins!!)


        val cartOrder = CartOrderAdapter(contex, arraycartorder)

        cartOrder.setOnItemChangedCallback(object: CartOrderAdapter.OnItemChangedCallback {
            override fun onItemChanged() {
                onContentChange.onCartContentChange()
            }
        })

        holder.recylercart.setHasFixedSize(true)
        holder.recylercart.adapter = cartOrder
        holder.recylercart.layoutManager = LinearLayoutManager(contex, LinearLayoutManager.VERTICAL, false)


    }

    fun getData(id_kantins: Int){
        localdb = AppDatabase.getInstance(contex!!)
        arraycartorder.clear()
        arraycartorder.addAll(localdb.cartDao().getById(id_kantins))
    }



    override fun getItemCount(): Int {
        return cartData.size
    }
}