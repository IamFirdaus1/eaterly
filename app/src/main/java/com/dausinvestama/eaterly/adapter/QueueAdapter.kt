package com.dausinvestama.eaterly.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.QueueData
import com.dausinvestama.eaterly.utils.CurrencyUtils.formatToRupiah
import com.dausinvestama.eaterly.utils.DateUtils.timestampStringToDate

class QueueAdapter(val queues: MutableList<QueueData>, private val context: Context) :
    RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    interface OnAcceptClickCallback {
        fun onAcceptClick(orderId: String)
    }

    interface OnDenyClickCallback {
        fun onDenyClick(orderId: String)
    }

    private lateinit var onAcceptClickCallback: OnAcceptClickCallback
    private lateinit var onDenyClickCallback: OnDenyClickCallback

    fun setOnAcceptClickCallback(onAcceptClickCallback: OnAcceptClickCallback) {
        this.onAcceptClickCallback = onAcceptClickCallback
    }

    fun setOnDenyClickCallback(onDenyClickCallback: OnDenyClickCallback){
        this.onDenyClickCallback = onDenyClickCallback
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTable = itemView.findViewById<TextView>(R.id.tv_table)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tv_price)
        private val tvTime = itemView.findViewById<TextView>(R.id.tv_time)
        private val rvMenu = itemView.findViewById<RecyclerView>(R.id.rv_menu)
        private val btnAcc = itemView.findViewById<Button>(R.id.btn_accept)
        private val btnDeny = itemView.findViewById<Button>(R.id.btn_deny)
        private val statusCoordinator = itemView.findViewById<CoordinatorLayout>(R.id.statusCoordinator)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tv_status)

        fun bind(queueData: QueueData) {
            tvTable.text = "Table " + queueData.queues.first().menuMeja
            tvPrice.text =
                formatToRupiah(queueData.queues.first().menuTotalprice.toString().toInt())
            tvTime.text = timestampStringToDate(queueData.time)

            rvMenu.layoutManager = LinearLayoutManager(itemView.context)
            rvMenu.adapter = MenuAdapter(queueData.queues, itemView.context)

            when (queueData.queues.first().menuStatus.toString()) {
                "0" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_preparing)
                    tvStatus.text = "Preparing"
                    btnAcc.text = "Finish"
                    btnDeny.text = "Cancel"
                }
                "1" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_onway)
                    tvStatus.text = "On Way"
                    btnDeny.visibility = View.GONE
                    btnAcc.text = "Confirm Payment"
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_green))
                }
                "2" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_preparing)
                    tvStatus.text = "Pending"
                }
                "3" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_denied)
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.dark_red))
                    tvStatus.text = "Cancelled"
                    btnAcc.visibility = View.GONE
                    btnDeny.visibility = View.GONE
                }
                "4" -> {
                    statusCoordinator.setBackgroundResource(R.drawable.background_onway)
                    tvStatus.text = "Finished"
                    btnAcc.visibility = View.GONE
                    btnDeny.visibility = View.GONE
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_green))
                }
                else -> {}
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.queue_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = queues.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(queues[position])

        val btnAcc = holder.itemView.findViewById<Button>(R.id.btn_accept)
        val btnDeny = holder.itemView.findViewById<Button>(R.id.btn_deny)

        btnAcc.setOnClickListener {
            onAcceptClickCallback.onAcceptClick(queues[position].queues.first().orderid.toString())
        }

        btnDeny.setOnClickListener {
            onDenyClickCallback.onDenyClick(queues[position].queues.first().orderid.toString())
        }

    }

    fun updateData(newData: List<QueueData>) {
        this.queues.clear()
        this.queues.addAll(newData)
        notifyDataSetChanged()
    }
}