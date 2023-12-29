package com.dausinvestama.eaterly.pages

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.adapter.QueueAdapter
import com.dausinvestama.eaterly.data.Menu
import com.dausinvestama.eaterly.data.QueueData
import com.dausinvestama.eaterly.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private val db = FirebaseFirestore.getInstance()

    private lateinit var historyAdapter: QueueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyAdapter = QueueAdapter(mutableListOf(), this@HistoryActivity)

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            rvContent.apply {
                layoutManager = LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
                adapter = historyAdapter
                setHasFixedSize(true)

                getData(historyAdapter, pbLoading, rvContent, llEmpty, 4, tvCount)
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val canteens = db.collection("canteens")
            .whereEqualTo("seller", userId)
            .limit(1)
            .get()
            .await()

        val canteenId = canteens.documents.firstOrNull()?.id?.toInt()

        Log.d(ContentValues.TAG, "getDataUid: $userId, $canteenId")
        val queues = mutableListOf<QueueData>()

        if (canteenId == null){
            userHistoryInit(userId, status, queues)
        } else {
            sellerHistoryInit(canteenId, status, queues)
        }

        withContext(Dispatchers.Main) {
            if (queues.isNotEmpty()) {
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

    private suspend fun sellerHistoryInit(canteenId: Int?, status: Int, queues: MutableList<QueueData>){
        try {
            val queueList = db.collection("orders")
                .whereEqualTo("canteen_id", canteenId)
                .whereEqualTo("status", status)
                .get()
                .await()

            Log.d(ContentValues.TAG, "queues: ${queueList.size()}")

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
                    Log.d(ContentValues.TAG, "err: ${e.localizedMessage}")
                }
            }

        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "getData error: $e")
        }
    }

    private suspend fun userHistoryInit(userId: String?, status: Int, queues: MutableList<QueueData>){
        try {
            val queueList = db.collection("orders")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("status", status)
                .orderBy("order_time", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(ContentValues.TAG, "queues: ${queueList.size()}")

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
                    Log.d(ContentValues.TAG, "err: ${e.localizedMessage}")
                }
            }

        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "getData error: $e")
        }
    }
}