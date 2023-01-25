package com.nexxo.galileosdk.network

import android.provider.SyncStateContract.Constants
import com.nexxo.galileosdk.model.TransactionStatusDto
import com.nexxo.galileosdk.model.VerifyXsilverUserDto
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit

interface RetrofitService {

    @GET
    suspend fun verifyXsilverUser(
        @Url url: String, @Query("phone") phone: String?,
        @Query("countryCode") countryCode: String?,
        @Query("referenceNumber") referenceNumber: String?
    ): Response<VerifyXsilverUserDto>

    @GET
    suspend fun getTransactionDetails(@Url url: String): Response<TransactionStatusDto>

    companion object {
        private lateinit var interceptor: HttpLoggingInterceptor
        private lateinit var okHttpClient: OkHttpClient
        private var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            interceptor.level = HttpLoggingInterceptor.Level.HEADERS
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectionSpecs(
                    Arrays.asList(
                        ConnectionSpec.MODERN_TLS,
                        ConnectionSpec.CLEARTEXT,
                        ConnectionSpec.COMPATIBLE_TLS
                    )
                )
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .apply {
                    addInterceptor(
                        Interceptor { chain ->
                            val builder = chain.request().newBuilder()
                            builder.header("Accept-Language", "en")
                            builder.header("User-Agent", "agent")
//                            builder.header("x-channel", Constants.XSILVER_APP_CHANNEL)
                            builder.header("Content-Type", "application/json")
//                            builder.header("x-device-details", Constants.DEVICE_DETAILS_VALUE)
//                            builder.header("x-source", Constants.DEVICE_SOURCE)
                            return@Interceptor chain.proceed(builder.build())
                        }
                    )
                }.build()
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(com.nexxo.galileosdk.utils.Constants.MAIN_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}