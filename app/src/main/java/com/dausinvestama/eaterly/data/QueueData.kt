package com.dausinvestama.eaterly.data

import com.google.firebase.Timestamp
import java.util.Date

data class QueueData(
    val time: String,
    val queues: MutableList<Menu>
)
