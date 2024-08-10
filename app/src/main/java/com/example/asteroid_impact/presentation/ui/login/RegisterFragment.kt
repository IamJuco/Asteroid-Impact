package com.example.asteroid_impact.presentation.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
        setupIsEnabledButton()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Snackbar.make(binding.root, "회원가입 성공", Snackbar.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, LoginFragment())
                    .commit()
            }?.onFailure {
                Snackbar.make(binding.root, "회원가입 실패: ${it.message}", Snackbar.LENGTH_SHORT).show()
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

        binding.btnRegisterSuccess.setOnClickListener {
            val password = binding.etPassword.text.toString()
            viewModel.registerUser(password)
        }
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
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
