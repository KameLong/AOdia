package com.kamelong.aodia

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.vending.billing.IInAppBillingService
import com.kamelong.tool.SDlog


/**
 * 支払情報を管理するクラス
 */
class Payment2(context: Context): PurchasesUpdatedListener {
    private val activity: Activity=context as Activity
    private var mService: IInAppBillingService? = null
    private val mServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            Log.d("mService", "Destroy")
            mService = null
        }

        override fun onServiceConnected(name: ComponentName,
                                        service: IBinder) {
            Log.d("mService", "Connected")
            mService = IInAppBillingService.Stub.asInterface(service)
        }
    }

    fun buy(id: String?) {
    }


    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        if (requestCode == 1001) {
//            val responseCode = data.getIntExtra("RESPONSE_CODE", 0)
//            val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    val jo = JSONObject(purchaseData)
//                    val productId = jo.getString("productId")
//                    alert("購入成功しました")
//                    // 購入成功後すぐに消費する
//// use();
//                } catch (e: JSONException) {
//                    alert("Failed to parse purchase data.")
//                    e.printStackTrace()
//                }
//            } else {
//                alert("課金に失敗しました")
//            }
//        }
    }

    private fun check() {
//        try { // 購入したものを確認する
//            val ownedItems = mService!!.getPurchases(3, activity.packageName, "inapp", null)
//            val response = ownedItems.getInt("RESPONSE_CODE")
//            if (response == 0) {
//                val ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
//                val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
//                val signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
//                val continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN")
//                for (i in purchaseDataList.indices) {
//                    val purchaseData = purchaseDataList[i]
//                    val `object` = JSONObject(purchaseData)
//                    val productId = `object`.getString("productId")
//                    val purchaseToken = `object`.getString("purchaseToken")
//                    alert("$productId,$purchaseToken")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    fun buyCheck(id: String): Boolean {
//        return false
//        try {
//            if (BuildConfig.BUILD_TYPE == "beta") {
//                return true
//            }
//            // 購入したものを確認する
//            println(mService)
//            val ownedItems = mService!!.getPurchases(3, activity.packageName, "inapp", null)
//            val response = ownedItems.getInt("RESPONSE_CODE")
//            if (response == 0) {
//                val ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST")
//                val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
//                val signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST")
//                val continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN")
//                for (i in purchaseDataList.indices) {
//                    val purchaseData = purchaseDataList[i]
//                    val json = JSONObject(purchaseData)
//                    val productId = json.getString("productId")
//                    val purchaseToken = json.getString("purchaseToken")
//                    if (productId == id) {
//                        return true
//                    }
//                }
//                return false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            alert("購入チェック時にエラーが発生しました")
//        }
        return false
    }

    fun close() {
        if (mService != null) {
            activity.unbindService(mServiceConn)
        }
    }

    lateinit private var billingClient: BillingClient

    init {
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    SDlog.log("OK");
                }
            }
            override fun onBillingServiceDisconnected() {
                SDlog.log("Disconnected");
            }
        })
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    fun querySkuDetails(id:String) {
        val skuList: MutableList<String> = ArrayList()
        val params = SkuDetailsParams.newBuilder()
        skuList.add(id)
        if(id=="010"){
            params.setSkusList(skuList).setType(SkuType.SUBS)

        }else{
            params.setSkusList(skuList).setType(SkuType.INAPP)
        }
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()
                    val responseCode: BillingResult = billingClient.launchBillingFlow(activity,flowParams)
                }
            }
            // Process the result.
        }
        // Process the result.
    }
}