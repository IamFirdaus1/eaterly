package com.dausinvestama.eaterly.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.SettingsAdapter
import com.dausinvestama.eaterly.databinding.FragmentProfileBinding
import com.dausinvestama.eaterly.pages.AccountActivity
import com.dausinvestama.eaterly.pages.EditProfileActivity
import com.dausinvestama.eaterly.pages.HistoryActivity
import com.dausinvestama.eaterly.utils.Utils.setSettingList
import com.google.firebase.auth.FirebaseAuth

class Profile(private val firebaseAuth: FirebaseAuth) : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        val user = firebaseAuth.currentUser

        binding.apply {

            tvUsername.text = user?.displayName
            tvEmail.text = user?.email
            if (user?.photoUrl != null){
                Glide.with(requireActivity())
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(imgPp)
            } else {
                imgPp.setImageResource(R.drawable.user_icon)
            }


            rvAccSettings.setHasFixedSize(true)
            val accAdapter = SettingsAdapter(
                setSettingList(
                    resources.getStringArray(R.array.account_settings),
                    resources.obtainTypedArray(R.array.acc_img_settings),
                    resources.getStringArray(R.array.acc_settings_desc)
                )
            )

            accAdapter.setOnItemClickCallback(object : SettingsAdapter.OnItemClickCallback {
                override fun onItemClicked(position: Int) {
                    when (position) {
                        0 -> startActivity(Intent(context, HistoryActivity::class.java))
                        1 -> {}
                        2 -> {}
                        3 -> {}
                        4 -> {}
                        5 -> {}
                        6 -> startActivity(Intent(context, AccountActivity::class.java))
                    }
                }
            })

            rvAccSettings.adapter = accAdapter

            rvAccSettings.layoutManager = LinearLayoutManager(activity)
            rvAccSettings.overScrollMode = View.OVER_SCROLL_NEVER
            rvAccSettings.isClickable = true

            rvOtherSettings.setHasFixedSize(true)
            rvOtherSettings.adapter = SettingsAdapter(
                setSettingList(
                    resources.getStringArray(R.array.other_settings),
                    resources.obtainTypedArray(R.array.oth_img_settings),
                    resources.getStringArray(R.array.oth_settings_desc)
                )
            )
            rvOtherSettings.layoutManager = LinearLayoutManager(activity)
            rvOtherSettings.overScrollMode = View.OVER_SCROLL_NEVER
            rvOtherSettings.isClickable = true

            btnEdit.setOnClickListener {
                Intent(requireContext(), EditProfileActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        return binding.root
    }


}