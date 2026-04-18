package com.nhom.travelapp.ui.auth.login

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nhom.travelapp.R
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nhom.travelapp.MainActivity
import com.nhom.travelapp.core.utils.Resource
import com.nhom.travelapp.data.model.Place
import com.nhom.travelapp.data.session.SessionManager
import com.nhom.travelapp.databinding.ActivityLoginBinding
import com.nhom.travelapp.ui.auth.forgotpassword.ForgotPasswordActivity
import com.nhom.travelapp.ui.auth.register.RegisterActivity
import com.nhom.travelapp.ui.details.DetailActivity
import com.nhom.travelapp.ui.discovery.DiscoveryFragment
import com.nhom.travelapp.ui.map.MapsFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        applyBackgroundBlur()
        loadRememberedLogin()
        setupViews()
        observeViewModel()
        setupGoogleSignIn()
    }

    private fun applyBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.ivBackground.setRenderEffect(
                RenderEffect.createBlurEffect(
                    20f,
                    20f,
                    Shader.TileMode.CLAMP
                )
            )
        }
    }

    private fun loadRememberedLogin() {
        val isRememberMe = sessionManager.isRememberMeEnabled()
        val savedEmail = sessionManager.getSavedEmail()

        binding.cbRememberMe.isChecked = isRememberMe

        if (isRememberMe && savedEmail.isNotEmpty()) {
            binding.etEmail.setText(savedEmail)
            binding.etPassword.requestFocus()
        }
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Cập nhật lại sự kiện cho nút Google
        binding.btnGoogleLater.setOnClickListener {
            setLoading(true)
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is Resource.Idle -> setLoading(false)

                is Resource.Loading -> setLoading(true)

                is Resource.Success -> {
                    setLoading(false)
                    handleRememberMe()
                    Toast.makeText(
                        this,
                        state.message ?: "Đăng nhập thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    goToMain()
                }

                is Resource.Error -> {
                    setLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
            }
        }
    }

    private fun handleRememberMe() {
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val isRememberMe = binding.cbRememberMe.isChecked

        if (isRememberMe) {
            sessionManager.saveRememberMe(true)
            sessionManager.saveEmail(email)
        } else {
            sessionManager.clearRememberedLogin()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.cbRememberMe.isEnabled = !isLoading
        binding.tvForgotPassword.isEnabled = !isLoading
        binding.tvGoToRegister.isEnabled = !isLoading
        binding.btnGoogleLater.isEnabled = !isLoading
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)

        // Cờ này giúp xóa sạch các Activity cũ (như Login) khỏi hàng chờ
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    // Bộ lắng nghe kết quả trả về từ màn hình chọn tài khoản Google
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    viewModel.loginWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                setLoading(false)
                Toast.makeText(this, "Lỗi kết nối Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            setLoading(false)
        }
    }

}