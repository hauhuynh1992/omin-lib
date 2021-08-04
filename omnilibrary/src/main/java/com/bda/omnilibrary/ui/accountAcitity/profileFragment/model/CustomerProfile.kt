package com.bda.omnilibrary.ui.accountAcitity.profileFragment.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerProfile(
    val uid: String = "",
    val gender: String = "",
    val customer_name: String = "",
    val email: String = "",
    val phone_number: String = "",
    val dateOfBirth: Long = 0
) : Parcelable
