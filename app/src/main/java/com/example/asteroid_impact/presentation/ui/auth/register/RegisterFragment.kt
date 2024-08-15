package com.example.asteroid_impact.presentation.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.R
import com.example.asteroid_impact.databinding.FragmentRegisterBinding
import com.example.asteroid_impact.presentation.ui.auth.login.LoginFragment
import com.example.asteroid_impact.presentation.ui.auth.SharedViewModel
import com.example.asteroid_impact.presentation.util.setPasswordToggle
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPasswordVisibleToggle()
        setUpListener()
        setupIsEnabledButton()
        setUpObserver()
    }

    private fun setUpPasswordVisibleToggle() {
        binding.ivPasswordVisibleToggle.setPasswordToggle(binding.etPassword)
        binding.ivPasswordCheckVisibleToggle.setPasswordToggle(binding.etPasswordCheck)
    }

    private fun setUpObserver() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Snackbar.make(binding.root, R.string.register_success, Snackbar.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, LoginFragment())
                    .commit()
            }?.onFailure {
                Snackbar.make(binding.root, R.string.register_fail, Snackbar.LENGTH_SHORT).show()
                Log.d("0526RegisterFail", "회원가입 실패: ${it.message}")
            }
        }
    }

    private fun setupIsEnabledButton() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateRegisterButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etPassword.addTextChangedListener(textWatcher)
        binding.etPasswordCheck.addTextChangedListener(textWatcher)
        binding.etNickname.addTextChangedListener(textWatcher)
    }

    private fun updateRegisterButtonState() {
        val isPasswordValid = checkPassword()
        val isPasswordCheckValid = checkPasswordAgain()
        val isNicknameValid = checkNickname()

        binding.btnRegisterSuccess.isEnabled =
            isPasswordValid && isPasswordCheckValid && isNicknameValid
    }

    private fun checkPassword(): Boolean {
        val password = binding.etPassword.text.toString()
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,16}$"
        val passwordPatternCheck = Pattern.matches(passwordPattern, password)
        return if (password.isNotEmpty() && !passwordPatternCheck) {
            binding.tvPasswordWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvPasswordWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun checkPasswordAgain(): Boolean {
        val password = binding.etPassword.text.toString()
        val passwordCheck = binding.etPasswordCheck.text.toString()
        return if (passwordCheck.isNotEmpty() && password != passwordCheck) {
            binding.tvPasswordCheckWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvPasswordCheckWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun checkNickname(): Boolean {
        val nickname = binding.etNickname.text.toString()
        return if (nickname.isNotEmpty() && (nickname.length !in 2..6)) {
            binding.tvNicknameWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvNicknameWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun setUpListener() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnRegisterSuccess.setOnClickListener {
            val password = binding.etPassword.text.toString()
            viewModel.registerUser(password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}