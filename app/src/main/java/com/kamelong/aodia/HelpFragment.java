package com.kamelong.aodia;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.kamelong.aodia.web.UrlOpenDialog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kame on 2016/12/20.
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
public class HelpFragment extends KLFragment {
    public HelpFragment() {
        super();
    }

    WebView helpView;
    Handler handler;

    // ダウンロード変数
    DownloadManager dl_manager;
    DownloadManager.Query query;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
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
//        helpView.loadUrl("http://kamelong.com/OuDiaDataBase/");
        helpView.loadUrl("http://kamelong.com/aodia/help-v1.1.html");

        helpView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype, long contentLength) {
                oudiaUrlLoad(url);
            }
        });



//        helpView.loadUrl("http://kamelong.com/aodia/help-v1.1.html");
        return helpView;
    }
    private void oudiaUrlLoad(String url){
        if(1==1) {
            return;
        }
        if(url.endsWith("oud")||url.endsWith("oud2")) {
            UrlOpenDialog dialog=new UrlOpenDialog(getActivity(),url,helpView.getUrl());
        }

    }

}
