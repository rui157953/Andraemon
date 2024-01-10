package com.rui157953.andrarmon.tool.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL


object NetworkHelper {
    private const val TAG = "NetworkHelper"
    private var countDownTimer: CountDownTimer? = null

    fun waitForNetworkStable(context: Context, onStable: () -> Unit) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "等待网络稳定...")
            }

            override fun onFinish() {
                if (isWifiConnected(context)) {
                    onStable()
                }
            }
        }
        countDownTimer?.start()
    }

    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return null != networkInfo && networkInfo.isConnected
    }

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 x次, 如果 x 次都不成功，视为该地址不可用
     * @return true是可以上网，false是不能上网
     */
    private val url: URL = URL("https://www.baidu.com")
    private var con: HttpURLConnection? = null
    private var state = -1
    fun isOnline(): Boolean {
        // Android 4.0 之后不能在主线程中请求HTTP请求
        var counts = 0
        var online = true
        while (counts < 2) {
            try {
                con = url.openConnection() as HttpURLConnection
                state = con!!.responseCode
                Log.d(TAG, "isOnline counts: $counts=state: $state")
                if (state == 200) {
                    online = true
                }
                break
            } catch (ex: Exception) {
                online = false
                counts++
                Log.e(TAG, "isOnline 网络不可用，连接第 $counts 次")
                continue
            }
        }
        return online
    }
}