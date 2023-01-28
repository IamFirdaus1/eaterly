package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.utils.SharedPreferences


class NamePopUpFragment(context: Context?) : DialogFragment() {

    lateinit var editnamatext: EditText
    lateinit var butonsetnama: AppCompatButton

    var pre = SharedPreferences(context)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editnamatext = view.findViewById(R.id.namapanggilan)
        butonsetnama = view.findViewById(R.id.submitnama)

        butonsetnama.setOnClickListener {
            pre.first_name = editnamatext.text.toString()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroyed: ")
    }


}