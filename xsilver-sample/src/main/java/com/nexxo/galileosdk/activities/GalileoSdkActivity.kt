package com.nexxo.galileosdk.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.nexxo.galileosdk.R
import com.nexxo.galileosdk.interfaces.TransactionCallbacks
import com.nexxo.galileosdk.model.CustomerDto
import com.nexxo.galileosdk.network.MainRepository
import com.nexxo.galileosdk.network.RetrofitService
import com.nexxo.galileosdk.utils.Constants
import com.nexxo.galileosdk.utils.UtilityKotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GalileoSdkActivity : AppCompatActivity() {
    private var referenceNumber: String = ""
    private val MAIN_BASE_URL: String = ""
    private var mUrlString = "https://sandbox.xsilver.com/widget/"
    private lateinit var mWebView: WebView
    private var mTransactionCallbacks: TransactionCallbacks? = null
    private var customerDto: CustomerDto = CustomerDto()
    private var mPermissionRequest: PermissionRequest? = null
    private val REQUEST_CAMERA_PERMISSION = 1

    //API Call
    private val retrofitService = RetrofitService.getInstance()
    private val mainRepository = MainRepository(retrofitService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galileo_sdk)
        initView()
    }

    private fun initView() {
        mTransactionCallbacks = intent.getSerializableExtra("context") as TransactionCallbacks
        if (customerDto != null) {
            customerDto = intent.getParcelableExtra("customerData")!!
            UtilityKotlin.logData("customerDto -$customerDto")
            mUrlString = if (customerDto.environment?.lowercase() == "test") {
                "https://sandbox.xsilver.com/widget/${customerDto.referralCode}?phoneNumber=${customerDto.phoneNumber}&countryCode=${customerDto.countryCode}&email=${customerDto.email}&walletAddress=${customerDto.walletAddress}"
            } else {
                "https://xsilver.com/widget/${customerDto.referralCode}?phoneNumber=${customerDto.phoneNumber}&countryCode=${customerDto.countryCode}&email=${customerDto.email}&walletAddress=${customerDto.walletAddress}"
            }
        }
        mWebView = findViewById(R.id.webview)
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.setAppCacheEnabled(false)
        mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.builtInZoomControls = false
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.databaseEnabled = true
        mWebView.settings.allowFileAccess = true
        mWebView.settings.allowContentAccess = true
        mWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.mediaPlaybackRequiresUserGesture = false
        mWebView.settings.setSupportZoom(false)
        mWebView.settings.setGeolocationEnabled(true)
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                mPermissionRequest = request!!
                try {
                    val requestedResources = request.resources
                    for (r in requestedResources) {
                        if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                            UtilityKotlin.logData("inside RESOURCE_VIDEO_CAPTURE - true")
                            request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                            checkCameraPermission()
                            break
                        } else
                            UtilityKotlin.logData("inside RESOURCE_VIDEO_CAPTURE - false")
                    }
                } catch (e: Exception) {
                    UtilityKotlin.logData("Exception while onPermissionRequest - ${e.localizedMessage}")
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
//                super.onGeolocationPermissionsShowPrompt(origin, callback)
                try {
                    callback?.invoke(origin, true, false)
                } catch (e: Exception) {
                    UtilityKotlin.logData("Exception while onGeolocationPermissionsShowPrompt - ${e.localizedMessage}")
                }
            }
        }
        initWebView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (mWebView.canGoBack()) {
                mWebView.goBack()
            } else {
                mTransactionCallbacks?.onUserCancel()
            }
        } catch (e: Exception) {
            UtilityKotlin.logData("exception while onBackPressed - ${e.localizedMessage}")
        }
    }

    private fun initWebView() {
        try {
            mWebView.loadUrl(mUrlString)
            mWebView.webViewClient = object : WebViewClient() {
                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
//                    UtilityKotlin.logData("onLoadResource- $url")
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    UtilityKotlin.logData("doUpdateVisitedHistory url-$url")
                    if (url?.contains("finished") == true) {
                        getTransactionStatus()
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let { view?.loadUrl(it) }
                    UtilityKotlin.logData("shouldOverrideUrlLoading- $url")
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    try {
                        UtilityKotlin.logData("onPageStarted - url - ${url.toString()}")
                    } catch (e: Exception) {
                        UtilityKotlin.logData("Exception while onPageStarted - ${e.localizedMessage}")
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        UtilityKotlin.logData("onPageFinished - url - ${url.toString()}")
                        if (url?.contains("webhook") == true) {
                            val uri = Uri.parse(url)
                            referenceNumber = uri.getQueryParameter("referenceNo").toString()
                            UtilityKotlin.logData("referenceNumber - $referenceNumber")
                            verifyXsilverUser()
                        }
                    } catch (e: Exception) {
                        UtilityKotlin.logData("Exception while onPageFinished - ${e.localizedMessage}")
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    UtilityKotlin.logData("onReceivedError - ${error.toString()}")
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    UtilityKotlin.logData("onReceivedHttpError - ${errorResponse.toString()}")
                }
            }
        } catch (e: Exception) {
            UtilityKotlin.logData("Exception while initWebView - ${e.localizedMessage}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                    ) {
                        UtilityKotlin.logData("Camera permission granted")
//                        mWebView.reload()
                    } else if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        UtilityKotlin.logData("Read storage permission granted")
//                        mWebView.reload()
                    } else if (perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    ) {
                        UtilityKotlin.logData("Write storage permission granted")
                        mWebView.reload()
                    } else {
                        UtilityKotlin.logData("Camera permission is not granted ask again ")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.CAMERA
                            )
                        ) {
                            showDialogOK(
                                "Permission required for run this app"
                            ) { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkCameraPermission()
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        showSnackBar(
                                            "In order to work properly " + getString(R.string.app_name) + " App needs some permission.",
                                            "Go To Settings"
                                        )
                                    }
                                }
                            }
                        } else {
                            showSnackBar(
                                "In order to work properly " + getString(R.string.app_name) + " App needs some permission.",
                                "Go To Settings"
                            )
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        try {
            val permission = Manifest.permission.CAMERA
            val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
            val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    readStoragePermission
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    writeStoragePermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@GalileoSdkActivity,
                    arrayOf(permission, readStoragePermission, writeStoragePermission),
                    REQUEST_CAMERA_PERMISSION
                )
                return false
            }
            return true
        } catch (e: Exception) {
            UtilityKotlin.logData("Exception while checkCameraPermission - ${e.localizedMessage}")
        }
        return true
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun showSnackBar(string: String, stringSetAction: String) {
        try {
            showPopUp(this, string)
        } catch (e: Exception) {
            UtilityKotlin.logData("Exception while showSnackBar - ${e.localizedMessage}")
        }
    }

    private fun showPopUp(context: Context, str: String?) {
        val alertDialog2 = AlertDialog.Builder(context)

        // Setting Dialog Title
        alertDialog2.setTitle(context.getString(R.string.app_name))
        alertDialog2.setCancelable(false)

        // Setting Dialog Message
        alertDialog2.setMessage(str)

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
        }
        // Setting Positive "Yes" Btn
        alertDialog2.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        // Showing Alert Dialog
        alertDialog2.show()
    }

    private fun getTransactionStatus() {
        try {
            CoroutineScope(
                Dispatchers.IO + UtilityKotlin.onErrorCoroutineExceptionHandler(
                    this@GalileoSdkActivity
                )
            ).launch {
                val url = MAIN_BASE_URL + Constants.TRANSACTION_STATUS_URL + referenceNumber
                val response = mainRepository.getTransactionDetails(url)
                withContext(Dispatchers.Main) {
                    try {
                        UtilityKotlin.logData("Response of trxn status API - $response")
                        if (response.isSuccessful) {
                            try {
                                val transactionStatusDto = response.body()
                                when (transactionStatusDto?.txnStatus) {
                                    "SUCCESSFUL" -> {
//                                    SUCCESSFUL
                                        mTransactionCallbacks?.onSuccess(Gson().toJson(transactionStatusDto))
                                        finish()
                                    }
                                    "FAILED" -> {
//                                    FAILED
                                        mTransactionCallbacks?.onFailure(Gson().toJson(transactionStatusDto))
                                        finish()
                                    }
                                    "PENDING" -> {
//                                    PENDING
                                        mTransactionCallbacks?.onPending(Gson().toJson(transactionStatusDto))
                                        finish()
                                    }
                                }
                            }
                            catch (e:Exception)
                            {
                                UtilityKotlin.logData("Exception while response.isSuccessful -${e.localizedMessage}")
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                UtilityKotlin.logData("jObjError :$jObjError")
                            } catch (e: Exception) {
                                UtilityKotlin.showAlertPopUpAndFinish(
                                    this@GalileoSdkActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        UtilityKotlin.logData("Exception while handling response of trxn status API -${e.localizedMessage}")
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { UtilityKotlin.logData("Exception while getTransactionStatus - $it") }
        }
    }

    private fun verifyXsilverUser() {
        try {
            UtilityKotlin.logData("verifyXsilverUser API called")
            CoroutineScope(
                Dispatchers.IO + UtilityKotlin.onErrorCoroutineExceptionHandler(
                    this@GalileoSdkActivity
                )
            ).launch {
                val url = MAIN_BASE_URL + Constants.VERIFY_XSILVER_USER_URL

                val response = mainRepository.verifyXsilverUser(
                    url,
                    customerDto.phoneNumber,
                    customerDto.countryCode,
                    referenceNumber
                )
                withContext(Dispatchers.Main) {
                    try {
                        UtilityKotlin.logData("Response of check KYC required API - $response")
                        if (response.isSuccessful) {
                            val verifyXsilverUserDto = response.body()
                            if (!TextUtils.isEmpty(verifyXsilverUserDto?.kycLevel)) {
                                UtilityKotlin.logData("KYC is required. Asking for permissions.")
//                                do kyc
//                                ask camera and storage permission
                                checkCameraPermission()
                            } else {
                                UtilityKotlin.logData("KYC is complete so no permission is required.")
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                UtilityKotlin.logData("jObjError :$jObjError")
                            } catch (e: Exception) {
                                UtilityKotlin.showAlertPopUpAndFinish(
                                    this@GalileoSdkActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        UtilityKotlin.logData("Exception while handling response of trxn status API -${e.localizedMessage}")
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { UtilityKotlin.logData("Exception while getTransactionStatus - $it") }
        }
    }

}