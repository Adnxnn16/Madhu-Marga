package com.madhum.marga.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.madhum.marga.databinding.ActivityRegisterBinding
import com.madhum.marga.ui.main.MainActivity
import com.madhum.marga.util.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val mobile = binding.etMobile.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (password != confirmPassword) {
                Snackbar.make(binding.root, "Passwords do not match.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.register(email, password, name, mobile)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
            if (android.os.Build.VERSION.SDK_INT >= 34) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            } else {
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.authResult.observe(this) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    sessionManager.saveSession(
                        result.user.id,
                        result.user.email,
                        result.user.fullName
                    )
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                is AuthResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
