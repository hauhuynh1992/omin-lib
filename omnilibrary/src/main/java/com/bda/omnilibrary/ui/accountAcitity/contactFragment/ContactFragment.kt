package com.bda.omnilibrary.ui.accountAcitity.contactFragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bda.omnilibrary.R


class ContactFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            ContactFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
