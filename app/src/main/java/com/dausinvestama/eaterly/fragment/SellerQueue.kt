package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.QueueAdapter
import com.dausinvestama.eaterly.data.Menu
import com.dausinvestama.eaterly.data.QueueData
import com.dausinvestama.eaterly.databinding.FragmentSellerQueueBinding
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Queue

class SellerQueue : Fragment() {
    private lateinit var binding: FragmentSellerQueueBinding
    private lateinit var pendingAdapter: QueueAdapter
    private lateinit var preppingAdapter: QueueAdapter

    private val db = FirebaseFirestore.getInstance()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBarPending : ProgressBar
    private lateinit var progressBarPrepping : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerQueueBinding.inflate(layoutInflater)

        binding.apply {
            val pendingAdapter = QueueAdapter(mutableListOf(), requireContext())
            val prepAdapter = QueueAdapter(mutableListOf(), requireContext())

            rvPending.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = pendingAdapter
                setHasFixedSize(true)
            }

            rvPrep.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = prepAdapter
                setHasFixedSize(true)
            }

            rvDelivering.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = prepAdapter
                setHasFixedSize(true)
            }

            setUpMinimizeAndExpand(btnMinPending, rvPending)
            setUpMinimizeAndExpand(btnMinPrep, rvPrep)
            setUpMinimizeAndExpand(btnMinDeliv, rvDelivering)

            getData(pbPending, rvPending, llEmptyPending, 2)
            getData(pbPrepping, rvPrep, llEmptyPrepping, 0)
            getData(pbDelivering, rvDelivering, llEmptyDelivering, 1)
        }

        return binding.root
    }

    private fun setUpMinimizeAndExpand(btnMin: ImageButton, recyclerView: RecyclerView){
        btnMin.setOnClickListener {
            if (recyclerView.visibility == View.VISIBLE){
                recyclerView.visibility = View.GONE
                btnMin.setImageResource(R.drawable.ic_expand_more)
            } else {
                recyclerView.visibility = View.VISIBLE
                btnMin.setImageResource(R.drawable.ic_expand_less)
            }
        }
    }

    private fun getData(pb: ProgressBar, rv: RecyclerView, ll: LinearLayout, status: Int) = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) {pb.visibility = View.VISIBLE}
        val sellerId = FirebaseAuth.getInstance().currentUser?.uid

        val canteens = db.collection("canteens")
            .whereEqualTo("seller", sellerId)
            .limit(1)
            .get()
            .await()

        val canteenId = canteens.documents.firstOrNull()
        Log.d(TAG, "getDataUid: $sellerId, $canteenId")
        val queues = mutableListOf<QueueData>()

        try {
            val queueList = db.collection("orders")
                .whereEqualTo("canteen_id", canteenId)
                .get()
                .await()

            Log.d(TAG, "queues: ${queueList.size()}")

            for (queueDoc in queueList) {
                try {
                    queueDoc.apply {
                        val userId = get("user_id")
                        val table = get("meja")
                        val time = get("order_time")
                        val menuItemsMap = get("menu_items") as Map<*, *>?
                        val menusList = mutableListOf<Menu>()
                        menuItemsMap?.forEach { (menuId, quantity) ->
                            val menuDocument = db.collection("menus")
                                .document(menuId.toString())
                                .get()
                                .await()

                            if (menuDocument.exists()){

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "err: ${e.localizedMessage}")
                }
            }



        } catch (e: Exception){
            Log.d(TAG, "getData error: $e")
        }
    }
}