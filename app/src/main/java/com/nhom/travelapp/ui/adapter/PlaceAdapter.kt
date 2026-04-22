package com.nhom.travelapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.databinding.ActivityItemPlaceBinding
import com.nhom.travelapp.ui.details.DetailActivity
import java.util.Locale // Thêm thư viện này để fix lỗi Locale

class PlaceAdapter(private var places: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(val binding: ActivityItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ActivityItemPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        val context = holder.itemView.context

        holder.binding.apply {
            tvPlaceName.text = place.name
            tvLocation.text = place.location

            // FIX LỖI LOCALE: Sử dụng Locale.US để đảm bảo dấu thập phân là dấu chấm (.)
            tvRating.text = String.format(Locale.US, "%.1f", place.rating)

            Glide.with(ivPlace.context)
                .load(place.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(ivPlace)

            // Mở trang DetailActivity
            root.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("EXTRA_PLACE", place)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = places.size

    // Cập nhật danh sách dùng DiffUtil để tối ưu hiệu năng
    fun updateData(newPlaces: List<Place>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = places.size
            override fun getNewListSize() = newPlaces.size

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                // So sánh ID của hai địa điểm
                return places[oldPos].id == newPlaces[newPos].id
            }

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                // So sánh toàn bộ nội dung object
                return places[oldPos] == newPlaces[newPos]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.places = newPlaces
        diffResult.dispatchUpdatesTo(this)
    }
}