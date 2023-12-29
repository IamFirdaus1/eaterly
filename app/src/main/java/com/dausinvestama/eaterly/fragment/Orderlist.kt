package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.adapter.CanteenOrderAdapter
import com.dausinvestama.eaterly.data.Menu
import com.dausinvestama.eaterly.data.OrderListData
import com.dausinvestama.eaterly.databinding.FragmentOrderlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Orderlist : Fragment() {
    private lateinit var binding: FragmentOrderlistBinding

    private lateinit var canteenAdapter: CanteenOrderAdapter

    val db = FirebaseFirestore.getInstance()

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderlistBinding.inflate(layoutInflater)
        val view = binding.root

        recycler = binding.recyclerhistoryfragment
        progressBar = binding.progressBar

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        canteenAdapter = CanteenOrderAdapter(mutableListOf())
        recycler.adapter = canteenAdapter

        getData()

        return view
    }


    private fun getData() = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) { progressBar.visibility = View.VISIBLE }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d(TAG, "getDataUid: $userId")
        try {
            val orders = db.collection("orders")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
            val canteensWithMenusList = mutableListOf<OrderListData>()
            Log.d(TAG, "Orderlist testing 1: ${orders.size()}")
            for (orderDocument in orders) {
                try {
                    Log.d(TAG, "Orderlist testing 2: $")
                    val canteenId = orderDocument.get("canteen_id")
                    val price = orderDocument.get("total_price")
                    Log.d(TAG, "Orderlist testing 3: $canteenId price $price")
                    canteenId?.let { id ->
                        val canteenDocument = db.collection("canteens")
                            .document(id.toString())
                            .get()
                            .await()

                        if (canteenDocument.exists()) {
                            val canteenName = canteenDocument.getString("name")

                            val menuItemsMap = orderDocument.get("menu_items") as Map<*, *>?
                            val menusList = mutableListOf<Menu>()

                            menuItemsMap?.forEach { (menuId, quantity) ->
                                val menuDocument = db.collection("menus")
                                    .document(menuId.toString())
                                    .get()
                                    .await()

                                if (menuDocument.exists()) {
                                    val menuName = menuDocument.getString("name")
                                    val price = orderDocument.get("total_price")
                                    val status = orderDocument.get("status")
                                    val meja = orderDocument.get("meja")
                                    val orderid = orderDocument.id
                                    val url = menuDocument.getString("url")
                                    Log.d(
                                        TAG,
                                        "Orderlist testing4: $canteenName menu $menuName quantity $quantity price $price"
                                    )
                                    menusList.add(
                                        Menu(
                                            orderid,
                                            menuId,
                                            menuName,
                                            quantity,
                                            status,
                                            price,
                                            meja,
                                            url
                                        )
                                    )

                                }
                            }
                            val canteenWithMenus = OrderListData(canteenId, canteenName, menusList)
                            canteensWithMenusList.add(canteenWithMenus)

                        } else {

                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "getData error: $e")
                }

            }
            withContext(Dispatchers.Main) {
                if (canteensWithMenusList.isNotEmpty()) {
                    canteenAdapter = CanteenOrderAdapter(canteensWithMenusList)
                    recycler.adapter = canteenAdapter

                    progressBar.visibility = View.GONE
                } else {
                }
            }
        } catch (e: Exception) {

        }
    }


}