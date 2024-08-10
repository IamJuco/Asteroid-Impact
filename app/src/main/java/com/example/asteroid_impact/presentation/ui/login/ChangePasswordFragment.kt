package com.example.asteroid_impact.presentation.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.Constants
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentChangePasswordBinding
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
        setupIsEnabledButton()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.sendVerifyCodeForChangePassword.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Snackbar.make(binding.root, "비밀번호 재설정 이메일이 발송되었습니다.", Snackbar.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, ChangePasswordVerifyFragment())
                    .commit()
            } else {
                Snackbar.make(
                    binding.root,
                    "비밀번호 재설정 실패: ${result.exceptionOrNull()?.message}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupIsEnabledButton() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isCheckEmail = checkEmail()
                binding.btnSendEmailVerifyCode.isEnabled = isCheckEmail
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etEmail.addTextChangedListener(textWatcher)
    }

    private fun setUpListener() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSendEmailVerifyCode.setOnClickListener {
            val email = binding.etEmail.text.toString()
            viewModel.sendVerifyCodeForChangePassword(email)
        }
    }

    private fun checkEmail(): Boolean {
        val email = binding.etEmail.text.toString()
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        val emailPatternCheck = Pattern.matches(emailPattern, email)
        return !(email.isNotEmpty() && !emailPatternCheck)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}