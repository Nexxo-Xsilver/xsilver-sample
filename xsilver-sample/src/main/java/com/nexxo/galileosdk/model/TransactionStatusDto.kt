package com.nexxo.galileosdk.model

import com.google.gson.annotations.SerializedName

data class TransactionStatusDto(

    @SerializedName("paymentMode") var paymentMode: String? = "",
    @SerializedName("cardNumber") var cardNumber: String? = "",
    @SerializedName("referenceId") var referenceId: String? = "",
    @SerializedName("date") var date: String? = "",
    @SerializedName("fiatAmount") var fiatAmount: String? = "",
    @SerializedName("fiatType") var fiatType: String? = "",
    @SerializedName("cryptoAmount") var cryptoAmount: String? = "",
    @SerializedName("cryptoType") var cryptoType: String? = "",
    @SerializedName("phoneNumber") var phoneNumber: String? = "",
    @SerializedName("isdCode") var isdCode: String? = "",
    @SerializedName("emailId") var emailId: String? = "",
    @SerializedName("whitelistedAddress") var whitelistedAddress: String? = "",
    @SerializedName("txnStatus") var txnStatus: String? = ""
)