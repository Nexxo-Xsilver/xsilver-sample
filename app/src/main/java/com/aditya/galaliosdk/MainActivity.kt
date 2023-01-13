package com.aditya.galaliosdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.nexxo.galileosdk.CustomerDto
import com.nexxo.galileosdk.GalileoSdkActivity
import com.nexxo.galileosdk.SdkCallbacks

class MainActivity : AppCompatActivity(),SdkCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            var btn = findViewById<AppCompatButton>(R.id.btn_go_to_sdk)
            btn.setOnClickListener {
                var customerDto = CustomerDto()
                customerDto.email = "userEmail"
                customerDto.mobileNumber = "9876543210"
                customerDto.walletAddress=""
                customerDto.environment="test"
               var intent = Intent(this,GalileoSdkActivity::class.java)
                intent.putExtra("context",this)
                intent.putExtra("customerData",customerDto)
                startActivity(intent)
            }
        }
       catch (e:Exception)
       {
           Log.v("logData",e.localizedMessage)
       }
    }

/*    fun initinterface(){
        try {
            sdkCallbacks = object : SdkCallbacks {
                override fun onSuccess(successResponse: String) {
                    Log.v("logData", successResponse)
                }

                override fun onFailure(failureResponse: String) {
                    Log.v("logData", failureResponse)
                }

                override fun onPending(pendingResponse: String) {
                    Log.v("logData", pendingResponse)
                }

                override fun onUserCancel() {
                    Log.v("logData", "Canceled by user")
                }

            }
        }
        catch (e:Exception)
        {
            Log.v("logData",e.localizedMessage)
        }
    }*/

    override fun onSuccess(successResponse: String) {
        Log.v("logData", successResponse)
    }

    override fun onFailure(failureResponse: String) {
        Log.v("logData", failureResponse)
    }

    override fun onPending(pendingResponse: String) {
        Log.v("logData", pendingResponse)
    }

    override fun onUserCancel() {
        Log.v("logData", "Canceled by user")
    }

}