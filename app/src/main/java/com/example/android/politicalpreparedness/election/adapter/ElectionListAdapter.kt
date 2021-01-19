package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ItemElectionBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_election,parent,false)
        return ElectionViewHolder(binding)
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.binding.llMain.setOnClickListener {
            clickListener.onClick(item)
        }

    }



    //TODO: Add companion object to inflate ViewHolder (from)
}

//TODO: Create ElectionViewHolder

class ElectionViewHolder(val binding : ItemElectionBinding ) : RecyclerView.ViewHolder(binding.root){
    fun bind(item : Election){
        binding.item = item
        binding.executePendingBindings()
    }
}

//TODO: Create ElectionDiffCallback

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>(){
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
       return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}

//TODO: Create ElectionListener
public class ElectionListener(val onClick : (Election) -> Unit){
    private fun onItemClick(item:Election) = onClick(item)
}

