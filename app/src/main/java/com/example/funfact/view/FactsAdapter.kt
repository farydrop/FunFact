package com.example.funfact.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.funfact.databinding.FactItemBinding
import com.example.funfact.model.FactsModel

class FactsAdapter(private val factList: ArrayList<FactsModel>): RecyclerView.Adapter<FactsAdapter.FactsListViewHolder>() {


    class FactsListViewHolder(private val binding: FactItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(facts: FactsModel) {
            //binding.tvFrequencyItem.text = frequency.days.toString()
            binding.ivPicture.setImageResource(facts.picture)
            binding.tvFactTitle.text = facts.title
            binding.tvFactText.text = facts.fact
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactsListViewHolder {
        val binding = FactItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FactsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FactsListViewHolder, position: Int) {
        //val frequency = frequencyList[position]
        val fact = factList[position]
        holder.bind(fact)
    }

    override fun getItemCount(): Int = factList.size
}