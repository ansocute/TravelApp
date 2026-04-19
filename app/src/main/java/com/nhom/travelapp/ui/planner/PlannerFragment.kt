package com.nhom.travelapp.ui.planner

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nhom.travelapp.R
import com.nhom.travelapp.data.local.AppDatabase
import com.nhom.travelapp.data.local.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlannerFragment : Fragment() {

    // Khai báo UI
    private lateinit var listView: ListView
    private lateinit var fab: FloatingActionButton

    // Database Room
    private lateinit var db: AppDatabase

    // Danh sách hiển thị
    private val list = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Ánh xạ view
        listView = view.findViewById(R.id.listView)
        fab = view.findViewById(R.id.fabAdd)

        // Khởi tạo database
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "trip_db"
        ).build()

        // Load dữ liệu khi mở màn hình
        loadData()

        // Nút thêm
        fab.setOnClickListener {
            showAddDialog()
        }
    }

    // Hiển thị dialog thêm chuyến đi
    private fun showAddDialog() {

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_trip, null)

        val edtTitle = dialogView.findViewById<EditText>(R.id.edtTitle)
        val edtLocation = dialogView.findViewById<EditText>(R.id.edtLocation)
        val edtDay = dialogView.findViewById<EditText>(R.id.edtDay)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Thêm chuyến đi")
            .setView(dialogView)
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        // Xử lý nút Lưu
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val title = edtTitle.text.toString().trim()
            val location = edtLocation.text.toString().trim()
            val day = edtDay.text.toString().toIntOrNull()

            // Validate dữ liệu
            if (title.isEmpty()) {
                edtTitle.error = "Nhập tiêu đề"
                return@setOnClickListener
            }

            if (location.isEmpty()) {
                edtLocation.error = "Nhập địa điểm"
                return@setOnClickListener
            }

            if (day == null || day <= 0) {
                edtDay.error = "Ngày không hợp lệ"
                return@setOnClickListener
            }

            // Lưu vào database (chạy background)
            lifecycleScope.launch(Dispatchers.IO) {
                db.tripDao().insert(
                    Trip(title = title, location = location, day = day)
                )

                // Quay lại main thread để cập nhật UI
                withContext(Dispatchers.Main) {
                    loadData()
                    dialog.dismiss()
                }
            }
        }
    }

    // Load dữ liệu từ database
    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {

            val trips = db.tripDao().getAll()

            list.clear()

            for (t in trips) {
                list.add("Day ${t.day}: ${t.title} - ${t.location}")
            }

            // Cập nhật UI
            withContext(Dispatchers.Main) {
                listView.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    list
                )
            }
        }
    }
}