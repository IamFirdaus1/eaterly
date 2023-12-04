package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

    class myHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val gambarcartOrder: ImageView = itemView.findViewById(R.id.gambarcartorder)
        val nama_makanan: TextView = itemView.findViewById(R.id.namamakanan)
        val butontambah: Button = itemView.findViewById(R.id.butontambah)
        val butonkurang: Button = itemView.findViewById(R.id.butonkurang)
        val harga: TextView = itemView.findViewById(R.id.harga)
        val clearmenu: Button = itemView.findViewById(R.id.clearmenu)
        val jumlahmenu: TextView = itemView.findViewById(R.id.jumlah)
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

        var ct = cartOrderData!![position]

        holder.nama_makanan.text = ct.nama_makanan
        Glide.with(context!!).load(ct.gambar_makanan).into(holder.gambarcartOrder)
        holder.harga.text = (ct.harga * ct.jumlah).toString()
        holder.jumlahmenu.text  = ct.jumlah.toString()
        holder.clearmenu.setOnClickListener {
            localdb = AppDatabase.getInstance(context!!)
            val cartItemDao = localdb.cartDao()
            cartItemDao.deleteid(ct)
            cartOrderData!!.clear()
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())
            getData(ct.id_kantin)
            notifyItemChanged(position)
        }

        holder.butontambah.setOnClickListener {
            localdb = AppDatabase.getInstance(context!!)
            val cartItemDao = localdb.cartDao()
            cartItemDao.incrementJumlah(ct.id_makanan)
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())

        }

        holder.butonkurang.setOnClickListener {
            localdb = AppDatabase.getInstance(context!!)
            val cartItemDao = localdb.cartDao()
            cartItemDao.decrementJumlah(ct.id_makanan)
            updateData(localdb.cartDao().getBymakanan(ct.id_makanan).toMutableList())
        }

        /* cartItemViewModel = ViewModelProvider(this).get(cartItemViewModel::class.java)

        cartItemViewModel.allCartItems.observe(this, Observer { cartItems ->
            // Update the cached copy of the cart items in the adapter.
            cartOrderAdapter.setCartItems(cartItems)
        }) */


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