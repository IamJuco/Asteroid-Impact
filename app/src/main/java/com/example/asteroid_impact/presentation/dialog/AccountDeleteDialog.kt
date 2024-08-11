package com.example.asteroid_impact.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.asteroid_impact.R
import com.example.asteroid_impact.databinding.DialogAccountDeleteBinding
import com.google.android.material.snackbar.Snackbar

class AccountDeleteDialog : DialogFragment() {
    private var _binding: DialogAccountDeleteBinding? = null
    private val binding get() = _binding!!

    private var listener: AccountDeleteListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAccountDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDeleteAccount.setOnClickListener {
            val password = binding.etPassword.text.toString()
            if (password.isEmpty()) {
                Snackbar.make(binding.root, R.string.account_delete_text_null, Snackbar.LENGTH_SHORT).show()
            } else {
                listener?.onAccountDelete(password)
                dismiss()
            }

        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setListener(listener: AccountDeleteListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface AccountDeleteListener {
        fun onAccountDelete(password: String)
    }
}