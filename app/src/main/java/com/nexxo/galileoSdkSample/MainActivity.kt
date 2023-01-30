package com.nexxo.galileoSdkSample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.nexxo.galileosdk.activities.GalileoSdkActivity
import com.nexxo.galileosdk.interfaces.TransactionCallbacks
import com.nexxo.galileosdk.model.CustomerDto

class MainActivity : AppCompatActivity(), TransactionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val btn = findViewById<AppCompatButton>(R.id.btn_go_to_sdk)
            btn.setOnClickListener {
                var customerDto = CustomerDto()
                customerDto.email = "ansh@mailinator.com" //customer's email address
                customerDto.phoneNumber = "9354487741" //customer's phone number
                customerDto.countryCode = "+91" //customer's ISD code
                customerDto.walletAddress =
                    "0xaEf6A0E48914499D6F7909a8DDb0Dda82b1Ecdb4" //customer's crypto wallet address
                customerDto.environment = "test" //environment test or live
                customerDto.referralCode = "nexxoio" //partner's referral code
                val intent = Intent(this, GalileoSdkActivity::class.java)
                intent.putExtra("context", this)
                intent.putExtra("customerData", customerDto)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.v("logData", e.localizedMessage)
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

    private fun toastMsg(msg: String) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }
}