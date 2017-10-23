package com.kamelong.aodia;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kame on 2017/03/20.
 */

public class Payment {
    private Activity activity;
    IInAppBillingService mService;
    ServiceConnection         mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    public Payment(Context context){
        activity=(Activity)context;
                Intent serviceIntent =
                        new Intent("com.android.vending.billing.InAppBillingService.BIND");
                serviceIntent.setPackage("com.android.vending");
                activity.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }
    public void buy(String id) {
        try {
            System.out.println(mService);
            // 購入リクエストの送信
            // item001 はGoogle Play Developer Consoleで作成した値を使う
            Bundle buyIntentBundle = mService.getBuyIntent(3, activity.getPackageName(), id, "inapp", "sdlkjfhiauiaushdli");
            // レスポンスコードを取得する
            int response = buyIntentBundle.getInt("RESPONSE_CODE");
            // 購入可能
            // BILLING_RESPONSE_RESULT_OK
            if(response == 0) {
                // 購入フローを開始する
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                // 購入トランザクションの完了
                activity.startIntentSenderForResult(
                        pendingIntent.getIntentSender(),
                        1001,
                        new Intent(),
                        Integer.valueOf(0),
                        Integer.valueOf(0),
                        Integer.valueOf(0));
            }
            // BILLING_RESPONSE_RESULT_USER_CANCELED
            else if(response == 1) {
                alert("購入できませんでした");
            }
            // BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED
            else if(response == 7){
                alert("既に購入している");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void alert(String str){
        Toast.makeText(activity,str,Toast.LENGTH_LONG).show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String productId = jo.getString("productId");

                    alert("購入成功しました");
                    // 購入成功後すぐに消費する
                    // use();
                }
                catch (JSONException e) {
                    alert("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            } else {
                alert("課金に失敗しました");
            }
        }
    }
    private void check() {
        try {
            // 購入したものを確認する
            Bundle ownedItems = mService.getPurchases(3, activity.getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    JSONObject object = new JSONObject(purchaseData);
                    String productId = object.getString("productId");
                    String purchaseToken = object.getString("purchaseToken");

                    alert(productId + "," + purchaseToken);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public boolean buyCheck(String id){
        if(true){
            return true;
        }
        try {
            if(BuildConfig.BUILD_TYPE.equals("beta")){
                return true;
            }
            // 購入したものを確認する
            Bundle ownedItems = mService.getPurchases(3, activity.getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    JSONObject object = new JSONObject(purchaseData);
                    String productId = object.getString("productId");
                    String purchaseToken = object.getString("purchaseToken");
                    if(productId.equals(id)){
                        return true;
                    }
                }
                return false;
            }
        }catch(Exception e) {
            e.printStackTrace();
//            alert("購入チェック時にエラーが発生しました");
        }
        return false;
    }
    public void close(){
        if (mService != null) {
            activity.unbindService(mServiceConn);
        }
    }
    public void use() {
        try {
            // 購入したものを全て消費する
            Bundle ownedItems = mService.getPurchases(3, activity.getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    JSONObject object = new JSONObject(purchaseData);
                    String productId = object.getString("productId");
                    String purchaseToken = object.getString("purchaseToken");

                    // 消費する
                    response = mService.consumePurchase(3, activity.getPackageName(), purchaseToken);

                    // 正常終了
                    if(response == 0) {
                        alert(productId + "を消費しました。");
                    } else {
                        alert(purchaseData);
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    }
