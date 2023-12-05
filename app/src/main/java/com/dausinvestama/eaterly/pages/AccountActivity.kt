package com.dausinvestama.eaterly.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dausinvestama.eaterly.R
import com.dausinvestama.eaterly.SignInActivity
import com.dausinvestama.eaterly.adapter.SettingsAdapter
import com.dausinvestama.eaterly.databinding.ActivityAccountBinding
import com.dausinvestama.eaterly.utils.Utils.setSettingList
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.apply {
            rvContent.setHasFixedSize(false)
            val accMngAdapter = SettingsAdapter(
                setSettingList(
                    resources.getStringArray(R.array.mng_acc_settings),
                    resources.obtainTypedArray(R.array.mng_acc_img),
                    resources.getStringArray(R.array.mng_acc_desc)
                )
            )

            accMngAdapter.setOnItemClickCallback(object : SettingsAdapter.OnItemClickCallback {
                override fun onItemClicked(position: Int) {
                    when(position){
                        0 -> {
                            auth.signOut()
                            startActivity(Intent(this@AccountActivity, SignInActivity::class.java))
                            finish()
                        }
                        1 -> {

                        }
                    }
                }

            })

            rvContent.adapter = accMngAdapter
            rvContent.layoutManager = LinearLayoutManager(this@AccountActivity)
            rvContent.isClickable = true

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}