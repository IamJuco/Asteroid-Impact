package com.example.asteroid_impact.presentation.ui.login

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.asteroid_impact.R
import com.example.asteroid_impact.databinding.ActivityLoginBinding
import com.example.asteroid_impact.presentation.shared.KeyboardCleaner

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val keyboardCleaner: KeyboardCleaner by lazy {
        KeyboardCleaner(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        uiSetting()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, LoginFragment())
                .commit()
        }
    }

    private fun uiSetting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_UP) keyboardCleaner.setPrevFocus(currentFocus)
        val result = super.dispatchTouchEvent(ev)
        keyboardCleaner.handleTouchEvent(ev)
        return result
    }
}