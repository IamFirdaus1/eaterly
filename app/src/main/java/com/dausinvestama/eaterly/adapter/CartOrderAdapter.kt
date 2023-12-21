package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb
import com.google.firebase.firestore.FirebaseFirestore

class CartOrderAdapter(var context: Context?, var cartOrderData: MutableList<CartItemDb>?)
    :RecyclerView.Adapter<CartOrderAdapter.myHolder>(){

    private lateinit var localdb: AppDatabase
    var arraycartorder = mutableListOf<CartItemDb>()

    var arraycart = mutableListOf<CartDb>()
    private lateinit var Cartlocaldb: CartDatabase

    val db = FirebaseFirestore.getInstance()

    var subtotals: Int = 0;

    interface OnItemChangedCallback {
        fun onItemChanged()
    }

    private lateinit var onItemChanged: OnItemChangedCallback

    fun setOnItemChangedCallback(onItemChanged : OnItemChangedCallback){
        this.onItemChanged = onItemChanged
    }

    class myHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val gambarcartOrder: ImageView = itemView.findViewById(R.id.gambarcartorder)
        val nama_makanan: TextView = itemView.findViewById(R.id.namamakanan)
        val butontambah: Button = itemView.findViewById(R.id.butontambah)
        val butonkurang: Button = itemView.findViewById(R.id.butonkurang)
        val harga: TextView = itemView.findViewById(R.id.harga)
        val clearmenu: Button = itemView.findViewById(R.id.clearmenu)
        val jumlahmenu: TextView = itemView.findViewById(R.id.jumlah)
        val cvMin : CardView = itemView.findViewById(R.id.cv_min)
        val cvAdd : CardView = itemView.findViewById(R.id.cv_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.cartorderitem,
                parent,
                false)

        return myHolder(view)
    }

    override fun onBindViewHolder(holder: myHolder, position: Int) {

        val ct = cartOrderData!![position]

        holder.nama_makanan.text = ct.nama_makanan
        Glide.with(context!!).load(ct.gambar_makanan).into(holder.gambarcartOrder)
        holder.harga.text = (ct.harga * ct.jumlah).toString()
        holder.jumlahmenu.text  = ct.jumlah.toString()
        localdb = AppDatabase.getInstance(context!!)
        val cartItemDao = localdb.cartDao()
        holder.clearmenu.setOnClickListener {
            cartItemDao.deleteid(ct)
            cartOrderData!!.clear()
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())
            getData(ct.id_kantin)
            notifyItemChanged(position)
            onItemChanged.onItemChanged()
        }

        holder.butontambah.setOnClickListener {
            cartItemDao.incrementJumlah(ct.id_makanan)
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())
            onItemChanged.onItemChanged()
        }

        holder.butonkurang.setOnClickListener {
            cartItemDao.decrementJumlah(ct.id_makanan)
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())
            onItemChanged.onItemChanged()
        }

        if (ct.jumlah == 1){
            holder.butonkurang.isEnabled = false
            holder.butonkurang.isActivated = false
            holder.cvMin.alpha = 0.5f
        } else if (ct.jumlah == 2) {
            holder.butonkurang.isEnabled = true
            holder.butonkurang.isActivated = true
            holder.cvMin.alpha = 1f
        }


    }

    fun updateData(newData: MutableList<CartItemDb>?) {
        cartOrderData = newData
        notifyDataSetChanged()
    }


    fun getData(position: Int){
        Cartlocaldb = CartDatabase.getInstance(context!!)
        Cartlocaldb.outerCartDao().deleteid(position)
        arraycart.clear()
        arraycart.addAll(Cartlocaldb.outerCartDao().getAll())

        val cartadapter = CartAdapter(context, arraycart as ArrayList<CartDb>)
        notifyItemChanged(position)
    }


    override fun getItemCount(): Int {
        return cartOrderData!!.size
    }


}