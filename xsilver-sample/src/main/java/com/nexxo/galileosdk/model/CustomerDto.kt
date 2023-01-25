package com.nexxo.galileosdk.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerDto(

    var email: String? = "",
    var phoneNumber: String? = "",
    var countryCode: String? = "",
    var walletAddress: String? = "",
    var environment: String? = "",
    var referralCode:String?=""
) : Parcelable
