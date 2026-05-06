package com.nhom.travelapp.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import kotlinx.coroutines.tasks.await

class PlaceRepository {

    private val database = FirebaseDatabase.getInstance().getReference("places")

    private val dummyPlaces = listOf(
        // --- SIGHTS ---
        Place("p1", "Dinh Độc Lập", "135 Nam Kỳ Khởi Nghĩa, Q.1", 10.777, 106.695, "Sights", "Sights", 4.5f,
            "img_dinh_doc_lap",
            "Historical independence palace."),
        Place("p2", "Bưu điện Thành phố", "02 Công xã Paris, Q.1", 10.779, 106.700, "Sights", "Sights", 4.7f,
            "img_buu_dien_thanh_pho",
            "Classic French architecture."),
        Place("p3", "Nhà thờ Đức Bà", "01 Công xã Paris, Q.1", 10.779, 106.699, "Sights", "Sights", 4.6f,
            "img_nha_tho_duc_ba",
            "Famous cathedral in Saigon."),
        Place("p4", "Landmark 81", "Vinhomes Central Park, Bình Thạnh", 10.794, 106.722, "Sights", "Sights", 4.8f,
            "img_landmark_81",
            "The tallest building in Vietnam."),
        Place("p5", "Bảo tàng Mỹ thuật", "97 Phó Đức Chính, Q.1", 10.770, 106.698, "Sights", "Sights", 4.4f,
            "img_bao_tang_my_thuat",
            "Fine arts museum with French style."),

        // --- FOOD ---
        Place("f1", "Chợ Bến Thành", "Lê Lợi, Q.1", 10.771, 106.698, "Food", "Food", 4.2f,
            "img_cho_ben_thanh",
            "Famous market with local street food."),
        Place("f2", "Cơm Tấm Ba Ghiền", "84 Đặng Văn Ngữ, Phú Nhuận", 10.799, 106.674, "Food", "Food", 4.5f,
            "img_com_tam_ba_ghien",
            "Famous broken rice in Saigon."),
        Place("f3", "Bánh Mì Huỳnh Hoa", "26 Lê Thị Riêng, Q.1", 10.772, 106.693, "Food", "Food", 4.7f,
            "img_banh_mi_huynh_hoa",
            "The most famous banh mi shop."),
        Place("f4", "Phở Hòa Pasteur", "260C Pasteur, Q.3", 10.787, 106.689, "Food", "Food", 4.4f,
            "img_pho_hoa_pasteur",
            "Traditional Pho with long history."),
        Place("f5", "Ốc Đào", "212B Nguyễn Trãi, Q.1", 10.768, 106.689, "Food", "Food", 4.3f,
            "img_oc_dao",
            "Best place for snail dishes."),

        // --- HOTELS ---
        Place("h1", "The Reverie Saigon", "22-36 Nguyễn Huệ, Q.1", 10.774, 106.703, "Hotels", "Hotels", 5.0f,
            "img_the_reverie_saigon",
            "Luxury 6-star hotel."),
        Place("h2", "Hotel Continental", "132 Đồng Khởi, Q.1", 10.776, 106.702, "Hotels", "Hotels", 4.6f,
            "img_hotel_continental",
            "The oldest hotel in Vietnam."),
        Place("h3", "Park Hyatt Saigon", "02 Lam Sơn, Q.1", 10.776, 106.703, "Hotels", "Hotels", 4.9f,
            "img_park_hyatt_saigon",
            "Elegant French style hotel."),
        Place("h4", "Caravelle Saigon", "19-23 Lam Sơn, Q.1", 10.775, 106.703, "Hotels", "Hotels", 4.7f,
            "img_caravelle_saigon",
            "Historical and famous hotel."),
        Place("h5", "Rex Hotel", "141 Nguyễn Huệ, Q.1", 10.775, 106.701, "Hotels", "Hotels", 4.5f,
            "img_rex_hotel",
            "Famous rooftop bar hotel.")
    )

    fun getDefaultPlaces(): List<Place> = dummyPlaces

    suspend fun pushDummyData() {
        try {
            database.removeValue().await() // Xóa sạch dữ liệu cũ lỗi link để nạp 15 cái mới
            dummyPlaces.forEach { place ->
                database.child(place.id).setValue(place).await()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
    suspend fun getAllPlaces(): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
            if (places.isEmpty()) Resource.Success(dummyPlaces) else Resource.Success(places)
        } catch (e: Exception) { Resource.Error(e.message ?: "Lỗi kết nối Realtime Database") }
    }

    suspend fun searchPlaces(query: String): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
                .filter { it.name.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true) }
            Resource.Success(places)
        } catch (e: Exception) { Resource.Error("Search Error") }
    }

    suspend fun getPlacesByCategory(category: String): Resource<List<Place>> {
        return try {
            val snapshot = database.get().await()
            val places = snapshot.children.mapNotNull { it.getValue(Place::class.java) }
                .filter { if (category == "All") true else it.category.equals(category, ignoreCase = true) }
            Resource.Success(places)
        } catch (e: Exception) { Resource.Error("Filter Error") }
    }
}