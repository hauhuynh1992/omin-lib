package com.bda.omnilibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.custome.BDAKeyboardView
import kotlinx.android.synthetic.main.dialog_keyboard.*

class KeyboardDialog(
    val keyboardType: Int,
    val content: String,
    val tvContent: EditText,
    val nextEditText: View? = null
) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_keyboard, container).apply {
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            if (keyboard != null) {
                keyboard.removeAllViews()
                when (keyboardType) {
                    KEYBOARD_NAME_TYPE -> {
                        keyboard.initKeyboardName()
                    }
                    KEYBOARD_ADDRESS_TYPE -> {
                        keyboard.initKeyboardAddress()
                    }
                    KEYBOARD_PHONE_TYPE -> {
                        keyboard.initKeyboardNumber()
                    }
                }
                keyboard.setOnKeyboardCallback(object : BDAKeyboardView.OnKeyboardCallback {
                    override fun onSearchSubmit(query: String?, cursor: Int) {
                        tvContent.setText(query.toString())
                        tvContent.setSelection(cursor)
                    }

                    override fun onChangeCursor(position: Int) {
                        tvContent.setSelection(position)
                    }

                    override fun onComplete() {
                        dismiss()
                        nextEditText?.requestFocus()
                    }

                    override fun onNext() {
                        dismiss()
                    }

                    override fun onSearchFocusable(isFocus: Boolean) {

                    }

                })
                keyboard.addText(content)
                keyboard.requestFocus()
            }

        }, 300)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(true)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.BOTTOM
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window.attributes
            lp.dimAmount = 0f
            dialog.setOnKeyListener { _, _, event ->
                /*if (event.action == KeyEvent.ACTION_DOWN) {

                }*/
                false
            }
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog.window?.attributes = lp
        }
    }

    companion object {
        const val KEYBOARD_NAME_TYPE = 0
        const val KEYBOARD_PHONE_TYPE = 1
        const val KEYBOARD_ADDRESS_TYPE = 2
    }

}
