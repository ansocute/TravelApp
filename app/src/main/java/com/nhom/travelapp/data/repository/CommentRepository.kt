package com.nhom.travelapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nhom.travelapp.data.model.Comment

class CommentRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // lấy danh sách bình luận
    fun getComments(placeId: String): LiveData<List<Comment>> {
        val commentsLiveData = MutableLiveData<List<Comment>>()

        database.child("Comments").orderByChild("placeId").equalTo(placeId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Comment>()
                    for (data in snapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        if (comment != null) list.add(comment)
                    }
                    // Cập nhật dữ liệu vào LiveData
                    commentsLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi
                }
            })
        return commentsLiveData
    }

    // Đẩy bình luận mới lên Firebase
    fun postComment(comment: Comment, onComplete: (Boolean) -> Unit) {
        val newCommentRef = database.child("Comments").push()
        newCommentRef.setValue(comment)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}