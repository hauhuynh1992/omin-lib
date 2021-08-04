package com.bda.omnilibrary.ui.checkoutExistingUser.creditCard.credit

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.ProductDescriptionDialog
import com.bda.omnilibrary.util.Functions
import kotlinx.android.synthetic.main.fragment_credit_card.*

/**
 * A simple [Fragment] subclass.
 */
class CreditCardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credit_card, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initial()
    }

    private fun initial() {
        edt_card_number.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.setStrokeFocus(cv_edt_card_number, activity!!)
            } else {
                Functions.setLostFocus(cv_edt_card_number, activity!!)
            }
        }

        edt_expired.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.setStrokeFocus(cv_edt_expired, activity!!)
            } else {
                Functions.setLostFocus(cv_edt_expired, activity!!)
            }
        }

        edt_vcc.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.setStrokeFocus(cv_edt_vcc, activity!!)
            } else {
                Functions.setLostFocus(cv_edt_vcc, activity!!)
            }
        }

        bn_pay.setOnFocusChangeListener { _, hasFocus ->
            text_bn_pay.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_pay.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_pay.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.text_black_70)
                }
            }
        }

        /*edt_card_number.setOnClickListener {
            *//*val keyboardDialog = KeyboardDialog(
                       KeyboardDialog.KEYBOARD_NAME_TYPE,
                       edt_name.text.toString(),
                       edt_name, edt_phone
                   )
                   keyboardDialog.show(childFragmentManager, keyboardDialog.tag)*//*
        }

        edt_expired.setOnClickListener {
            *//*val keyboardDialog = KeyboardDialog(
                       KeyboardDialog.KEYBOARD_NAME_TYPE,
                       edt_name.text.toString(),
                       edt_name, edt_phone
                   )
                   keyboardDialog.show(childFragmentManager, keyboardDialog.tag)*//*
        }

        edt_vcc.setOnClickListener {
            *//*val keyboardDialog = KeyboardDialog(
                       KeyboardDialog.KEYBOARD_NAME_TYPE,
                       edt_name.text.toString(),
                       edt_name, edt_phone
                   )
                   keyboardDialog.show(childFragmentManager, keyboardDialog.tag)*//*
        }*/

        rule.setOnClickListener {
            ProductDescriptionDialog(activity!!, "")

        }

        bn_pay.setOnClickListener {

        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreditCardFragment()
    }
}
