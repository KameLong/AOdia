package com.kamelong.aodia;

import android.app.Dialog;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kamelong.aodia.R;

public class InfoDialog extends Dialog {
    public InfoDialog(Context context){
        super(context);
        setContentView(R.layout.info);
        WebView webView=findViewById(R.id.infoWeb);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }


        });
        webView.setWebChromeClient(new WebChromeClient(){

        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://kamelong.com/aodia/AOdiaInfo.html");
    }
}
