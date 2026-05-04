package com.nhom.travelapp.ui.planner

import com.nhom.travelapp.ui.adapter.TripAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nhom.travelapp.R
import com.nhom.travelapp.data.local.AppDatabase
import com.nhom.travelapp.data.local.Trip

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlannerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var db: AppDatabase
    private lateinit var adapter: TripAdapter

    private val tripList = mutableListOf<Trip>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recyclerView)
        fab = view.findViewById(R.id.fabAdd)

        adapter = TripAdapter(tripList) { trip ->

            lifecycleScope.launch(Dispatchers.IO) {
                db.tripDao().delete(trip)

                withContext(Dispatchers.Main) {
                    loadData()
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "trip_db"
        )   .fallbackToDestructiveMigration()
            .build()

        loadData()

        fab.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {

        val dialogView = LayoutInflater
            .from(requireContext())
            .inflate(R.layout.dialog_add_trip, null)

        val edtTitle = dialogView.findViewById<EditText>(R.id.edtTitle)
        val edtLocation = dialogView.findViewById<EditText>(R.id.edtLocation)
        val edtDay = dialogView.findViewById<EditText>(R.id.edtDay)
        val edtImage = dialogView.findViewById<EditText>(R.id.edtImage)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm chuyến đi")
            .setView(dialogView)
            .setPositiveButton("Lưu", null)
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val title = edtTitle.text.toString().trim()
            val location = edtLocation.text.toString().trim()
            val day = edtDay.text.toString().toIntOrNull()
            val image = edtImage.text.toString().trim()

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

            lifecycleScope.launch(Dispatchers.IO) {

                db.tripDao().insert(
                    Trip(
                        title = title,
                        location = location,
                        day = day,
                        image = if (image.isEmpty())
                            "https://picsum.photos/400"
                        else image
                    )
                )

                withContext(Dispatchers.Main) {
                    loadData()
                    Toast.makeText(requireContext(), "Đã thêm!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun loadData() {

        lifecycleScope.launch(Dispatchers.IO) {

            val trips = db.tripDao().getAll()

            withContext(Dispatchers.Main) {

                tripList.clear()
                tripList.addAll(trips)

                adapter.notifyDataSetChanged()
            }
        }
    }
}