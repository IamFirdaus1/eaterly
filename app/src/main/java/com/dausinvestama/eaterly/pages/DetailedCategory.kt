package com.dausinvestama.eaterly.pages

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.CategoryAdapter
import com.dausinvestama.eaterly.adapter.DetailMakananAdapter
import com.dausinvestama.eaterly.data.CategoryDetailData
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.data.IntroList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class DetailedCategory : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    lateinit var kategoritextview: TextView
    lateinit var detailRecycler: RecyclerView


    lateinit var detailMakananAdapter: DetailMakananAdapter
    var detalList: ArrayList<CategoryDetailData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_list)

        detailRecycler  = findViewById(R.id.detailrecycler)
        kategoritextview = findViewById(R.id.categories)
        val categoryList = intent.getParcelableExtra<CategoryList>("categorylist")


        val id_categories: Int? = categoryList?.id_kategori
        if (categoryList != null) {
            kategoritextview.setText(categoryList.Categorylist)
        }
        if (id_categories != null) {
            initapi(id_categories)
        }

    }

    fun initapi(id_categories: Int) {
        val categoryList = intent.getParcelableExtra<CategoryList>("categorylist")

        db.collection("kategoritesting").document(id_categories.toString()).collection("member").get().addOnSuccessListener {result ->
            for (document in result){
                Log.d(TAG, "kategori reference : ${document.get("reference")}")
                db.collection("makanan").document(document.get("reference") as String).get().addOnSuccessListener { results->
                    Log.d(TAG, "kategori reference : ${results.get("id_kantin")}")
                    var id_jenis: Long = results.get("id_jenis") as Long
                    var id_makanan: Long = results.get("id_makanan") as Long
                    var id_kantin: Long = results.get("id_kantin") as Long
                    var nama_makanan: String = results.get("nama_makanan") as String
                    var desc_makanan: String = results.get("desc_makanan") as String
                    var harga_makanan: Long = results.get("harga_makanan") as Long
                    Log.d(TAG, "id_kantin from makanan reference : $id_kantin")
                    db.collection("kantintesting").document(id_kantin.toString()).get().addOnSuccessListener { hasil ->
                        var order_id: Long = hasil.get("order_id") as Long
                        var nama_kantin: String = hasil.get("nama_kantin") as String

                        Log.d(TAG, "order_id reference : $order_id")
                        db.collection("orderlist")
                            .document("24-01-2023")
                            .collection(order_id.toString())
                            .whereEqualTo("status_pesanan", 0)
                            .get().addOnSuccessListener { resultan ->
                                Log.d(TAG, "antrian: " + resultan.size() + " " + id_kantin + " " +order_id )
                                Log.d(TAG, "hasil akhir: nama kantin:${nama_kantin} nama_makanan: ${nama_makanan}  desc makanan: ${desc_makanan} antrian: ${resultan.size()}" )
                                detalList.add(CategoryDetailData(nama_makanan, id_makanan,id_jenis, harga_makanan, id_kantin, desc_makanan, nama_kantin, resultan.size()))
                                Log.d(TAG, "docsa:" )

                                detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                detailRecycler.adapter = detailMakananAdapter
                                var layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                detailRecycler.setHasFixedSize(true)
                                detailRecycler.layoutManager = layoutManager
                        }

                    }
                }
            }



        }

    }


}