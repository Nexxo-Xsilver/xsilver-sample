package com.aditya.galileoSdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.nexxo.galileosdk.model.CustomerDto
import com.nexxo.galileosdk.activities.GalileoSdkActivity
import com.nexxo.galileosdk.interfaces.SdkCallbacks

class MainActivity : AppCompatActivity(), SdkCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            var btn = findViewById<AppCompatButton>(R.id.btn_go_to_sdk)
            btn.setOnClickListener {
                var customerDto = CustomerDto()
                customerDto.email = "ansh@mailinator.com"
                customerDto.phoneNumber = "9354487741"
                customerDto.countryCode = "+91"
                customerDto.walletAddress="0xaEf6A0E48914499D6F7909a8DDb0Dda82b1Ecdb4"
                customerDto.environment="test"
                customerDto.referralCode="nexxoio"
               var intent = Intent(this, GalileoSdkActivity::class.java)
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

    override fun onSuccess(successResponse: String) {
        Log.v("logData", "onSuccess - $successResponse")
        toastMsg("Success!!!!\n$successResponse")
    }

    override fun onFailure(failureResponse: String) {
        Log.v("logData", "onFailure - $failureResponse")
        toastMsg("Failure!!!!\n$failureResponse")
    }

    override fun onPending(pendingResponse: String) {
        Log.v("logData", "onPending -$pendingResponse")
        toastMsg("Pending!!!!\n$pendingResponse")
    }

    override fun onUserCancel() {
        Log.v("logData", "Canceled by user")
        toastMsg("Canceled by user")
    }

    private fun toastMsg(msg:String)
    {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }
}