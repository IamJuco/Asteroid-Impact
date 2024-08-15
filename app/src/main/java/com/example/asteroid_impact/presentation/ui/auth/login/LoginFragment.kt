package com.example.asteroid_impact.presentation.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentLoginBinding
import com.example.asteroid_impact.presentation.dialog.BackPressedExitAppDialog
import com.example.asteroid_impact.presentation.ui.auth.SharedViewModel
import com.example.asteroid_impact.presentation.ui.auth.SharedViewModelFactory
import com.example.asteroid_impact.presentation.ui.auth.changepassword.ChangePasswordFragment
import com.example.asteroid_impact.presentation.ui.auth.register.RegisterEmailFragment
import com.example.asteroid_impact.presentation.ui.main.MainActivity
import com.example.asteroid_impact.presentation.util.setPasswordToggle
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.checkUserLoggedIn()) {
            moveToMainActivity()
        } else {
            setUpPasswordVisibleToggle()
            viewModel.clearLoginResult()
            setUpListener()
            setupIsEnabledButton()
            setUpObserver()
            backPressedForExitApp()
        }
    }

    private fun setUpPasswordVisibleToggle() {
        binding.ivPasswordVisibleToggle.setPasswordToggle(binding.etPassword)
    }

    private fun setUpObserver() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Snackbar.make(binding.root, R.string.login_fragment_login_success, Snackbar.LENGTH_SHORT).show()
                moveToMainActivity()
            }?.onFailure {
                Log.d("0526LoginSuccessOrFailure", "로그인 실패 ${it.message}")
                Snackbar.make(binding.root, R.string.login_fragment_login_fail, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupIsEnabledButton() {
        val textWatcher = object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val isLoginCheck = loginCheck()
                binding.btnLogin.isEnabled = isLoginCheck
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
    }

    private fun loginCheck(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        val emailPatternCheck = Pattern.matches(emailPattern, email)

        return emailPatternCheck && email.isNotEmpty() && password.isNotEmpty()
    }

    private fun setUpListener() {
        binding.btnRegister.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, RegisterEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.loginUser(email, password)
        }

        binding.tvChangePassword.setOnClickListener {
            viewModel.clearSendVerifyCodeForChangePassword()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnAnonymousLogin.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun moveToMainActivity() {
        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun backPressedForExitApp() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               val dialog = BackPressedExitAppDialog()
                dialog.show(parentFragmentManager, "BackPressedExitAppDialog")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}