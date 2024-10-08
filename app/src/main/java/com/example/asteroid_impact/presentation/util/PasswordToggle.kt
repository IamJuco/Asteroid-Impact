package com.example.asteroid_impact.presentation.util

import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import com.example.asteroid_impact.R

fun ImageView.setPasswordToggle(editText: EditText) {
    this.setOnClickListener {
        if (editText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            this.setImageResource(R.drawable.ic_visibility)
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            this.setImageResource(R.drawable.ic_visibility_off)
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        editText.setSelection(editText.text?.length ?: 0)
    }
}