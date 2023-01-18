package com.nexxo.galileosdk.network

class MainRepository constructor(private val retrofitService: RetrofitService) {

    suspend fun verifyXsilverUser(
        url: String,
        phone: String?,
        countryCode: String?,
        referenceNumber: String?
    ) =
        retrofitService.verifyXsilverUser(url, phone, countryCode, referenceNumber)

    suspend fun getTransactionDetails(url: String) = retrofitService.getTransactionDetails(url)
}