package com.example.asteroid_impact.presentation.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentChangePasswordBinding
import com.google.android.material.snackbar.Snackbar

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
        setUpObserver()
        setUpListener()
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

    private fun setUpListener() {
        binding.btnSendEmailVerifyCode.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                viewModel.sendVerifyCodeForChangePassword(email)
            } else {
                Snackbar.make(binding.root, "이메일을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}