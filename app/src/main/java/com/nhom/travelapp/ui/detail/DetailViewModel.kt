package com.nhom.travelapp.ui.details
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nhom.travelapp.data.model.Comment
import com.nhom.travelapp.data.repository.CommentRepository

class DetailViewModel : ViewModel() {
    private val repository = CommentRepository()

    var commentsList: LiveData<List<Comment>>? = null

    // Gọi Repository để lấy dữ liệu
    fun loadComments(placeId: String) {
        commentsList = repository.getComments(placeId)
    }

    // xử lý gửi bình luận
    fun sendComment(placeId: String, content: String, userId: String, onResult: (Boolean) -> Unit) {
        val comment = Comment(
            userId = userId,
            placeId = placeId,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        //đẩy lên Firebase
        repository.postComment(comment) { isSuccess ->
            onResult(isSuccess)
        }
    }
}