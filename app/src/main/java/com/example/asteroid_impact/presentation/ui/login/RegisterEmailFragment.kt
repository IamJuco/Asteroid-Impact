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
import com.example.asteroid_impact.databinding.FragmentRegisterEmailBinding
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class RegisterEmailFragment : Fragment() {
    private var _binding: FragmentRegisterEmailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupIsEnabledButton()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewModel.registerResultForEmailVerification.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                Snackbar.make(binding.root, "임시 회원가입 성공", Snackbar.LENGTH_SHORT).show()
                viewModel.sendEmailVerification()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, EmailVertifyFragment())
                    .commit()

            }?.onFailure {
                Snackbar.make(binding.root, "임시 회원가입 실패: ${it.message}", Snackbar.LENGTH_SHORT)
                    .show()
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
        binding.btnSendEmailVerifyCode.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = Constants.USER_TEMP_PASSWORD

            viewModel.registerUserForEmailVerification(email, password)
        }
    }

    private fun checkEmail(): Boolean {
        val email = binding.etEmail.text.toString()
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        val emailPatternCheck = Pattern.matches(emailPattern, email)
        return if (email.isNotEmpty() && !emailPatternCheck) {
            binding.tvEmailWarning.visibility = View.VISIBLE
            false
        } else {
            binding.tvEmailWarning.visibility = View.INVISIBLE
            true
        }
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}