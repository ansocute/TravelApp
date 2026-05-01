package com.nhom.travelapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nhom.travelapp.data.local.Trip
import com.nhom.travelapp.databinding.ItemTripBinding
import com.nhom.travelapp.ui.planner.DayDetailActivity
import com.bumptech.glide.Glide

class TripAdapter(
    private var tripList: List<Trip>,
    private val onDelete: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    inner class TripViewHolder(
        val binding: ItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripViewHolder {

        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TripViewHolder,
        position: Int
    ) {

        val trip = tripList[position]

        // LONG CLICK để xoá
        holder.itemView.setOnLongClickListener {

            android.app.AlertDialog.Builder(holder.itemView.context)
                .setTitle("Xóa ngày")
                .setMessage("Bạn có chắc muốn xóa Day ${trip.day}?")
                .setPositiveButton("Xóa") { _, _ ->
                    onDelete(trip)
                }
                .setNegativeButton("Hủy", null)
                .show()

            true
        }

        // HIỂN THỊ
        holder.binding.txtDay.text = "Day ${trip.day}"
        holder.binding.txtTitle.text = trip.title
        holder.binding.txtLocation.text = trip.location

        // LOAD ẢNH
        Glide.with(holder.itemView.context)
            .load(trip.image)
            .centerCrop()
            .into(holder.binding.imgTrip)

        // CLICK ITEM
        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            try {
                val intent = Intent(context, DayDetailActivity::class.java)

                intent.putExtra("day", trip.day)
                intent.putExtra("title", trip.title)
                intent.putExtra("tripId", trip.id)
                context.startActivity(intent)

            } catch (e: Exception) {

                // Nếu lỗi → hiện Toast (để bạn debug)
                Toast.makeText(
                    context,
                    "Lỗi mở màn hình!",
                    Toast.LENGTH_SHORT
                ).show()

                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }

    fun updateData(newList: List<Trip>) {
        tripList = newList
        notifyDataSetChanged()
    }
}