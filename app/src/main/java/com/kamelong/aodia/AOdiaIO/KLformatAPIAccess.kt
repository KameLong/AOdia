package com.kamelong.aodia.AOdiaIO

import android.view.View
import android.widget.TextView
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * このクラスはOuDiaデータベースのAPIをたたいて、結果を扱うためのクラスです。
 */
class KLformatAPIAccess {

    fun searchFromRouteID(routeID:Int):ArrayList<KLformatLineFile>{

        Thread(Runnable {
            try {
                var url = "https://kamelong.com/OuDiaDataBase/api/KLformatv1.php"
                url += "?routeID=$routeID"
                println("url:$url")
                val con = URL(url)
                val connection = con.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.doInput = true
                connection.doOutput = true
                connection.connect()
                if (connection.responseCode == HttpURLConnection.HTTP_OK) { // 通信に成功した
                    val json = JSONObject(BufferedReader(InputStreamReader(connection.inputStream,connection.contentEncoding?:"UTF-8")).readLine())
                    val jsonList=json.getJSONArray("result")
                    val result=ArrayList<KLformatLineFile>()
                    for(i in 0 until jsonList.length()){
                        val klFile=KLformatLineFile()
                        klFile.loadAPI(jsonList.getJSONObject(i))
                        result.add(klFile)
                    }
                    return result
                } else {
                    SDlog.toast("検索エラー" + connection.responseCode)
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }).start()


        var url = "https://kamelong.com/OuDiaDataBase/api/KLformatv1.php"
        url += "?routeID=$routeID"
        println("url:$url")
        val con = URL(url)
        val connection = con.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.doInput = true
        connection.doOutput = true
        connection.connect()

    }


}

/**
 * KLフォーマットの１ファイルに対応するオブジェクトクラス
 */
class KLformatLineFile{
    //所属route
    var routeID:Int=-1
    //データベース上のファイルID
    var fileID:Int=-1
    //投稿者
    var contributor:String=""
    //コメント
    var comment:String=""
    //改正日
    var revisionDay:String="1000-01-01"

    //OuDiaデータベースのAPI文字列を読み込む
    fun loadAPI(json: JSONObject){
        try{
            routeID=json.getString("routeID").toInt()
            fileID=json.getInt("fileID")
            contributor=json.getString("contributor")
            comment=json.getString("comment")
            revisionDay=json.getInt("year").toString()+"-"+"%02d".format(json.getInt("month"))+"-"+"%02d".format(json.getInt("month"))
        }catch (e:JSONException){
            SDlog.log(e)
        }
    }


}