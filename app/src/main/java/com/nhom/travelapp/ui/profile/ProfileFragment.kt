package com.nhom.travelapp.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.nhom.travelapp.R
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.databinding.FragmentProfileBinding
import com.nhom.travelapp.databinding.LayoutDialogResetPasswordBinding
import com.nhom.travelapp.ui.auth.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvUserName.text = name
        }

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvUserEmail.text = email
        }

        viewModel.userPhone.observe(viewLifecycleOwner) { phone ->
            binding.tvUserPhone.text = phone
        }

        viewModel.userAboutMe.observe(viewLifecycleOwner) { aboutMe ->
            binding.tvUserAboutMe.text = aboutMe
        }

        viewModel.userAvatar.observe(viewLifecycleOwner) { avatarUrl ->
            if (!avatarUrl.isNullOrEmpty()) {
                if (avatarUrl.startsWith("http")) {
                    // 1. Nếu là tài khoản Google (link web)
                    Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.ivAvatar)
                } else {
                    // 2. Nếu là ảnh tự tải (chuỗi Base64)
                    try {
                        val imageByteArray = Base64.decode(avatarUrl, Base64.DEFAULT)
                        Glide.with(this)
                            .load(imageByteArray)
                            .placeholder(R.drawable.ic_profile)
                            .into(binding.ivAvatar)
                    } catch (e: Exception) {
                        binding.ivAvatar.setImageResource(R.drawable.ic_profile)
                    }
                }
            } else {
                // Nếu không có ảnh thì set ảnh mặc định
                binding.ivAvatar.setImageResource(R.drawable.ic_profile)
            }
        }

        viewModel.passwordResetStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Đang gửi yêu cầu...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.data, Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.menuLogout.setOnClickListener {
            performLogout()
        }

        binding.menuChangePassword.setOnClickListener {
            showResetPasswordDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    private fun showResetPasswordDialog() {
        val email = viewModel.userEmail.value ?: ""

        val dialogBinding = LayoutDialogResetPasswordBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.tvTargetEmail.text = email

        dialogBinding.btnConfirm.setOnClickListener {
            viewModel.sendPasswordResetEmail()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }


    private fun performLogout() {
        viewModel.logout()

        val intent = Intent(requireContext(), LoginActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}