package com.nexxo.galileosdk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerDto(

    var email: String? = "",
    var mobileNumber: String? = "",
    var walletAddress: String? = "",
    var environment: String? = "",
    var referralCode:String?=""
) : Parcelable
