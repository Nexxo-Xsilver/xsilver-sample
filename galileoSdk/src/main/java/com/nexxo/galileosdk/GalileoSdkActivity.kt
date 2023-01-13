package com.nexxo.galileosdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity

class GalileoSdkActivity: AppCompatActivity() {
    private var mUrlString = ""
    private lateinit var mWebView: WebView
    private var mSdkCallbacks: SdkCallbacks?=null
    private lateinit var customerDto:CustomerDto
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

                    if(url.toString().contains("widget/v3/user-data"))
                    {
                        Handler().postDelayed(
                            {
                                mSdkCallbacks?.onSuccess(url.toString())
                            },100
                        )
                        finish()
                    }

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
}