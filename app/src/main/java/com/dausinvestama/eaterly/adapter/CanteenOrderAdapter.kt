package com.dausinvestama.eaterly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.OrderListData

class CanteenOrderAdapter(val canteens: MutableList<OrderListData>): RecyclerView.Adapter<CanteenOrderAdapter.CanteenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanteenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orderlist, parent, false)
        return CanteenViewHolder(view)
    }

    override fun onBindViewHolder(holder: CanteenViewHolder, position: Int) {
        holder.bind(canteens[position])
    }

    override fun getItemCount(): Int = canteens.size
    class CanteenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val canteenNameTxVw: TextView = itemView.findViewById(R.id.nama_kantin)
        private val menuRecycler: RecyclerView = itemView.findViewById(R.id.recyclerhistory)
        private val menuMejaTxVw: TextView = itemView.findViewById(R.id.nomejaTxtview)
        private val idorderTxt: TextView = itemView.findViewById(R.id.idorderTxtview)
        private val statusCoordinator: CoordinatorLayout = itemView.findViewById(R.id.statusCoordinator)
        private val statusTxtVw: TextView = itemView.findViewById(R.id.statusorder)

        fun bind(canteen: OrderListData) {
            canteenNameTxVw.text = canteen.canteenName.toString()
            menuRecycler.layoutManager = LinearLayoutManager(itemView.context)
            menuRecycler.adapter = MenuAdapter(canteen.menus, itemView.context)
            idorderTxt.text = canteen.menus.first().orderid.toString()
            menuMejaTxVw.text = canteen.menus.first().menuMeja.toString()
            when (canteen.menus.first().menuStatus.toString()) {
                "0" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_preparing)
                    statusTxtVw.text = "Preparing"
                }
                "1" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_onway)
                    statusTxtVw.text = "On Way"
                    statusTxtVw.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_green))
                }
                "2" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_preparing)
                    statusTxtVw.text = "Pending"
                }
                "3" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_denied)
                    statusTxtVw.text = "Cancelled"
                }
                "4" -> {
                    itemView.visibility = View.GONE
                }
                else -> {}
            }
        }
    }
}