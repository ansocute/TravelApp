package com.nhom.travelapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhom.travelapp.data.local.TripActivity
import com.nhom.travelapp.databinding.ItemActivityBinding

class ActivityAdapter(
    private val list: List<TripActivity>,
    private val onDelete: (TripActivity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.txtTitle.text = item.title
        holder.binding.txtTime.text = item.time
        holder.binding.txtLocation.text = item.location

        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.imgActivity)

        holder.binding.btnDelete.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = list.size
}