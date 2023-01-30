package com.nexxo.galileosdk.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerDto(

    var email: String? = "", //customer's email address
    var phoneNumber: String? = "", //customer's phone number
    var countryCode: String? = "", //customer's ISD code
    var walletAddress: String? = "", //customer's crypto wallet address
    var environment: String? = "", //environment test or live
    var referralCode:String?="" //partner's referral code
) : Parcelable
