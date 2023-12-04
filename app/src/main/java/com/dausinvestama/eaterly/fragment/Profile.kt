package com.dausinvestama.eaterly.fragment

import android.content.res.TypedArray
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.adapter.SettingsAdapter
import com.dausinvestama.eaterly.data.Settings
import com.dausinvestama.eaterly.databinding.FragmentProfileBinding
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

            if (user != null) {
                tvUsername.text = user.displayName
                tvEmail.text = user.email
                Glide.with(this@Profile)
                    .load(user.photoUrl)
                    .circleCrop()
                    .into(imgPp)
            }

            rvAccSettings.setHasFixedSize(true)
            rvAccSettings.adapter = SettingsAdapter(
                setSettingList(
                    resources.getStringArray(R.array.account_settings),
                    resources.obtainTypedArray(R.array.acc_img_settings),
                    resources.getStringArray(R.array.acc_settings_desc)
                )
            )
            rvAccSettings.layoutManager = LinearLayoutManager(activity)
            rvAccSettings.overScrollMode = View.OVER_SCROLL_NEVER

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

        }

        return binding.root
    }

    private fun setSettingList(
        name: Array<String>,
        photo: TypedArray,
        desc: Array<String>
    ): ArrayList<Settings> {
        val listSettings = ArrayList<Settings>()

        for (i in name.indices) {
            val setting = Settings(name[i], photo.getResourceId(i, -1), desc[i])
            listSettings.add(setting)
        }

        return listSettings
    }


}