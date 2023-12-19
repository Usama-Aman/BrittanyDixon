package com.cp.brittany.dixon.network

import org.json.JSONObject

interface ResponseCallBack {

    fun onSuccess(jsonObject: JSONObject, tag: String)
    fun onError(error:   String, tag: String)

}