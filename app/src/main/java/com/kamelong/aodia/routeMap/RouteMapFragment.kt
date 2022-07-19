package com.kamelong.aodia.routeMap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdiaFragmentCustom
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog

class RouteMapFragment: AOdiaFragmentCustom() {
    private var webView:WebView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        webView = WebView(activity as FragmentActivity)

        val ws=webView!!.settings
        ws.javaScriptEnabled=true

        webView?.getSettings()?.setJavaScriptEnabled(true)
        if(webView != null){
            webView!!.addJavascriptInterface(JSObject(webView!!,aOdia),"AOdia");
            webView!!.loadUrl("https://kamelong.com/test/index.html")
            return webView
        }
        return View(activity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getHash(): String {
        return "RouteMapFragment"
    }


    override fun getName(): String {
        return "路線図"
    }

    override fun getLineFile(): LineFile? {
        return null
    }
}