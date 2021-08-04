package com.bda.omnilibrary.ui.checkoutExistingUser.creditCard.InstallmentStep2Financial

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.Functions
import kotlinx.android.synthetic.main.fragment_installment_step2_financial.*

/**
 * A simple [Fragment] subclass.
 */
class InstallmentStep2FinancialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_installment_step2_financial, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            InstallmentStep2FinancialFragment()/*.apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initial()
    }


    private fun initial() {


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

        bn_call_me.setOnFocusChangeListener { _, hasFocus ->
            text_bn_call_me.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_call_me.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_call_me.outlineSpotShadowColor =
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

        bn_pay.setOnClickListener {

        }

        bn_call_me.setOnClickListener {

        }

    }
}
