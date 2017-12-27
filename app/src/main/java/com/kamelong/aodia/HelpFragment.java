package com.kamelong.aodia;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kamelong.aodia.web.UrlOpenDialog;

/**
 * ヘルプを表示するためのFragment
 */
/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */
public class HelpFragment extends AOdiaFragment {
    public HelpFragment() {
        super();
    }

    private WebView helpView;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(true);
        handler=new Handler();
        helpView = new WebView(getActivity());
        helpView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.endsWith("oud")||url.endsWith("oud2")) {
                    oudiaUrlLoad(url);
                    return true;
                }
                return false;
            }


        });
        helpView.setWebChromeClient(new WebChromeClient(){

        });
        helpView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(helpView.canGoBack())helpView.goBack();
                    return true;
                }
                return false;
            }
        });
        helpView.loadUrl("http://kamelong.com/aodia/help-v2.3.html");

        helpView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype, long contentLength) {
                oudiaUrlLoad(url);
            }
        });
        return helpView;
    }
    private void oudiaUrlLoad(String url){
        if(1==1) {
            return;
        }
        if(url.endsWith("oud")||url.endsWith("oud2")) {
            UrlOpenDialog dialog=new UrlOpenDialog(getAodiaActivity(),url,helpView.getUrl());
        }

    }
    @Override
    public String fragmentName(){
        return "AOdiaヘルプv2.3";
    }
    @Override
    public String fragmentHash() {
        return "help";
    }
}
