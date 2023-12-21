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
import com.dausinvestama.eaterly.databinding.FragmentCartBinding
import com.dausinvestama.eaterly.databinding.FragmentHomeBinding
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.external.UiKitApi

class Cart : Fragment() {

    lateinit var cartAdapter: CartAdapter
    var arraycart: ArrayList<CartDb> = ArrayList()
    var arraycartorder = mutableListOf<CartItemDb>()
    private lateinit var localdb: CartDatabase
    private lateinit var Cartlocaldb: AppDatabase

    private lateinit var binding: FragmentCartBinding

    var subtotals: Int = 0

    lateinit var pre: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragmenthok
        binding = FragmentCartBinding.inflate(layoutInflater)
        pre = SharedPreferences(context)
        binding.apply {
            binding.nokursi.text = pre.nomor_meja.toString()
            getData(subtotal, biayajasa, biayatotal)
        }


        initcart(binding.root)

        return binding.root
    }

    private fun initmidtrans() {
        UiKitApi.Builder()
            .withMerchantClientKey("Mid-client-JhBFk4qvSkRkQyNi") // client_key is mandatory
            .withContext(requireContext()) // context is mandatory
            .withMerchantUrl("https://merchant-url-sandbox.com/") // set transaction finish callback (sdk callback)
            .enableLog(true) // enable sdk log (optional)
            .withColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .build()
    }

    private fun initcart(view: View) {
        val listcart: RecyclerView = view.findViewById(R.id.cartlist)

        listcart.setHasFixedSize(true)

        listcart.adapter = cartAdapter
        listcart.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    fun getData(subtotal: TextView, jasa: TextView, total: TextView) {
        var localSubtotals = 0
        localdb = CartDatabase.getInstance(requireContext())
        arraycart.clear()
        arraycart.addAll(localdb.outerCartDao().getAll())

        Cartlocaldb = AppDatabase.getInstance(requireContext())
        arraycartorder.clear()
        arraycartorder.addAll(Cartlocaldb.cartDao().getAll())

        for (i in 0 until arraycartorder.size) {
            val penambahan: Int = arraycartorder[i].jumlah * arraycartorder[i].harga
            localSubtotals += penambahan
        }
        val subtotals = localSubtotals

        subtotal.text = subtotals.toString()
        val tax: Int = (subtotals * 0.05).toInt()
        jasa.text = tax.toString()
        total.text = (subtotals + tax).toString()

        cartAdapter = CartAdapter(context, arraycart)
        cartAdapter.setOnCartContentChangedCallback(object :
            CartAdapter.OnCartContentChangedCallback {
            override fun onCartContentChange() {
                getData(subtotal, jasa, total)
            }
        })
    }


}