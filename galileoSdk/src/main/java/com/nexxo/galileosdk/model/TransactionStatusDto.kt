package com.nexxo.galileosdk.model

import com.google.gson.annotations.SerializedName

data class TransactionStatusDto(

    @SerializedName("paymentMode") var paymentMode: String? = null,
    @SerializedName("cardNumber") var cardNumber: String? = null,
    @SerializedName("referenceId") var referenceId: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("fiatAmount") var fiatAmount: String? = null,
    @SerializedName("fiatType") var fiatType: String? = null,
    @SerializedName("cryptoAmount") var cryptoAmount: String? = null,
    @SerializedName("cryptoType") var cryptoType: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("isdCode") var isdCode: String? = null,
    @SerializedName("emailId") var emailId: String? = null,
    @SerializedName("whitelistedAddress") var whitelistedAddress: String? = null,
    @SerializedName("txnStatus") var txnStatus: String? = null,
    @SerializedName("txnStatusId") var txnStatusId: String? = null
)
