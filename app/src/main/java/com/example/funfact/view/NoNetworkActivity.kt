package com.example.funfact.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funfact.databinding.ActivityNoNetworkBinding

class NoNetworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tryAgainButton.setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }
}