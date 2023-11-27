package com.dausinvestama.eaterly.pages

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.dausinvestama.eaterly.utils.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore

class DetailedCategory : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityDetailedListBinding
    lateinit var kategoritextview: TextView
    lateinit var detailRecycler: RecyclerView

    lateinit private var localdb: AppDatabase
    lateinit private var cartlocaldb: CartDatabase

    var arraycart: ArrayList<CartItemDb> = ArrayList()

    lateinit var detailMakananAdapter: DetailMakananAdapter
    var detalList: ArrayList<CategoryDetailData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailRecycler  = binding.detailrecycler
        kategoritextview = binding.categories
        val categoryList = intent.getParcelableExtra<CategoryList>("categorylist")
        val kantinList = intent.getParcelableExtra<KantinList>("kantinList")
        val jenisList = intent.getParcelableExtra<JenisList>("jenisList")


        val id_categories: Int? = categoryList?.id_kategori
        val id_kantin: Int? = kantinList?.idkantin
        val id_jenis: Int? = jenisList?.id_jenis

        Log.d(TAG, "cekpoint1: ${id_jenis} ")

        if (categoryList != null) {
            kategoritextview.setText(categoryList.Categorylist)
        }else if (id_kantin != null) {
            val nama_kantin: String = kantinList?.NamaKantin.toString()
            val order_id: Int = kantinList?.orderid!!
            kategoritextview.setText(kantinList.NamaKantin)
            initkantin(id_kantin,order_id, nama_kantin)
        }
        if (id_jenis != null) {
            initjenis(id_jenis)
        }
        if (id_categories != null) {
            initapi(id_categories)
        }

    }

    private fun initjenis(id_jenis: Int) {
        var pre = SharedPreferences(this)
        Log.d(TAG, "cekpoint: ${pre.location} ")

        db.collection("makanan")
            .document(pre.location.toString())
            .collection("1")
            .whereEqualTo("id_jenis", id_jenis)
            .get().addOnSuccessListener { result ->
                for (document in result) {

                    var id_jeniss: Long = document.get("id_jenis") as Long
                    var id_makanan: Long = document.get("id_makanan") as Long
                    var id_kantin: Long = document.get("id_kantin") as Long
                    var nama_makanan: String = document.get("nama_makanan") as String
                    var desc_makanan: String = document.get("desc") as String
                    var harga_makanan: Long = document.get("harga_makanan") as Long
                    var gambar_makanan: String = document.get("gambar_makanan") as String

                    Log.d(TAG, "initjenis: $id_jeniss $id_makanan ")

                    db.collection("kantintesting")
                        .document(pre.location.toString())
                        .collection("Kantin")
                        .document(id_kantin.toString())
                        .get().addOnSuccessListener { hasil ->
                            var id_kantin: Long = hasil.get("id_kantin") as Long
                            var nama_kantin: String = hasil.get("nama_kantin") as String

                            db.collection("orderlist")
                                .document(pre.location.toString())
                                .collection(id_kantin.toString())
                                .whereEqualTo("status_pesanan", 0)
                                .get().addOnSuccessListener { resultan ->

                                    detalList.add(CategoryDetailData(nama_makanan,
                                        id_makanan,
                                        id_jeniss,
                                        harga_makanan,
                                        id_kantin,
                                        desc_makanan,
                                        nama_kantin,
                                        resultan.size(),
                                        gambar_makanan))

                                    detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                    detailRecycler.adapter = detailMakananAdapter
                                    var layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                    detailRecycler.setHasFixedSize(true)
                                    detailRecycler.layoutManager = layoutManager

                                    localdb = AppDatabase.getInstance(applicationContext)
                                    cartlocaldb = CartDatabase.getInstance(applicationContext)

                                    detailMakananAdapter.onItemClick = { it


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
                                        Log.d(TAG, "pilihan: " + it.namamakanan )
                                    }
                                }
                        }
                }
            }
    }

    fun initkantin(idKantin: Int, order_id: Int, nama_kantin: String) {
         var pre = SharedPreferences(this)
         db.collection("makanan")
             .document(pre.location.toString())
             .collection("1")
             .whereEqualTo("id_kantin", idKantin)
             .get().addOnSuccessListener { result ->
                 for (document in result) {
                     var id_jenis: Long = document.get("id_jenis") as Long
                     var id_makanan: Long = document.get("id_makanan") as Long
                     var id_kantin: Long = document.get("id_kantin") as Long
                     var nama_makanan: String = document.get("nama_makanan") as String
                     var desc_makanan: String = document.get("desc") as String
                     var harga_makanan: Long = document.get("harga_makanan") as Long
                     var gambar_makanan: String = document.get("gambar_makanan") as String

                     db.collection("orderlist")
                         .document(pre.location.toString())
                         .collection(id_kantin.toString())
                         .whereEqualTo("status_pesanan", 0)
                         .get().addOnSuccessListener { resultan ->

                             detalList.add(CategoryDetailData(
                                 nama_makanan,
                                 id_makanan,
                                 id_jenis,
                                 harga_makanan,
                                 id_kantin,
                                 desc_makanan,
                                 nama_kantin,
                                 resultan.size(),
                                 gambar_makanan))

                             detailMakananAdapter = DetailMakananAdapter(this, detalList)
                             detailRecycler.adapter = detailMakananAdapter
                             var layoutManager: RecyclerView.LayoutManager = GridLayoutManager(
                                 this,
                                 1)
                             detailRecycler.setHasFixedSize(true)
                             detailRecycler.layoutManager = layoutManager

                             localdb = AppDatabase.getInstance(applicationContext)
                             cartlocaldb = CartDatabase.getInstance(applicationContext)

                             detailMakananAdapter.onItemClick = {it


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



//                                                arraycart.add(CartData(it.namakantin.toString(), it.idkantin))
//                                                arraycartorder.add(CartOrderData("https://firebasestorage.googleapis.com/v0/b/eaterlytestapi.appspot.com/o/Imagesicons8-kawaii-soda-100.png?alt=media&token=ecc15eb6-9012-4919-9ebb-6ff7035ffd5b",
//                                                    it.idmakanan, it.namamakanan.toString(), 1, it.hargamakanan, it.idkantin))
                                 Log.d(TAG, "pilihan: " + it.namamakanan )
                             }
                         }

                 }
             }

    }

    fun initapi(id_categories: Int) {
        val categoryList = intent.getParcelableExtra<CategoryList>("categorylist")

        var pre = SharedPreferences(this)
        db.collection("kategoritesting")
            .document(pre.location.toString()).collection("kategori")
            .document(id_categories.toString())
            .collection("member")
            .get().addOnSuccessListener {result ->
                for (document in result) {
                    db.collection("makanan")
                        .document(pre.location.toString())
                        .collection("1")
                        .document(document.get("reference")
                            .toString()).get().addOnSuccessListener { results ->
                            var id_jenis: Long = results.get("id_jenis") as Long
                            var id_makanan: Long = results.get("id_makanan") as Long
                            var id_kantin: Long = results.get("id_kantin") as Long
                            var nama_makanan: String = results.get("nama_makanan") as String
                            var desc_makanan: String = results.get("desc") as String
                            var harga_makanan: Long = results.get("harga_makanan") as Long
                            var gambar_makanan: String = results.get("gambar_makanan") as String

                            db.collection("kantintesting")
                                .document(pre.location.toString())
                                .collection("Kantin")
                                .document(id_kantin.toString())
                                .get().addOnSuccessListener { hasil ->
                                    var id_kantin: Long = hasil.get("id_kantin") as Long
                                    var nama_kantin: String = hasil.get("nama_kantin") as String

                                    db.collection("orderlist")
                                        .document("24-01-2023")
                                        .collection(pre.location.toString())
                                        .document(id_kantin.toString())
                                        .collection("order")
                                        .whereEqualTo("status_pesanan", 0)
                                        .get().addOnSuccessListener { resultan ->

                                            detalList.add(CategoryDetailData(
                                                nama_makanan,
                                                id_makanan,
                                                id_jenis,
                                                harga_makanan,
                                                id_kantin,
                                                desc_makanan,
                                                nama_kantin,
                                                resultan.size(),
                                                gambar_makanan))

                                            detailMakananAdapter = DetailMakananAdapter(this, detalList)
                                            detailRecycler.adapter = detailMakananAdapter
                                            var layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
                                            detailRecycler.setHasFixedSize(true)
                                            detailRecycler.layoutManager = layoutManager

                                            localdb = AppDatabase.getInstance(applicationContext)
                                            cartlocaldb = CartDatabase.getInstance(applicationContext)

                                            detailMakananAdapter.onItemClick = {it

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



//                                                arraycart.add(CartData(it.namakantin.toString(), it.idkantin))
//                                                arraycartorder.add(CartOrderData("https://firebasestorage.googleapis.com/v0/b/eaterlytestapi.appspot.com/o/Imagesicons8-kawaii-soda-100.png?alt=media&token=ecc15eb6-9012-4919-9ebb-6ff7035ffd5b",
//                                                    it.idmakanan, it.namamakanan.toString(), 1, it.hargamakanan, it.idkantin))
                                                Log.d(TAG, "pilihan: " + it.namamakanan )
                                            }
                                        }


                                }
                        }
                }


            }

    }


    override fun onResume() {
        super.onResume()
    }


}