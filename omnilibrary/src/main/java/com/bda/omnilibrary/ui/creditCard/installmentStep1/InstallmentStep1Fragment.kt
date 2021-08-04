package com.bda.omnilibrary.ui.checkoutExistingUser.creditCard.installmentStep1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R

class InstallmentStep1Fragment : Fragment() {
    //private lateinit var adapter: PaymentTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_installment_step1, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*adapter = PaymentTypeAdapter(activity!!, arrayListOf()) {}

        vg_product.adapter = adapter*/
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InstallmentStep1Fragment()
    }
}
