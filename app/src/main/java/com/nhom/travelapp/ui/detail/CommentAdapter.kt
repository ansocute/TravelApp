package com.nhom.travelapp.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nhom.travelapp.R
import com.nhom.travelapp.data.model.Comment
import com.nhom.travelapp.data.model.User // Nhớ import class User

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var commentList = listOf<Comment>()

    fun submitList(list: List<Comment>) {
        commentList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]

        // Hiển thị nội dung bình luận
        holder.tvContent.text = comment.content

        holder.tvUserName.text = "Đang tải..."

        // truy vấn firebase lấy thông tin user
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(comment.userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Ép kiểu dữ liệu tải về thành object User
                val user = snapshot.getValue(User::class.java)

                if (user != null) {
                    // 1. Hiển thị Tên
                    holder.tvUserName.text = user.fullName

                    // 2. Load Avatar
                    if (user.avatarUrl.isNotEmpty()) {
                        Glide.with(holder.itemView.context)
                            .load(user.avatarUrl)
                            .circleCrop()
                            .into(holder.imgAvatar)
                    }
                } else {
                    holder.tvUserName.text = "Người dùng ẩn danh"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                holder.tvUserName.text = "Lỗi tải tên"
            }
        })
    }

    override fun getItemCount(): Int = commentList.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
    }
}
