package com.dausinvestama.eaterly.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.CartAdapter
import com.dausinvestama.eaterly.data.CartOrderData
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb

class Cart : Fragment() {

    lateinit var cartAdapter: CartAdapter
    var arraycart: ArrayList<CartDb> = ArrayList()
    var arraycartorder = mutableListOf<CartItemDb>()
    lateinit private var localdb: CartDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragmenthok
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        initcart(view)

        return view
    }

    private fun initcart(view: View) {
        var listcart:RecyclerView = view.findViewById(R.id.cartlist)

        getData()

        listcart.setHasFixedSize(true)

        listcart.adapter = cartAdapter
        listcart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    fun getData(){
        localdb = CartDatabase.getInstance(requireContext())
        arraycart.clear()
        arraycart.addAll(localdb.outerCartDao().getAll())


        cartAdapter = CartAdapter(context, arraycart)
        cartAdapter.notifyDataSetChanged()
    }


}