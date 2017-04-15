package com.fc2.web.kamelong.aodia.netgram;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fc2.web.kamelong.aodia.detabase.DBHelper;
import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kame on 2017/01/12.
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
 */
public class NetgramActivity extends Activity {
    WebView webView;
    RelativeLayout layout;
    FloatingActionButton fab;
    Handler handler;
    int dataNumber;
    int folderNumber;
    String cookie;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout= new RelativeLayout(this);
        webView=new WebView(this);
        handler=new Handler();
        fab=new FloatingActionButton(this);
        fab.setImageResource(R.drawable.ic_file_download);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postData="generate=";
                webView.postUrl("http://netgram.me/member/folder/"+folderNumber+"/dia/"+dataNumber+"/detail",postData.getBytes());
                webView.loadUrl("http://netgram.me/member/folder/"+folderNumber+"/dia/"+dataNumber+"/detail");
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        try {
                            URL dataURL1 =new URL( "http://netgram.me/member/folder/diameta/" + dataNumber+ "_2_meta.json");
                            URL dataURL2 =new URL( "http://netgram.me/member/folder/diameta/" + dataNumber+ "_1_meta.json");
                            URL dataURL3 =new URL( "http://netgram.me/member/folder/"+folderNumber+"/dia/"+dataNumber+"/2/list");
                            HttpURLConnection download1 =  (HttpURLConnection) dataURL1.openConnection();
                            HttpURLConnection download2 =  (HttpURLConnection) dataURL2.openConnection();
                            HttpURLConnection download3 =  (HttpURLConnection) dataURL3.openConnection();
                            download3.setRequestProperty("Cookie", cookie);

                            download1.connect();
                            download2.connect();
                            download3.connect();
                            final int status1 = download1.getResponseCode();
                            final int status2 = download1.getResponseCode();
                            final int status3 = download1.getResponseCode();
                            if (status1 == HttpURLConnection.HTTP_OK&&status2 == HttpURLConnection.HTTP_OK&&status3 == HttpURLConnection.HTTP_OK) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NetgramActivity.this,"ダウンロードを開始しました", Toast.LENGTH_SHORT).show();
                                        fab.setVisibility(View.INVISIBLE);
                                    }
                                });
                                BufferedReader br=new BufferedReader(new InputStreamReader(download3.getInputStream()));
                                String line=br.readLine();
                                JSONArray jsonStationArary = new JSONArray();



                                while(line!=null){
                                    if(line.contains("insert-data-title")){
                                        line=br.readLine();
                                        while(true){
                                            line=br.readLine();
                                            line=line.replace("<",",");
                                            line=line.replace(">",",");
                                            String[] lines=line.split(",",-1);
                                            if(lines.length<5){
                                                break;
                                            }
                                            JSONObject jsonStation = new JSONObject();
                                            jsonStation.put("name",lines[4]);
                                            if(lines[1].contains("full-col")){
                                                jsonStation.put("mainStation","true");
                                            }else{
                                                jsonStation.put("mainStation","false");
                                            }
                                            jsonStationArary.put(jsonStation);
                                        }
                                    }
                                    line=br.readLine();
                                }
                                String startStation=jsonStationArary.getJSONObject(0).getString("name");
                                String endStation=jsonStationArary.getJSONObject(jsonStationArary.length()-1).getString("name");
                                JSONObject data=new JSONObject();
                                data.put("url","http://netgram.me/member/folder/"+folderNumber+"/dia/"+dataNumber);
                                data.put("station",jsonStationArary);
                                br=new BufferedReader(new InputStreamReader(download1.getInputStream()));
                                JSONArray downTime = new JSONArray(br.readLine());
                               data.put("down",downTime);
                                br=new BufferedReader(new InputStreamReader(download2.getInputStream()));
                                JSONArray upTime = new JSONArray(br.readLine());
                               data.put("up",upTime);

                                String fileName=dataNumber+"-("+startStation+"~"+endStation+").json";
                                final String filePath =getExternalFilesDir(null).getPath() + "/" +fileName;
                                // jsonファイル出力
                                File file = new File(filePath);
                                FileWriter filewriter;

                                filewriter = new FileWriter(file);
                                BufferedWriter bw = new BufferedWriter(filewriter);
                                PrintWriter pw = new PrintWriter(bw);
                                pw.write(data.toString());
                                pw.close();
                                DBHelper db = new DBHelper(NetgramActivity.this);
                                db.setRecentFile(filePath,0,0);
                                Intent intent=new Intent();
                                intent.setClass(NetgramActivity.this,MainActivity.class);
                                startActivity(intent);


                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NetgramActivity.this, "保存に成功しました", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NetgramActivity.this,"ダウンロードに失敗しました", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        layout.addView(webView);
        RelativeLayout.LayoutParams fabParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabParams.setMargins(0,0,50,50);

        layout.addView(fab,fabParams);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            public void onPageFinished (WebView view, String url){
                super.onPageFinished(view,url);
                try {
                    System.out.println(url);
                    fab.setVisibility(View.INVISIBLE);
                    if (url.equals("http://netgram.me/member/")) {
                        SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("access", "true");
                        editor.commit();
                        setContentView(layout);

                        Intent i = getIntent();
                        String goUrl= i.getStringExtra("url");
                        if(goUrl.length()>0){
                            webView.loadUrl(goUrl);
                        }
                    }
                    dataNumber = getDataNumber(url);
                    folderNumber = getFolderNumber(url);
                    if (dataNumber > 0&&folderNumber>0&&url.endsWith("detail")) {
                        fab.setVisibility(View.VISIBLE);
                        System.out.println(CookieManager.getInstance().getCookie(url));
                        cookie =  CookieManager.getInstance().getCookie(url);
                    }
                    if (url.equals("http://netgram.me/logout")) {
                        SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("access", "false");
                        editor.commit();
                    }
                    if (url.equals("http://netgram.me/member/login")) {
                        SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("access", "false");
                        editor.commit();
                        NetgramActivity.this.setContentView(R.layout.netgram);
                        setLogin(true);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
        System.out.println(preference.getAll());
        if(preference.getString("access","null").equals("true")){
            String postData = "username="+preference.getString("id","null")+"&password="+preference.getString("pass","null");
            webView.postUrl("http://netgram.me/member/login", postData.getBytes());
            setContentView(layout);
            return;
        }
        setContentView(R.layout.netgram);
        setLogin(false);

    }
    private void setLogin(boolean logout){
        SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
        Button button=(Button) findViewById(R.id.button);
        if(logout){
            ((TextView)findViewById(R.id.textView6)).setText("ログアウトしているか\nユーザー名・パスワードが異なります。");
        }
        EditText userName = (EditText)findViewById(R.id.userName);
        EditText password= (EditText)findViewById(R.id.password);
        userName.setText(preference.getString("id",""));
        password.setText(preference.getString("pass",""));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userName = (EditText)findViewById(R.id.userName);
                EditText password= (EditText)findViewById(R.id.password);
                Log.d("button","clicked");

                SharedPreferences preference = getSharedPreferences("netgram", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("id", userName.getText().toString());
                editor.putString("pass", password.getText().toString());
                editor.commit();
                String postData = "username="+userName.getText().toString()+"&password="+password.getText().toString();
                webView.postUrl("http://netgram.me/member/login", postData.getBytes());
                //NetgramActivity.this.setContentView(webView);
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 端末の戻るボタンでブラウザバック
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,  event);
    }
    public int getDataNumber(String url){
        String[] urls=url.split("/");
        if(urls.length>7&&urls[6].equals("dia")){
            System.out.println(urls[7]);
            return Integer.parseInt(urls[7]);
        }
        return -1;
    }
    public int getFolderNumber(String url){
        String[] urls=url.split("/");
        if(urls.length>7&&urls[4].equals("folder")){
            System.out.println(urls[5]);
            return Integer.parseInt(urls[5]);
        }
        return -1;
    }
}
