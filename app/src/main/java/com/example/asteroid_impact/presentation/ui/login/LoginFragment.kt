package com.example.asteroid_impact.presentation.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentLoginBinding
import com.example.asteroid_impact.presentation.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

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
    }

    private fun setUpObserver() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Snackbar.make(binding.root, "로그인 성공", Snackbar.LENGTH_SHORT).show()
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }.onFailure {
                Snackbar.make(binding.root, "로그인 실패: ${it.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpListener() {
        binding.btnRegister.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, RegisterEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.loginUser(email, password)
        }

        binding.tvChangePassword.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainer, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}