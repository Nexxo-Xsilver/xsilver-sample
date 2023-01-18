package com.nexxo.galileosdk.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.android.volley.*
import kotlinx.coroutines.CoroutineExceptionHandler
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

class UtilityKotlin {

    companion object {
        /*
* isOnline - Check if there is a NetworkConnection
* @return boolean
*/
        private fun isNetworkAvailable(context: Context): Boolean {
            var isAvailable = false
            try {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = Objects.requireNonNull(cm).activeNetworkInfo
                isAvailable = netInfo != null && netInfo.isConnected
            } catch (er: java.lang.Exception) {
                logData(er.message.toString())
                isAvailable = false
            }
            return isAvailable
        }

        fun logData(logData: String) {
            Log.v("logData", logData);
        }

        fun onErrorCoroutineExceptionHandler(mContext: Context): CoroutineExceptionHandler {
            val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                onErrorResponse(throwable, mContext)
            }
            return exceptionHandler
        }

        private fun onErrorResponse(error: Throwable, mContext: Context) {
            Handler(Looper.getMainLooper()).post {
                try {
                    if (!isNetworkAvailable(mContext)) {
                        showPopUp(mContext, "No internet connection. Please try again later.")
                    } else {
                        val message = error.message
                        logData("onErrorResponse " + error.message)
                        if (error is NoConnectionError) {
                            val cm = mContext
                                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                            var activeNetwork: NetworkInfo? = null
                            if (cm != null) {
                                activeNetwork = cm.activeNetworkInfo
                            }
                            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
                                showPopUp(mContext, "Server is not connected to internet.")
                            } else {
                                showPopUp(mContext, "Your device is not connected to internet.")
                            }
                        } else if (error is NetworkError || error.cause is ConnectException
                            || (error.cause!!.message != null
                                    && error.cause!!.message!!.contains("connection"))
                        ) {
                            showPopUp(mContext, "Your device is not connected to internet.")
                        } else if (error.cause is MalformedURLException) {
                            showPopUp(mContext, "Bad Request")
                        } else if (error is ParseError || error.cause is IllegalStateException
                            || error.cause is JSONException
                            || error.cause is XmlPullParserException
                        ) {
                            showPopUp(mContext, "Parse Error (because of invalid json or xml")
                        } else if (error.cause is OutOfMemoryError) {
                            showPopUp(mContext, "Out Of Memory Error.")
                        } else if (error is AuthFailureError) {
                            showPopUp(mContext, " server couldn't find the authenticated request.")
                        } else if (error is ServerError || error.cause is ServerError) {
                            showPopUp(mContext, "Server is not responding.")
                        } else if (error is TimeoutError || error.cause is SocketTimeoutException
                            || error.cause is ConnectTimeoutException
                            || error.cause is SocketException
                            || (error.cause!!.message != null
                                    && error.cause!!.message!!.contains("Connection timed out"))
                        ) {
                            showPopUp(mContext, "Connection timeout error")
                        } else {
                            if (TextUtils.isEmpty(message)) {
                                showPopUp(mContext, "An unknown error occurred.")
                            } else {
                                showPopUp(mContext, message)
                            }
                        }
                    }
                } catch (er: java.lang.Exception) {
                    if (er.message != null) {
                        showPopUp(mContext, er.message)
                    }
                    er.message?.let { logData(it) }
                }
            }
        }

        fun showPopUp(context: Context, str: String?) {
            val alertDialog2 = AlertDialog.Builder(context)

            // Setting Dialog Title
            alertDialog2.setTitle("Error")
            alertDialog2.setCancelable(false)

            // Setting Dialog Message
            alertDialog2.setMessage(str)

            // Setting Positive "Yes" Btn
            alertDialog2.setPositiveButton(
                "OK"
            ) { dialog, which ->
                dialog.dismiss()
            }

            // Showing Alert Dialog
            alertDialog2.show()
        }

        /* Method for showing the Alert popup */
        fun showAlertPopUpAndFinish(context: Context, string: String?) {
            val activity: Activity = context as Activity
            if (!activity.isFinishing){
                Handler(Looper.getMainLooper()).post {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Error")
                    alertDialog.setMessage(string)
                    alertDialog.setCancelable(false)
                    alertDialog.setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        dialog.dismiss()
                        (context as Activity).finish()
                    }
                    alertDialog.show()
                }
            }
        }

    }
}
