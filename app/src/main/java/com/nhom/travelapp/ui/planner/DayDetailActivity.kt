package com.nhom.travelapp.ui.planner

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.nhom.travelapp.R
import com.nhom.travelapp.data.local.AppDatabase
import com.nhom.travelapp.data.local.TripActivity
import com.nhom.travelapp.databinding.ActivityDayDetailBinding
import com.nhom.travelapp.ui.adapter.ActivityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDayDetailBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: ActivityAdapter

    private val list = mutableListOf<TripActivity>()
    private var tripId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getIntExtra("tripId", -1)
        val dayNumber = intent.getIntExtra("day", 1)
        binding.txtTitle.text = "Day $dayNumber"

        adapter = ActivityAdapter(list) { item ->
            lifecycleScope.launch(Dispatchers.IO) {
                db.tripActivityDao().delete(item) // Đã sửa tên hàm
                withContext(Dispatchers.Main) { loadData() }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "trip_db")
            .fallbackToDestructiveMigration() // Đã bỏ tham số lỗi
            .build()

        loadData()

        binding.fabAddActivity.setOnClickListener { showAddDialog() }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = db.tripActivityDao().getByTripId(tripId)
            withContext(Dispatchers.Main) {
                list.clear()
                list.addAll(data)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showAddDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_activity, null)
        val edtTitle = view.findViewById<EditText>(R.id.edtTitle)
        val edtTime = view.findViewById<EditText>(R.id.edtTime)
        val edtLocation = view.findViewById<EditText>(R.id.edtLocation)
        val edtImage = view.findViewById<EditText>(R.id.edtImage)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Thêm địa điểm")
            .setView(view)
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = edtTitle.text.toString()
            val time = edtTime.text.toString()
            val location = edtLocation.text.toString()
            val imageInput = edtImage.text.toString()

            if (title.isEmpty()) {
                edtTitle.error = "Nhập tên"
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val newActivity = TripActivity(
                    tripId = tripId,
                    title = title,
                    time = time,
                    location = location,
                    image = imageInput.ifEmpty { "https://picsum.photos/300" }
                )
                db.tripActivityDao().insert(newActivity)
                withContext(Dispatchers.Main) {
                    loadData()
                    dialog.dismiss()
                }
            }
        }
    }
}