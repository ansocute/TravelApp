package com.nhom.travelapp.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nhom.travelapp.R
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.databinding.ActivityEditProfileBinding
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            Glide.with(this).load(uri).into(binding.ivEditAvatar)

            val base64String = encodeImageToBase64(uri)
            if (base64String != null) {
                viewModel.updateUserAvatar(base64String)
            } else {
                Toast.makeText(this, "Không thể xử lý định dạng ảnh này", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val fullName = binding.etFullName.text?.toString()?.trim() ?: ""
            val phone = binding.etPhone.text?.toString()?.trim() ?: ""
            val aboutMe = binding.etAboutMe.text?.toString()?.trim() ?: ""

            if (fullName.isEmpty()) {
                binding.etFullName.error = "Tên không được để trống"
                return@setOnClickListener
            }
            viewModel.updateProfile(fullName, phone, aboutMe)
        }

        binding.btnDeactivate.setOnClickListener {
            showDeactivateConfirmDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.userData.observe(this) { user ->
            if (user.fullName.isEmpty()) {
                binding.etFullName.setText("")
                binding.etFullName.hint = "Chưa cập nhật"
            } else {
                binding.etFullName.setText(user.fullName)
            }

            binding.etEmail.setText(user.email)

            // Số điện thoại
            if (user.phone.isEmpty()) {
                binding.etPhone.setText("")
                binding.etPhone.hint = "Chưa cập nhật"
            } else {
                binding.etPhone.setText(user.phone)
            }

            // Giới thiệu bản thân
            if (user.aboutMe.isEmpty()) {
                binding.etAboutMe.setText("")
                binding.etAboutMe.hint = "Chưa cập nhật"
            } else {
                binding.etAboutMe.setText(user.aboutMe)
            }

            if (user.avatarUrl.isNotEmpty()) {
                if (user.avatarUrl.startsWith("http")) {
                    Glide.with(this)
                        .load(user.avatarUrl)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.ivEditAvatar)
                } else {
                    try {
                        val imageByteArray = Base64.decode(user.avatarUrl, Base64.DEFAULT)
                        Glide.with(this)
                            .load(imageByteArray)
                            .placeholder(R.drawable.ic_profile)
                            .into(binding.ivEditAvatar)
                    } catch (e: Exception) {
                        binding.ivEditAvatar.setImageResource(R.drawable.ic_profile)
                    }
                }
            }
        }

        viewModel.updateStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSave.text = "Đang xử lý..."
                    binding.btnSave.isEnabled = false
                    binding.btnDeactivate.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnSave.text = "Lưu"
                    binding.btnSave.isEnabled = true
                    binding.btnDeactivate.isEnabled = false
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()

                    val successMessage = resource.data ?: ""
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()

                    when {
                        successMessage.contains("xóa vĩnh viễn") -> {
                            val intent = Intent(this, com.nhom.travelapp.ui.auth.login.LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        successMessage.contains("Hồ sơ") -> {
                            finish()
                        }
                    }
                }
                is Resource.Error -> {
                    binding.btnSave.text = "Lưu"
                    binding.btnSave.isEnabled = true
                    binding.btnDeactivate.isEnabled = true
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun encodeImageToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val resizedBitmap = bitmap.scale(300, 300)

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)

            val byteArr = outputStream.toByteArray()
            Base64.encodeToString(byteArr, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun showDeactivateConfirmDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa tài khoản")
            .setMessage("Mọi dữ liệu của bạn sẽ bị xóa vĩnh viễn và không thể khôi phục. Bạn có chắc chắn muốn tiếp tục không?")
            .setPositiveButton("Xóa tài khoản") { _, _ ->
                viewModel.deactivateAccount()
            }
            .setNegativeButton("Hủy", null)
            .create()
            .apply {
                show()
                getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(android.graphics.Color.RED)
            }
    }
}