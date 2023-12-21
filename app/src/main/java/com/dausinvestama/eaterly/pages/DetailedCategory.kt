package com.dausinvestama.eaterly.pages

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.AppDatabase
import com.dausinvestama.eaterly.CartDatabase
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.DetailMakananAdapter
import com.dausinvestama.eaterly.data.*
import com.dausinvestama.eaterly.database.CartDb
import com.dausinvestama.eaterly.database.CartItemDb
import com.dausinvestama.eaterly.databinding.ActivityDetailedListBinding
import com.dausinvestama.eaterly.fragment.Cart
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore

class DetailedCategory : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityDetailedListBinding
    lateinit var kategoritextview: TextView
    lateinit var detailRecycler: RecyclerView
    lateinit var progressBar: ProgressBar

    lateinit private var localdb: AppDatabase
    lateinit private var cartlocaldb: CartDatabase

    var arraycart: ArrayList<CartItemDb> = ArrayList()

    lateinit var detailMakananAdapter: DetailMakananAdapter
    var detalList: ArrayList<CategoryDetailData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        detailRecycler  = binding.detailrecycler
        kategoritextview = binding.categories
        val categoryList = intent.getParcelableExtra<CategoryList>("categorylist")
        val kantinList = intent.getParcelableExtra<KantinList>("kantinList")
        val jenisList = intent.getParcelableExtra<JenisList>("jenisList")


        val id_categories: Int? = categoryList?.id_kategori
        val id_kantin: Int? = kantinList?.idkantin
        val id_jenis: Int? = jenisList?.id_jenis
        progressBar.visibility = View.VISIBLE

        Log.d(TAG, "cekpoint1: ${id_jenis} ")

        if (categoryList != null) {
            kategoritextview.setText(categoryList.Categorylist)

        }else if (id_kantin != null) {
            kategoritextview.setText(kantinList.NamaKantin)
            initkantin1(id_kantin)

        }
        if (id_jenis != null) {
            initjenis1(id_jenis)

        }
        if (id_categories != null) {
            initkategori(id_categories)

        }

    }

    private fun destroyProgress() {
        progressBar.visibility = View.GONE
    }

    private fun initjenis1(id_jenis: Int) {
        var pre = SharedPreferences(this)

        db.collection("menus")
            .whereEqualTo("location_id", pre.location_id)
            .whereEqualTo("type_id", id_jenis)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    var id_makanan: Long = document.id.toLong()
                    var id_kantin: Long = document.get("canteen_id") as Long
                    var nama_makanan: String = document.get("name") as String
                    var desc_makanan: String = document.get("description") as String
                    var harga_makanan: Long = document.get("price") as Long
                    var gambar_makanan: String = document.get("url") as String
                    var id_jenis2: Long = document.id.toLong()


                    db.collection("canteens")
                        .document(id_kantin.toString())
                        .get()
                        .addOnSuccessListener {canteenDocument ->
                            if (canteenDocument.exists()) {
                                var nama_kantin = canteenDocument.getString("name") as String
                                var orderqueue = canteenDocument.get("order_queue")
                                detalList.add(CategoryDetailData(
                                    nama_makanan,
                                    id_makanan,
                                    id_jenis2,
                                    harga_makanan,
                                    id_kantin,
                                    desc_makanan,
                                    nama_kantin,
                                    orderqueue,
                                    gambar_makanan

                                ))
                                detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                detailRecycler.adapter = detailMakananAdapter
                                var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                detailRecycler.setHasFixedSize(true)
                                detailRecycler.layoutManager = layoutmanager

                                destroyProgress()

                                localdb = AppDatabase.getInstance(applicationContext)
                                cartlocaldb = CartDatabase.getInstance(applicationContext)

                                detailMakananAdapter.onItemClick = {

                                    if (it.namakantin.toString().isNotEmpty() && it.idkantin.toString().isNotEmpty()){
                                        if (localdb.cartDao().getBymakanan(it.idmakanan.toInt()).isEmpty()){
                                            localdb.cartDao().InsertAll(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }else{
                                            var jumlahtemp: CartItemDb = localdb.cartDao().getBymakanan(it.idmakanan.toInt())[0]
                                            localdb.cartDao().UpdateOrder(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                jumlahtemp.jumlah+1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }
                                        if (cartlocaldb.outerCartDao().getbyId(it.idkantin.toInt()).isEmpty()){
                                            cartlocaldb.outerCartDao().InsertAll(CartDb(it.idkantin.toInt(), it.namakantin.toString()))
                                        }else{
                                            Log.d(TAG, "initdetail: ini kagak" )
                                        }


                                    }

                                }
                            }
                        }.addOnFailureListener {

                        }
                }
            }.addOnFailureListener {

            }
    }

    fun initkantin1(idKantin: Int){
        db.collection("menus")
            .whereEqualTo("canteen_id", idKantin)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    var id_makanan: Long = document.id.toLong()
                    var id_kantin: Long = document.get("canteen_id") as Long
                    var nama_makanan: String = document.get("name") as String
                    var desc_makanan: String = document.get("description") as String
                    var harga_makanan: Long = document.get("price") as Long
                    var gambar_makanan: String = document.get("url") as String
                    var id_jenis2: Long = document.id.toLong()


                    db.collection("canteens")
                        .document(id_kantin.toString())
                        .get()
                        .addOnSuccessListener {canteenDocument ->
                            if (canteenDocument.exists()) {
                                var nama_kantin = canteenDocument.getString("name") as String
                                var orderqueue = canteenDocument.get("order_queue")
                                detalList.add(CategoryDetailData(
                                    nama_makanan,
                                    id_makanan,
                                    id_jenis2,
                                    harga_makanan,
                                    id_kantin,
                                    desc_makanan,
                                    nama_kantin,
                                    orderqueue,
                                    gambar_makanan

                                ))
                                detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                detailRecycler.adapter = detailMakananAdapter
                                var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                detailRecycler.setHasFixedSize(true)
                                detailRecycler.layoutManager = layoutmanager

                                destroyProgress()

                                localdb = AppDatabase.getInstance(applicationContext)
                                cartlocaldb = CartDatabase.getInstance(applicationContext)

                                detailMakananAdapter.onItemClick = {

                                    if (it.namakantin.toString().isNotEmpty() && it.idkantin.toString().isNotEmpty()){
                                        if (localdb.cartDao().getBymakanan(it.idmakanan.toInt()).isEmpty()){
                                            localdb.cartDao().InsertAll(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }else{
                                            var jumlahtemp: CartItemDb = localdb.cartDao().getBymakanan(it.idmakanan.toInt())[0]
                                            localdb.cartDao().UpdateOrder(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                jumlahtemp.jumlah+1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }
                                        if (cartlocaldb.outerCartDao().getbyId(it.idkantin.toInt()).isEmpty()){
                                            cartlocaldb.outerCartDao().InsertAll(CartDb(it.idkantin.toInt(), it.namakantin.toString()))
                                        }else{
                                            Log.d(TAG, "initdetail: ini kagak" )
                                        }


                                    }

                                }
                            }
                        }.addOnFailureListener {

                        }
                }
            }.addOnFailureListener {

            }

    }

    fun initkategori(id_categories: Int) {

        var pre = SharedPreferences(this)

        db.collection("menus")
            .whereEqualTo("location_id", pre.location_id)
            .whereEqualTo("category_id", id_categories)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    var id_makanan: Long = document.id.toLong()
                    var id_kantin: Long = document.get("canteen_id") as Long
                    var nama_makanan: String = document.get("name") as String
                    var desc_makanan: String = document.get("description") as String
                    var harga_makanan: Long = document.get("price") as Long
                    var gambar_makanan: String = document.get("url") as String
                    var id_jenis2: Long = document.id.toLong()


                    db.collection("canteens")
                        .document(id_kantin.toString())
                        .get()
                        .addOnSuccessListener {canteenDocument ->
                            if (canteenDocument.exists()) {
                                var nama_kantin = canteenDocument.getString("name") as String
                                var orderqueue = canteenDocument.get("order_queue")
                                detalList.add(CategoryDetailData(
                                    nama_makanan,
                                    id_makanan,
                                    id_jenis2,
                                    harga_makanan,
                                    id_kantin,
                                    desc_makanan,
                                    nama_kantin,
                                    orderqueue,
                                    gambar_makanan

                                ))
                                detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                detailRecycler.adapter = detailMakananAdapter
                                var layoutmanager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                detailRecycler.setHasFixedSize(true)
                                detailRecycler.layoutManager = layoutmanager

                                destroyProgress()

                                localdb = AppDatabase.getInstance(applicationContext)
                                cartlocaldb = CartDatabase.getInstance(applicationContext)

                                detailMakananAdapter.onItemClick = {

                                    if (it.namakantin.toString().isNotEmpty() && it.idkantin.toString().isNotEmpty()){
                                        if (localdb.cartDao().getBymakanan(it.idmakanan.toInt()).isEmpty()){
                                            localdb.cartDao().InsertAll(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }else{
                                            var jumlahtemp: CartItemDb = localdb.cartDao().getBymakanan(it.idmakanan.toInt())[0]
                                            localdb.cartDao().UpdateOrder(CartItemDb(
                                                it.idmakanan.toInt(),
                                                it.hargamakanan.toInt(),
                                                jumlahtemp.jumlah+1,
                                                it.namamakanan.toString(),
                                                it.idkantin.toInt(),
                                                it.idjenis.toInt(),
                                                it.namakantin.toString(),
                                                it.gambar_makanan
                                            ))
                                            finish()
                                        }
                                        if (cartlocaldb.outerCartDao().getbyId(it.idkantin.toInt()).isEmpty()){
                                            cartlocaldb.outerCartDao().InsertAll(CartDb(it.idkantin.toInt(), it.namakantin.toString()))
                                        }else{
                                            Log.d(TAG, "initdetail: ini kagak" )
                                        }


                                    }

                                }
                            }
                        }.addOnFailureListener {

                        }
                }
            }.addOnFailureListener {

            }

    }
}