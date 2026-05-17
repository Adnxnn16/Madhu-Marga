package com.madhum.marga.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.madhum.marga.databinding.ActivityLoginBinding
import com.madhum.marga.ui.main.MainActivity
import com.madhum.marga.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            if (android.os.Build.VERSION.SDK_INT >= 34) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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
                    binding.btnLogin.isEnabled = false
                }
                is AuthResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    sessionManager.saveSession(
                        result.user.id,
                        result.user.email,
                        result.user.fullName
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
