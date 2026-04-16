package com.nhom.travelapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import kotlinx.coroutines.tasks.await

class PlaceRepository {

    // Khởi tạo Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val placesCollection = firestore.collection("places")

    /**
     * Lấy toàn bộ danh sách địa điểm từ Firebase
     */
    suspend fun getAllPlaces(): Resource<List<Place>> {
        return try {
            // Lấy dữ liệu và đợi (await) kết quả trả về
            val snapshot = placesCollection.get().await()

            // Chuyển đổi các document thành list đối tượng Place
            val places = snapshot.toObjects(Place::class.java)

            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Không thể kết nối với máy chủ Firebase")
        }
    }

    /**
     * Tìm kiếm địa điểm
     * Lưu ý: Firestore không hỗ trợ tìm kiếm chuỗi linh hoạt (like query).
     * Cách đơn giản nhất là lọc theo tiền tố (prefix) hoặc lấy hết về lọc ở Client.
     */
    suspend fun searchPlaces(query: String): Resource<List<Place>> {
        return try {
            // Cách lọc theo tiền tố: Tên bắt đầu bằng 'query'
            val snapshot = placesCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get().await()

            val places = snapshot.toObjects(Place::class.java)
            Resource.Success(places)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi khi tìm kiếm dữ liệu")
        }
    }
}