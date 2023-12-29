package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.SellerInsertMenu
import com.dausinvestama.eaterly.adapter.MenuAdapter
import com.dausinvestama.eaterly.adapter.MenuSellerAdapter
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.data.MenuData
import com.dausinvestama.eaterly.databinding.FragmentMenuBinding
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity.Companion.DESC
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity.Companion.NAME
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity.Companion.PRICE
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity.Companion.TIME
import com.dausinvestama.eaterly.pages.DetailMenuSellerActivity.Companion.URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MenuFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentMenuBinding
    private lateinit var menuAdapter: MenuSellerAdapter
    var listMenu: ArrayList<CategoryList> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)

        binding.apply {
            val menuAdapter = MenuAdapter(mutableListOf(), requireContext())

            rvMenu.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvMenu.adapter = menuAdapter
            rvMenu.setHasFixedSize(true)

            getData(pbLoading, rvMenu, llEmpty)

            fabAdd.setOnClickListener{
                Intent(requireContext(), SellerInsertMenu::class.java).also {
                    startActivity(it)
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getData(progressBar: ProgressBar, recyclerView: RecyclerView, ifGone: LinearLayout) =
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) { progressBar.visibility = View.VISIBLE }
            val sellerId = FirebaseAuth.getInstance().currentUser?.uid

            val canteens = db.collection("canteens")
                .whereEqualTo("seller", sellerId)
                .limit(1)
                .get()
                .await()

            val canteenId = canteens.documents.firstOrNull()?.id?.toInt()

            try {
                val menuList = db.collection("menus")
                    .whereEqualTo("canteen_id", canteenId)
                    .get()
                    .await()

                val menus = mutableListOf<MenuData>()
                Log.d(TAG, "MenuList testing 1: ${menus.size}")
                for (menuDocument in menuList) {
                    try {
                        val name = menuDocument.get("name")
                        val description = menuDocument.get("description")
                        val time = menuDocument.get("timeEstimation")
                        val price = menuDocument.get("price")
                        val url = menuDocument.get("url")

                        val menu = MenuData(
                            name = name,
                            description = description,
                            price = price,
                            time = time,
                            url = url
                        )
                        menus.add(menu)
                    } catch (e: Exception) {
                        Log.d(TAG, "getData error: $e")
                    }
                }
                withContext(Dispatchers.Main) {
                    if (menus.isNotEmpty() && isAdded) {
                        menuAdapter = MenuSellerAdapter(menus, requireContext())
                        menuAdapter.setOnItemClickCallback(object : MenuSellerAdapter.OnItemClickCallback {
                            override fun onItemClick(position: Int) {
                                Intent(requireContext(), DetailMenuSellerActivity::class.java).also {
                                    it.putExtra(NAME, menus[position].name.toString())
                                    it.putExtra(URL, menus[position].url.toString())
                                    it.putExtra(TIME, menus[position].time.toString())
                                    it.putExtra(DESC, menus[position].description.toString())
                                    it.putExtra(PRICE, menus[position].price.toString())

                                    startActivity(it)
                                }
                            }

                        })
                        recyclerView.adapter = menuAdapter
                        ifGone.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.GONE
                        ifGone.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getData error: $e")
            }
        }

}