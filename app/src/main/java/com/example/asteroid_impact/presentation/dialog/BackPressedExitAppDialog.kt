package com.example.asteroid_impact.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.asteroid_impact.R

class BackPressedExitAppDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.exit_app_title)
            .setMessage(R.string.exit_app_message)
            .setPositiveButton(R.string.exit_app_positive) { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton(R.string.exit_app_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}