package com.nexxo.galileosdk.model

import com.google.gson.annotations.SerializedName

data class VerifyXsilverUserDto(

    @SerializedName("isXsilverUser") var isXsilverUser: Boolean? = false,
    @SerializedName("kycLevel") var kycLevel: String? = ""
)
