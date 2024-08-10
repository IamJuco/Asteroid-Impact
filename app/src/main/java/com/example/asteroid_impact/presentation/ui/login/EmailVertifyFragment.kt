package com.example.asteroid_impact.presentation.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.asteroid_impact.Constants
import com.example.asteroid_impact.R
import com.example.asteroid_impact.data.repository.FirebaseAuthRepositoryImpl
import com.example.asteroid_impact.databinding.FragmentEmailVertifyBinding
import com.google.android.material.snackbar.Snackbar

class EmailVertifyFragment : Fragment() {
    private var _binding: FragmentEmailVertifyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(FirebaseAuthRepositoryImpl())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVertifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkEmailVerify()
        setUpListener()
    }

    private fun checkEmailVerify() {
        binding.btnEmailVertify.setOnClickListener {
            viewModel.checkEmailVerification { isVerified ->
                if (isVerified) {
                    Snackbar.make(binding.root, "이메일 인증 완료", Snackbar.LENGTH_SHORT).show()

                    val email = viewModel.email.value
                    val password = Constants.USER_TEMP_PASSWORD

                    //TODO email null check 해봐야할듯
                    viewModel.deleteAccountAndReAuthentication(
                        email.orEmpty(),
                        password
                    ) { result ->
                        result.onSuccess {
                            viewModel.clearRegisterResult()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.frameContainer, RegisterFragment())
                                .commit()
                        }.onFailure {
                            Snackbar.make(
                                binding.root,
                                "계정 삭제 실패: ${it.message}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    Snackbar.make(binding.root, "이메일 인증이 아직 완료되지 않았습니다.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setUpListener() {
        binding.ivBack.setOnClickListener {
            viewModel.accountDelete()
            viewModel.clearRegisterResult()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}