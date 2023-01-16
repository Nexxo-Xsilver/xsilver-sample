package com.nexxo.galileosdk

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.HashMap


class GalileoSdkActivity: AppCompatActivity() {
    private var mUrlString = ""
    private lateinit var mWebView: WebView
    private var mSdkCallbacks: SdkCallbacks?=null
    private lateinit var customerDto:CustomerDto
    private var mPermissionRequest: PermissionRequest? = null
    private val REQUEST_CAMERA_PERMISSION = 1
    private val PERM_CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val PERM_LOCATION = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galileo_sdk)
        intItView()
    }

    private fun intItView() {
        mSdkCallbacks = intent.getSerializableExtra("context") as SdkCallbacks
        customerDto = intent.getParcelableExtra<CustomerDto>("customerData")!!
        mWebView = findViewById(R.id.webview)
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.builtInZoomControls = false
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.databaseEnabled = true
        mWebView.settings.setGeolocationEnabled(true)
        mWebView.webChromeClient = object : WebChromeClient(){
            override fun onPermissionRequest(request: PermissionRequest?) {
                super.onPermissionRequest(request)
                mPermissionRequest = request!!
                if(checkCameraPermission())
                {
                    Log.v("logData","Camera permission granted")
                }
                else
                {
                    Log.v("logData","Nothing")
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)

            }
        }

        mUrlString = "https://sandbox.xsilver.com/widget/nexxoio"
        initWebView()
    }

    private fun userClickTopCancel() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (mWebView.canGoBack()) {
                mWebView.goBack()
            }
            else
            {
                mSdkCallbacks?.onUserCancel()
            }
        }
        catch (e:Exception)
        {
            Log.v("logData","exception while onBackPressed = ${e.localizedMessage}")
        }
    }

    private fun initWebView() {
        try{
            mWebView.loadUrl(mUrlString)
            mWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let { view?.loadUrl(it) }
                    Log.v("logData","shouldOverrideUrlLoading")
                    return true
                }
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.v("logData","onPageStarted - url - ${url.toString()}")

                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.v("logData","onPageFinished - url - ${url.toString()}")
                    /*if(url.toString().contains("widget/v3/user-data"))
                    {
                        Handler().postDelayed(
                            {
                                mSdkCallbacks?.onSuccess(url.toString())
                            },100
                        )
                        finish()
                    }*/

                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.v("logData","onReceivedError - ${error.toString()}")
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    Log.v("logData","onReceivedHttpError - ${errorResponse.toString()}")
                }
            }
        }
        catch (e:Exception)
        {
            Log.v("logData","Exception while initWebView - ${e.localizedMessage}")
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
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++

                    }
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.v("logData","Camera permission granted")
                        mWebView.reload()
                    } else {
                        Log.v("logData","Camera permission is not granted ask again ")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_CONTACTS
                            )
                        ) {
                            showDialogOK(
                                "Permission required for run this app"
                            ) { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkCameraPermission()
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                        showSnackBar(
                                            "In order to work properly " + getString(R.string.app_name) + " App needs some permission.",
                                            "GO TO Settings"
                                        )
                                    }
                                }
                            }
                        } else {
                            showSnackBar(
                                "In order to work properly " + getString(R.string.app_name) + " App needs some permission.",
                                "GO TO Settings"
                            )
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }
    private fun checkCameraPermission():Boolean {
        try {
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@GalileoSdkActivity,
                    arrayOf(permission),
                    REQUEST_CAMERA_PERMISSION
                )
                return false
            }
            return true
        }
        catch (e:Exception)
        {
            Log.v("logData","Exception while checkCameraPermission - ${e.localizedMessage}")
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
            Log.v("logData","Exception while showSnackBar - ${e.localizedMessage}")
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
}