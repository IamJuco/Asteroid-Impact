package com.example.asteroid_impact.presentation.ui.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BackPressedExitAppDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("앱 종료")
            .setMessage("정말 종료하시겠습니까?")
            .setPositiveButton("네") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}