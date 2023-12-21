package com.dausinvestama.eaterly.fragment

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.adapter.MenuAdapter
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.SellerInsertMenu
import com.dausinvestama.eaterly.adapter.CanteenOrderAdapter
import com.dausinvestama.eaterly.adapter.CategoryAdapter
import com.dausinvestama.eaterly.adapter.MenuSellerAdapter
import com.dausinvestama.eaterly.data.CategoryList
import com.dausinvestama.eaterly.data.MenuData
import com.dausinvestama.eaterly.databinding.FragmentMenuBinding
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
            try {
                val menuList = db.collection("menus")
                    .whereEqualTo("canteen_id", sellerId)
                    .get()
                    .await()

                val menus = mutableListOf<MenuData>()
                Log.d(TAG, "MenuList testing 1: ${menus.size}")
                for (menuDocument in menuList) {
                    try {
                        val name = menuDocument.get("name")
                        val description = menuDocument.get("description")
                        val price = menuDocument.get("price")
                        val url = menuDocument.get("url")

                        val menu = MenuData(name, description, price, url)
                        menus.add(menu)
                    } catch (e: Exception) {
                        Log.d(TAG, "getData error: $e")
                    }
                }
                withContext(Dispatchers.Main) {
                    if (menus.isNotEmpty() && isAdded) {
                        menuAdapter = MenuSellerAdapter(menus, requireContext())
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