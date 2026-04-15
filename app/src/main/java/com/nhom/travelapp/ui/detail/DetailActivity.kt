package com.nhom.travelapp.ui.details

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.nhom.travelapp.R
import com.nhom.travelapp.data.model.Comment
import com.nhom.travelapp.data.model.Place

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    // Khởi tạo ViewModel
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var commentAdapter: CommentAdapter
    private var currentPlace: Place? = null

    // Hiển thị thêm bình luận
    private var isExpanded = false
    private var fullCommentList = listOf<Comment>()
    private val INITIAL_COMMENT_COUNT = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
        // 1. Nhận dữ liệu địa điểm từ Intent
        currentPlace = intent.getSerializableExtra("EXTRA_PLACE") as? Place

        if (currentPlace != null) {
            setupUI(currentPlace!!)
            setupRecyclerView()
            // Kích hoạt ViewModel tải bình luận
            viewModel.loadComments(currentPlace!!.id)
            viewModel.commentsList?.observe(this) { comments ->
                fullCommentList = comments.reversed()
                updateCommentUI()
            }
            // sự kiện cho nút thêm bình luận
            findViewById<TextView>(R.id.tvSeeMoreComments).setOnClickListener {
                isExpanded =
                    !isExpanded // đảo ngược trạng thái
                updateCommentUI()
            }
        } else {
            Toast.makeText(this, "Lỗi tải địa điểm", Toast.LENGTH_SHORT).show()
            finish()
        }
        // sự kiện gửi bình luận
        findViewById<Button>(R.id.btnSendComment).setOnClickListener {
            handleSendComment()
        }
    }

    // 4. THÊM HÀM NÀY ĐỂ XỬ LÝ VIỆC HIỂN THỊ BAO NHIÊU BÌNH LUẬN
    private fun updateCommentUI() {
        val tvSeeMore = findViewById<TextView>(R.id.tvSeeMoreComments)

        // Nếu tổng số bình luận ít hơn hoặc bằng 3 -> Ẩn nút "Xem thêm" và hiện hết
        if (fullCommentList.size <= INITIAL_COMMENT_COUNT) {
            commentAdapter.submitList(fullCommentList)
            tvSeeMore.visibility = View.GONE
        } else {
            // Nếu có nhiều bình luận
            tvSeeMore.visibility = View.VISIBLE

            if (isExpanded) {
                // Đang ở chế độ "Mở rộng" -> Hiện hết toàn bộ
                commentAdapter.submitList(fullCommentList)
                tvSeeMore.text = "Thu gọn"
            } else {
                // cắt lấy 3 comment cái đầu tiên
                val shortList = fullCommentList.take(INITIAL_COMMENT_COUNT)
                commentAdapter.submitList(shortList)
                tvSeeMore.text = "Xem thêm bình luận"
            }
        }
    }

    private fun setupUI(place: Place) {
        findViewById<TextView>(R.id.tvPlaceName).text = place.name
        findViewById<TextView>(R.id.tvAddress).text = place.address
        findViewById<TextView>(R.id.tvDescription).text = place.description

        findViewById<TextView>(R.id.tvCategory).text = place.category
        findViewById<RatingBar>(R.id.ratingBar).rating = place.rating

        // load ảnh vào ImageView
        val imgPlace = findViewById<ImageView>(R.id.imgPlace)
        Glide.with(this)
            .load(place.imageUrl)
            .into(imgPlace)
    }

    private fun setupRecyclerView() {
        val rvComments = findViewById<RecyclerView>(R.id.rvComments)
        commentAdapter = CommentAdapter()
        rvComments.layoutManager = LinearLayoutManager(this)
        rvComments.adapter = commentAdapter
    }

    private fun handleSendComment() {
        val edtComment = findViewById<EditText>(R.id.edtComment)
        val content = edtComment.text.toString().trim()

        if (content.isEmpty()) return

        // Lấy ID người dùng của User
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "User123"

        // Gọi ViewModel
        viewModel.sendComment(currentPlace!!.id, content, userId) { isSuccess ->
            if (isSuccess) {
                edtComment.text.clear()
            } else {
                Toast.makeText(this, "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }
}