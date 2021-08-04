package com.bda.omnilibrary.ui.authenActivity.fragmentLogin

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.custome.BDAKeyboardView
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        keyboard.visibility = View.GONE
        if (keyboard != null) {
            keyboard.initKeyboardNumber()
            keyboard.setOnKeyboardCallback(object : BDAKeyboardView.OnKeyboardCallback {

                override fun onSearchSubmit(query: String?, cursor: Int) {

                }

                override fun onChangeCursor(position: Int) {

                }

                override fun onComplete() {

                }

                override fun onNext() {

                }

                override fun onSearchFocusable(isFocus: Boolean) {

                }
            })

        }
        Handler().postDelayed({
            keyboard.visibility = View.GONE
            edt_phone__.requestFocus()
        }, 300)

        edt_phone__.setOnClickListener {
            keyboard.visibility = View.VISIBLE
            keyboard.removeAllViews()
            keyboard.initKeyboardNumber()
            keyboard.addText(edt_phone__.text.toString())
            edt_phone__.isSelected = true
            keyboard.requestFocus()
        }

        edt_password.setOnClickListener {
            keyboard.removeAllViews()
            keyboard.visibility = View.VISIBLE
            keyboard.initKeyboardAddress()
            keyboard.addText(edt_password.text.toString())
            edt_password.isSelected = true
            keyboard.requestFocus()
        }

        bn_login.setOnFocusChangeListener { view, isFocus ->
            if (isFocus) {
                keyboard.visibility = View.GONE
                edt_password.isSelected = false
                edt_phone__.isSelected = false
            }
        }


        bn_login.setOnClickListener {

        }
    }

    @SuppressLint("SetTextI18n")
    fun dispatchKeyEvent(event: KeyEvent?) {
        var text = ""
        if (edt_phone__.isSelected) {
            text = edt_phone__.text.toString()
        } else {
            text = edt_password.text.toString()
        }
        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (event!!.keyCode) {
                KeyEvent.KEYCODE_0 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "0"
                    } else {
                        edt_password.text = text + "0"
                    }
                    keyboard.addText(text + "0")
                }
                KeyEvent.KEYCODE_1 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "1"
                    } else {
                        edt_password.text = text + "1"
                    }
                    keyboard.addText(text + "1")
                }
                KeyEvent.KEYCODE_2 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "2"
                    } else {
                        edt_password.text = text + "2"
                    }
                    keyboard.addText(text + "2")
                }
                KeyEvent.KEYCODE_3 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "3"
                    } else {
                        edt_password.text = text + "3"
                    }
                    keyboard.addText(text + "3")
                }
                KeyEvent.KEYCODE_4 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "4"
                    } else {
                        edt_password.text = text + "4"
                    }
                    keyboard.addText(text + "4")
                }
                KeyEvent.KEYCODE_5 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "5"
                    } else {
                        edt_password.text = text + "5"
                    }
                    keyboard.addText(text + "5")
                }
                KeyEvent.KEYCODE_6 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "6"
                    } else {
                        edt_password.text = text + "6"
                    }
                    keyboard.addText(text + "6")
                }
                KeyEvent.KEYCODE_7 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "7"
                    } else {
                        edt_password.text = text + "7"
                    }
                    keyboard.addText(text + "7")
                }
                KeyEvent.KEYCODE_8 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "8"
                    } else {
                        edt_password.text = text + "8"
                    }
                    keyboard.addText(text + "8")
                }
                KeyEvent.KEYCODE_9 -> {
                    if (edt_phone__.isSelected) {
                        edt_phone__.text = text + "9"
                    } else {
                        edt_password.text = text + "9"
                    }
                    keyboard.addText(text + "9")
                }
                KeyEvent.KEYCODE_DEL -> {
                    if (!text.isBlank()) {

                        if (text.length >= 2) {
                            val t = text.subSequence(0, text.length - 1)
                            if (edt_phone__.isSelected) {
                                edt_phone__.text = t
                            } else {
                                edt_password.text = t
                            }
                            keyboard.addText(t.toString())
                        }

                        if (text.length == 1) {
                            if (edt_phone__.isSelected) {
                                edt_phone__.text = ""
                            } else {
                                edt_password.text = ""
                            }
                            keyboard.addText("")
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
