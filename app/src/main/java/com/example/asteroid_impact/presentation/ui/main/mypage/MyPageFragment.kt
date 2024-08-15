package com.example.asteroid_impact.presentation.ui.main.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.asteroid_impact.databinding.FragmentMyPageBinding
import com.example.asteroid_impact.presentation.dialog.AccountDeleteDialog
import com.example.asteroid_impact.presentation.ui.auth.AuthActivity
import com.google.android.material.snackbar.Snackbar

class MyPageFragment : Fragment(), AccountDeleteDialog.AccountDeleteListener {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListener()
        setUpObserver()

    }

    private fun setUpObserver() {
        viewModel.accountDeleteResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    viewModel.signOut()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                    Snackbar.make(binding.root, "회원탈퇴가 되었습니다.", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "회원 탈퇴에 실패했습니다. 비밀번호를 확인해주세요.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.d("0526AccountDeleteInMyPage", "계정 탈퇴 실패: ${it.exceptionOrNull()?.message}")
                }
            }
        }
    }

    private fun setUpListener() {
        binding.btnLogout.setOnClickListener {
            viewModel.signOut()
            requireActivity().startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finish()
        }

        binding.btnDeleteAccount.setOnClickListener {
            val dialog = AccountDeleteDialog()
            dialog.setListener(this)
            dialog.show(parentFragmentManager, "AccountDeleteDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAccountDelete(password: String) {
        val email = viewModel.authRepository.getCurrentUser()?.email ?: return
        viewModel.deleteAccount(email, password)
    }
}