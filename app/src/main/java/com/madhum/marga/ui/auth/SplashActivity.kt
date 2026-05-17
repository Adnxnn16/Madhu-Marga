package com.madhum.marga.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.madhum.marga.databinding.ActivitySplashBinding
import com.madhum.marga.ui.main.MainActivity
import com.madhum.marga.util.SessionManager

/**
 * SplashActivity — entry point with animated logo.
 * Redirects to MainActivity if logged in, LoginActivity otherwise.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Fade-in animation
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1200
            fillAfter = true
        }
        binding.ivLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(fadeIn)

        val fadeTagline = AlphaAnimation(0f, 1f).apply {
            duration = 1200
            startOffset = 600
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    navigateNext()
                }
            })
        }
        binding.tvTagline.startAnimation(fadeTagline)
    }

    private fun navigateNext() {
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
