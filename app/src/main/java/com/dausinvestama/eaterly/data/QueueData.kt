package com.dausinvestama.eaterly.data

data class QueueData(
    val buyerName: Any?,
    val queues: List<Queue>
)

data class Queue(
    val orderid: Any?,
    val time: Any?,
    val menuName: Any?,
    val menuQuantity: Any?,
    val menuStatus: Any?,
    val menuTotalprice: Any?,
    val menuMeja: Any?,
    val menuUrl: Any?
)