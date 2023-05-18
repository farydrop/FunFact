package com.example.funfact.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funfact.R
import com.example.funfact.databinding.ActivityMainBinding
import com.example.funfact.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var factsAdapter: FactsAdapter
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.factsState.observe(this) {
            factsAdapter = FactsAdapter(it)
            binding.rvFacts.adapter = factsAdapter
        }

    }
}