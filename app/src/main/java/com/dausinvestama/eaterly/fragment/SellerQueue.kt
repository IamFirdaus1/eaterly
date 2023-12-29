package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.QueueAdapter
import com.dausinvestama.eaterly.adapter.QueueAdapter.OnAcceptClickCallback
import com.dausinvestama.eaterly.data.Menu
import com.dausinvestama.eaterly.data.QueueData
import com.dausinvestama.eaterly.databinding.FragmentSellerQueueBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SellerQueue : Fragment() {
    private lateinit var binding: FragmentSellerQueueBinding

    private val db = FirebaseFirestore.getInstance()

    private lateinit var pendingAdapter: QueueAdapter
    private lateinit var prepAdapter: QueueAdapter
    private lateinit var deliveringAdapter: QueueAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        pendingAdapter = QueueAdapter(mutableListOf(), context)
        prepAdapter = QueueAdapter(mutableListOf(), context)
        deliveringAdapter = QueueAdapter(mutableListOf(), context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerQueueBinding.inflate(layoutInflater)

        binding.rvPending.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = pendingAdapter
            setHasFixedSize(true)
        }

        binding.rvPrep.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = prepAdapter
            setHasFixedSize(true)
        }

        binding.rvDelivering.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = deliveringAdapter
            setHasFixedSize(true)
        }

        initAll()

        return binding.root
    }

    private fun initAll() {
        binding.apply {
            getData(pendingAdapter, pbPending, rvPending, llEmptyPending, 2, tvPendingCount)
            getData(prepAdapter, pbPrepping, rvPrep, llEmptyPrepping, 0, tvPreparingCount)
            getData(deliveringAdapter, pbDelivering, rvDelivering, llEmptyDelivering, 1, tvDeliveringCount)

            setUpMinimizeAndExpand(btnMinPending, flPending)
            setUpMinimizeAndExpand(btnMinPrep, flPrepping)
            setUpMinimizeAndExpand(btnMinDeliv, flDelivering)
        }
    }

    private fun setUpMinimizeAndExpand(btnMin: ImageButton, frameLayout: FrameLayout) {
        btnMin.setOnClickListener {
            if (frameLayout.visibility == View.VISIBLE) {
                frameLayout.visibility = View.GONE
                btnMin.setImageResource(R.drawable.ic_expand_more)
            } else {
                frameLayout.visibility = View.VISIBLE
                btnMin.setImageResource(R.drawable.ic_expand_less)
            }
        }
    }

    private fun getData(
        adapter: QueueAdapter,
        pb: ProgressBar,
        rv: RecyclerView,
        ll: LinearLayout,
        status: Int,
        tvCount: TextView
    ) = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) { pb.visibility = View.VISIBLE }
        val sellerId = FirebaseAuth.getInstance().currentUser?.uid

        val canteens = db.collection("canteens")
            .whereEqualTo("seller", sellerId)
            .limit(1)
            .get()
            .await()

        val canteenId = canteens.documents.firstOrNull()?.id?.toInt()
        Log.d(TAG, "getDataUid: $sellerId, $canteenId")
        val queues = mutableListOf<QueueData>()

        try {
            val queueList = db.collection("orders")
                .whereEqualTo("canteen_id", canteenId)
                .whereEqualTo("status", status)
                .get()
                .await()

            Log.d(TAG, "queues: ${queueList.size()}")

            for (queueDoc in queueList) {
                try {
                    queueDoc.apply {
                        val table = get("meja")
                        val time = get("order_time").toString()
                        val price = get("total_price")
                        val id = queueDoc.id

                        val menuItemsMap = get("menu_items") as Map<*, *>?
                        val menusList = mutableListOf<Menu>()
                        menuItemsMap?.forEach { (menuId, quantity) ->
                            val menuDocument = db.collection("menus")
                                .document(menuId.toString())
                                .get()
                                .await()

                            if (menuDocument.exists()) {
                                val menuName = menuDocument.getString("name")
                                val url = menuDocument.getString("url")

                                menusList.add(
                                    Menu(
                                        id,
                                        menuId,
                                        menuName,
                                        quantity,
                                        status,
                                        price,
                                        table,
                                        url
                                    )
                                )
                            }
                        }


                        queues.add(
                            QueueData(
                                time, menusList
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "err: ${e.localizedMessage}")
                }
            }


        } catch (e: Exception) {
            Log.d(TAG, "getData error: $e")
        }

        withContext(Dispatchers.Main) {
            if (queues.isNotEmpty()) {
                when (status.toString()) {
                    "0" -> {
                        adapter.setOnAcceptClickCallback(object : OnAcceptClickCallback {
                            override fun onAcceptClick(orderId: String) {
                                changeStatus(orderId, 1)
                            }
                        })
                    }

                    "1" -> {
                        adapter.setOnAcceptClickCallback(object : OnAcceptClickCallback {
                            override fun onAcceptClick(orderId: String) {
                                changeStatus(orderId, 4)
                                decrementQueue(canteens)
                            }
                        })
                    }
                    "2" -> {
                        adapter.setOnAcceptClickCallback(object : OnAcceptClickCallback {
                            override fun onAcceptClick(orderId: String) {
                                changeStatus(orderId, 0)
                            }
                        })
                    }

                    else -> {}
                }

                adapter.setOnDenyClickCallback(object : QueueAdapter.OnDenyClickCallback {
                    override fun onDenyClick(orderId: String) {
                        changeStatus(orderId, 3)
                        decrementQueue(canteens)
                    }
                })

                adapter.updateData(queues)

                ll.visibility = View.GONE
                rv.visibility = View.VISIBLE
            } else {
                ll.visibility = View.VISIBLE
                rv.visibility = View.GONE
            }
            tvCount.text = queues.size.toString()
            pb.visibility = View.GONE
        }
    }

    private fun changeStatus(orderId: String, status: Int) {
        val update = hashMapOf<String, Any>(
            "status" to status
        )

        db.collection("orders").document(orderId)
            .update(update)
            .addOnSuccessListener {
                initAll()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Action Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun decrementQueue(canteens: QuerySnapshot){
        if (!canteens.isEmpty) {
            val firstCanteenId = canteens.documents.firstOrNull()?.id
            firstCanteenId?.let { canteenId ->
                db.collection("canteens")
                    .document(canteenId)
                    .update("order_queue", FieldValue.increment(-1)) // Decrement by 1
                    .addOnSuccessListener {
                        Log.d(TAG, "order_queue decremented successfully")
                        // Handle the success case
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error decrementing order_queue", e)
                        // Handle the failure case
                    }
            }
        } else {
            Log.d(TAG, "No documents found in canteens collection query snapshot.")
            // Handle the case when the canteens query snapshot is empty
        }
    }

}