package com.example.asteroid_impact.presentation.ui.auth.register

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
import com.example.asteroid_impact.presentation.ui.auth.SharedViewModel
import com.example.asteroid_impact.presentation.ui.auth.SharedViewModelFactory
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
                    Snackbar.make(binding.root, R.string.email_fragment_success, Snackbar.LENGTH_SHORT).show()

                    val email = viewModel.email.value
                    val password = Constants.USER_TEMP_PASSWORD

                    //TODO email null check 해봐야할듯
                    viewModel.tempDeleteAccountAndReAuthentication(
                        email.orEmpty(),
                        password
                    ) { result ->
                        result.onSuccess {
                            viewModel.clearRegisterResult()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.frameContainer, RegisterFragment())
                                .commit()
                        }.onFailure {
                            Log.d("0526checkEmailVerify", "\"계정 삭제 실패: ${it.message}\"")
                        }
                    }

                } else {
                    Snackbar.make(binding.root, R.string.email_fragment_fail, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setUpListener() {
        binding.ivBack.setOnClickListener {
            viewModel.tempAccountDeleteForDoNotEmailVerification()
            viewModel.clearRegisterResult()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}