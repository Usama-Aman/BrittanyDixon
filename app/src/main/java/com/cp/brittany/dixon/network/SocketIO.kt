package com.cp.brittany.dixon.network


import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject

class SocketIO {


    companion object {
        const val socketBaseUrl = "http://178.128.29.7:5359/?"
    }

    private val opts = IO.Options()
    private var mSocket: Socket

    init {
//        opts.query = "id=${AppController.userDetails?.id}&user_type=user"
        mSocket = IO.socket(socketBaseUrl, opts)
    }

    fun on(name: String, callback: (JSONObject) -> Unit) {

        if (!mSocket.connected()) {
            mSocket.connect()
            Log.d("Socket is connected : ", "${mSocket.connected()}")
        }
        Log.d("Socket is connected : ", "${mSocket.connected()}")
        mSocket.on(name) { args -> callback(JSONObject(args.first().toString())) }
    }

    fun emit(name: String, data: JSONObject) {
        if (!mSocket.connected()) {
            Log.d("Socket is connected : ", "${mSocket.connected()}")
            mSocket.connect()
        }
        Log.d("Socket is connected : ", "${mSocket.connected()}")
        mSocket.emit(name, data)
    }

    fun isConnected() = mSocket.connected()

    fun offSocketEvent(event: String) {
        mSocket.off(event)
    }

    fun disconnect() {
        mSocket.off()
        mSocket.close()
        mSocket.disconnect()
        Log.e("Socket", "Disconnected")
    }
}