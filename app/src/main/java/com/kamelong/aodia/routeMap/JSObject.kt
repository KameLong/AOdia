package com.kamelong.aodia.routeMap

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView


class JSObject(private val mWebView: WebView) {
    // popStateイベント発火したら呼ばれる
    @JavascriptInterface
    fun AOdiaOpenStation(str: String,str2:String): String {


        Log.d("xxx", "hook $str,$str2")
        return "false"
    }
    @JavascriptInterface
    fun AOdiaOpenRoute(str: String): String {
        Log.d("xxx", "hook $str")
        return "false"
    }
}