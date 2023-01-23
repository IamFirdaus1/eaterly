package com.dausinvestama.eaterly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.data.CategoryList

class DetailedList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_list)

        val categoryList =   intent.getParcelableExtra<CategoryList>("categorylist")

        if (categoryList != null) {
            Log.d(categoryList.Categorylist, "onCreate: " + categoryList.Categorylist)
        }
    }
}