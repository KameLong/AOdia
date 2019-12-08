package com.kamelong.aodia.routeMap

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.kamelong.aodia.AOdia
import com.kamelong.aodia.AOdiaIO.FileSelectFromRouteID
import com.kamelong.aodia.AOdiaIO.OnFileSelect
import com.kamelong.aodia.MainActivity
import java.io.File


class JSObject(private val mWebView: WebView,private val aodia:AOdia) {
    // popStateイベント発火したら呼ばれる
    @JavascriptInterface
    fun AOdiaOpenStation(routeID: String,stationID:String): String {

        val dialog=FileSelectFromRouteID(mWebView.context as MainActivity,routeID,object:OnFileSelect{
            override fun OnFileSelect(filePath: String) {
                aodia.openFile(File(filePath))
            }
        })
        dialog.show()

        return "false"
    }
    @JavascriptInterface
    fun AOdiaOpenRoute(value: String): String {
        var routeID=value

        val dialog=FileSelectFromRouteID(mWebView.context as MainActivity,routeID,object:OnFileSelect{
            override fun OnFileSelect(filePath: String) {
                aodia.openFile(File(filePath))

            }
        })
        dialog.show()

        return "false"
    }
}