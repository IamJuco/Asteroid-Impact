package com.example.asteroid_impact.presentation.ui.login

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
import com.example.asteroid_impact.presentation.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        setUpListener()
        setUpTextWatchers()
        backPressedForExitApp()
    }

    private fun setUpObserver() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, "로그인 성공", Snackbar.LENGTH_SHORT).show()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }.onFailure {
                Log.d("0526LoginSuccessOrFailure", "로그인 실패 ${it.message}")
                Snackbar.make(binding.root, "로그인에 실패했습니다. 이메일또는 비밀번호를 다시 확인해주세요.}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val loginCheck = loginCheck()
                binding.btnLogin.isEnabled = loginCheck
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