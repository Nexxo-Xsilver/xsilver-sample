package com.nexxo.galileosdk.interfaces

import java.io.Serializable

interface TransactionCallbacks: Serializable {

    public fun onSuccess(successResponse: String)

    public fun onFailure(failureResponse: String)

    public fun onPending(pendingResponse: String)

    public fun onUserCancel()
}