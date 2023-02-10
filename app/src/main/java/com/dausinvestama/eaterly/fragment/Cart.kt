package com.dausinvestama.eaterly.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.CartAdapter
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb

class Cart : Fragment() {

    lateinit var cartAdapter: CartAdapter
    var arraycart: ArrayList<CartDb> = ArrayList()
    var arraycartorder = mutableListOf<CartItemDb>()
    lateinit private var localdb: CartDatabase
    lateinit private var Cartlocaldb: AppDatabase

    var subtotals: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragmenthok
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val subtotal: TextView = view.findViewById(R.id.subtotal)
        val jasa: TextView = view.findViewById(R.id.biayajasa)
        val total: TextView = view.findViewById(R.id.biayatotal)
        getData()
        subtotal.text = subtotals.toString()
        val tax: Int = (subtotals*0.05).toInt()
        jasa.text = tax.toString()
        total.text = (subtotals + tax).toString()

        initcart(view)

        return view
    }

    private fun initcart(view: View) {
        var listcart:RecyclerView = view.findViewById(R.id.cartlist)

        listcart.setHasFixedSize(true)

        listcart.adapter = cartAdapter
        listcart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    fun getData(){
        localdb = CartDatabase.getInstance(requireContext())
        arraycart.clear()
        arraycart.addAll(localdb.outerCartDao().getAll())

        Cartlocaldb = AppDatabase.getInstance(requireContext())
        arraycartorder.clear()
        arraycartorder.addAll(Cartlocaldb.cartDao().getAll())

        for (i in 0 until arraycartorder.size) {
            var penambahan: Int = arraycartorder[i].jumlah * arraycartorder[i].harga
            subtotals = subtotals + penambahan
        }

        cartAdapter = CartAdapter(context, arraycart)
        cartAdapter.notifyDataSetChanged()
    }


}