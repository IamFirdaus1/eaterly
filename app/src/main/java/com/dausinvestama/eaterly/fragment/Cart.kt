package com.dausinvestama.eaterly.fragment

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Cart : Fragment() {

    lateinit var cartAdapter: CartAdapter
    var arraycart: ArrayList<CartDb> = ArrayList()
    var arraycartorder = mutableListOf<CartItemDb>()
    private lateinit var localdb: CartDatabase
    private lateinit var Cartlocaldb: AppDatabase
    private var orderConfirmations: MutableMap<String, Boolean> = mutableMapOf()


    private lateinit var binding: FragmentCartBinding
    private lateinit var loadingDialog: Dialog

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
            binding.bayarcash.setOnClickListener {
                uploadOrder()
                arraycart.clear()
                cartAdapter.notifyDataSetChanged()
                Cartlocaldb.cartDao().delete()
                resetOrderConfirmations()
            }
            getData(subtotal, biayajasa, biayatotal)
        }


        initcart(binding.root)

        return binding.root
    }

    private fun uploadOrder() {

        val orderByKantin = arraycartorder.groupBy { it.id_kantin }

        orderByKantin.forEach { (kantinId, items) ->

            val menuItems = items.groupBy { it.id_makanan.toString() }
                .mapValues { (_, items) -> items.sumOf { it.jumlah } }

            val totalPrice = items.sumOf { it.harga * it.jumlah }
            val tableNumber = pre.nomor_meja ?: return
            val userid = FirebaseAuth.getInstance().currentUser?.uid ?: return

            val orderData = hashMapOf(
                "canteen_id" to kantinId,
                "meja" to tableNumber,
                "menu_items" to menuItems,
                "order_time" to FieldValue.serverTimestamp(),
                "status" to 2,
                "total_price" to totalPrice,
                "user_id" to userid
            )

            FirebaseFirestore.getInstance().collection("orders")
                .add(orderData)
                .addOnSuccessListener {
                    showLoadingConfirmation()
                    listenForConfirmation(it.id, kantinId)
                    Log.d(TAG, "uploadOrder: $it")
                }.addOnFailureListener {
                    Log.w(TAG, "uploadOrdererror: ",it)
                }

        }


    }

    private fun updatingOrderQueue(kantinId: Int){
        var orderqueue: Long =0
        FirebaseFirestore.getInstance()
            .collection("canteens")
            .document(kantinId.toString())
            .get().addOnSuccessListener {
                orderqueue = it.get("order_queue") as Long
            }.addOnSuccessListener {
                FirebaseFirestore.getInstance()
                    .collection("canteens")
                    .document(kantinId.toString())
                    .update("order_queue", orderqueue+1)
            }
    }

    private fun listenForConfirmation(orderId: String, kantinId: Int){
        orderConfirmations[orderId] = false

        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .addSnapshotListener { value, error ->
                if (error != null){
                    Log.w(TAG, "listenForConfirmation: ", error)
                    return@addSnapshotListener
                }

                if (value != null && value.exists()){
                    val status = value.getLong("status")
                    if (status == 1.toLong()){
                        Log.d(TAG, "listenForConfirmation2: $value")
                        orderConfirmations[orderId] = true
                        checkAllOrderConfirmed()
                        updatingOrderQueue(kantinId)
                    }
                }
            }
    }

    private fun checkAllOrderConfirmed() {
        if (orderConfirmations.values.all { it }) { // Check if all orders are confirmed
            hideLoadingScreen()
        }
    }

    private fun showLoadingConfirmation() {
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            return // Dialog is already showing, no need to create a new one
        }
        loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(R.layout.dialog_confirmation)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun hideLoadingScreen() {
        Log.d(TAG, "hideLoadingScreen: ")

        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
            Log.d(TAG, "hideLoadingScreen2: ")

        }
    }

    private fun resetOrderConfirmations() {
        orderConfirmations.clear()
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