package com.kamelong.aodia

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.DragEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.AOdiaIO.FileSelectFragment
import com.kamelong.aodia.AOdiaIO.ProgressDialog
import com.kamelong.aodia.EditTimeTable.LineTrainTimeFragment
import com.kamelong.aodia.detabase.DBHelper
import com.kamelong.aodia.diadataOld.AOdiaOperation
import com.kamelong.aodia.diagram.DiagramFragment
import com.kamelong.aodia.editStation.EditStationFragment
import com.kamelong.aodia.menu.MenuFragment
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.editTrainType.EditTrainTypeFragment
import com.kamelong.aodia.operation.OperationFragment
import com.kamelong.aodia.stationInfo.StationInfoFragment
import com.kamelong.aodia.stationInfo.StationInfoIndexFragment
import com.kamelong.aodia.timeTable.*

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

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
 */
/**
 * AOdiaのメインアクティビティー。
 * 起動時に呼ばれるアクティビティー。
 * 表示する各Fragmentページはアクティビティーが管理する。
 * アクティビティーはアプリ起動中は破棄されないため、アプリ起動中に失われたくないデータは全てアクティビティーが保持する。
 */
class AOdiaActivity : AppCompatActivity() {

    var payment: Payment? = null
        private set

    /**
     * ダイヤデータを保持する。
     * ダイヤファイルをクローズするとArrayListの順番を詰めずに空白にする
     */
    var diaFiles = ArrayList<AOdiaDiaFile>()
    /**
     * MenuにおけるdiaFilesの並び順を定義する、数値インデックス。
     */
    var diaFilesIndex = ArrayList<Int>()


    /**
     * 開いているFragmentを保存する
     * fragmentsはFragmentを削除すると順番を詰める
     */
    private val fragments = ArrayList<AOdiaFragmentInterface>()
    /**
     * 現在開いているFragmentのインデックス
     */
    private var fragmentIndex = -1

    /**
     * 使用するMenuFragment
     */
    private var menuFragment: MenuFragment? = null
    /**
     * ストレージのパミッションを得ているかを確認する
     * もし取得されていればtrueを返す
     * もし取得されていなければ、取得画面を表示する
     */
    private val storagePermission: Boolean
        get() {
            if (Build.VERSION.SDK_INT < 23) {
                return true
            } else {
                val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    val REQUEST_CODE = 1
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
                    return false
                } else {
                    return true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //あらかじめ購入処理関係を起動
        payment = Payment(this)
        SdLog.setActivity(this)
        //MainActivityに用いるContentViewを設定
        setContentView(R.layout.activity_main)
        setting()
        //Drawer初期化
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        drawer.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View?) {
            }

            override fun onDrawerOpened(drawerView: View?) {
                menuFragment!!.createMenu()
            }

        })
        val openDrawer = findViewById<Button>(R.id.Button2) as Button
        openDrawer.setOnClickListener {
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START)

            } else {
                drawer.closeDrawer(GravityCompat.START)
            }
        }
        //メニュー初期化
        menuFragment = MenuFragment()
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.menu, menuFragment)
        fragmentTransaction.commit()
        findViewById<View>(R.id.backFragment).setOnClickListener {
            fragmentIndex--
            println("fragment=" + fragmentIndex + ",max=" + fragments.size)
            if (fragmentIndex <= 0) {
                fragmentIndex = 0
            }
            selectFragment(fragments[fragmentIndex])
            findViewById<View>(R.id.backFragment).visibility = if (fragmentIndex == 0) View.INVISIBLE else View.VISIBLE
            findViewById<View>(R.id.proceedFragment).visibility = if (fragmentIndex < fragments.size - 1) View.VISIBLE else View.INVISIBLE
        }
        findViewById<View>(R.id.proceedFragment).setOnClickListener {
            fragmentIndex++
            println("fragment=" + fragmentIndex + ",max=" + fragments.size)
            if (fragmentIndex >= fragments.size) {
                fragmentIndex = fragments.size - 1
            }
            selectFragment(fragments[fragmentIndex])
            findViewById<View>(R.id.backFragment).visibility = if (fragmentIndex == 0) View.INVISIBLE else View.VISIBLE
            findViewById<View>(R.id.proceedFragment).visibility = if (fragmentIndex < fragments.size - 1) View.VISIBLE else View.INVISIBLE
        }
        findViewById<View>(R.id.killFragment).setOnClickListener { killFragment(fragmentIndex) }

        createSample()//sample.oudを作成する


        // ファイル関連付けで開くことをできるようにする
        if (Intent.ACTION_VIEW == intent.action) {
            val fname = intent.data.toString().split("//".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (fname.size == 2) {
                // 「file:」と「/～.gpx」に分けられたと仮定
                // 受け取ったファイル名をオープンして読み込んで表示する
                try {
                    val filePath = URLDecoder.decode(fname[1], "UTF-8")
                    //前回のデータが存在するときは、そのファイルを開く
                    println(filePath)
                    if (filePath.length > 0 && File(filePath).exists()) {
                        if (storagePermission) {
                            onFileSelect(File(filePath))
                            openDiaOrTimeFragment(0, 0, 0)
                            return
                        }
                    }
                } catch (e: Exception) {
                    SdLog.log(e)
                }

            }
        } else {
            // 通常起動の時
            //データベースより前回開いたデータを取得

            try {
                val preference = getSharedPreferences("AOdiaPreference", Context.MODE_PRIVATE)
                val filePath = preference.getString("RecentFilePath", "")
                val diaNum = preference.getInt("RecentDiaNum", 0)
                val direct = preference.getInt("RecentDirect", 0)
                //前回のデータが存在するときは、そのファイルを開く
                if (filePath!!.length > 0 && File(filePath).exists()) {
                    if (storagePermission) {
                        onFileSelect(File(filePath))
                        //                        openDiaOrTimeFragment(0,diaNum,direct);
                        return
                    }
                }
            } catch (e: Exception) {
                SdLog.log(e)
            }

        }
        //もし前回のデータが無ければsample.oudを開く
        //        diaFiles.add(new AOdiaDiaFile(this));
        // diaFilesIndex.add(0);
        //全開のデータがない場合はsampleを開いたうえでヘルプを開く
        openHelp()
    }

    public override fun onDestroy() {
        payment!!.close()
        super.onDestroy()
    }

    public override fun onStop() {
        super.onStop()
    }

    /**
     * sample.oudをdataフォルダにコピーする
     */
    private fun createSample() {
        val file = File(getExternalFilesDir(null), "sample.oud")
        if (file.exists()) {
            //return;
        }
        try {
            val assetManager = assets
            val `is` = assetManager.open("sample.oud")

            val os = FileOutputStream(file)
            val data = ByteArray(`is`.available())
            `is`.read(data)
            os.write(data)
            `is`.close()
            os.close()
        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing " + file, e)
        }

    }

    /**
     * 戻るボタンを押すとMenuDrawerを表示している際、閉じる
     *
     * 理想としては戻るボタンを押すと操作が一つ戻るようにしたいが現状そこまではできていない
     */
    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            findViewById<View>(R.id.backFragment).callOnClick()
        }
    }


    /**
     * 設定を押したときの処理
     */
    fun openSetting() {
        val preference = SettingFragment()
        openFragment(preference)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val preference = SettingFragment()
            try {
                val fragmentManager = fragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, preference)
                fragmentTransaction.addToBackStack(null) // 戻るボタンでreplace前に戻る
                fragmentTransaction.commit()
            } catch (e: Exception) {
                SdLog.log(e)
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (1 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // 拒否された
                Toast.makeText(this, "エラー：ファイルへのアクセスを許可してください", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * リクエストが返ってきたときに行う
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            0//設定が終わった時
            -> try {
                val spf = PreferenceManager.getDefaultSharedPreferences(this)
                val textSize = spf.getInt("textsize", 30)
                if (textSize > 0 && textSize < 100) {
                    KLView.textSize=textSize
                }
                openDiaOrTimeFragment(0, 0, 0)
            } catch (e: Exception) {
                SdLog.log(e)
            }

            1001//購入操作が終わった時
            -> {
                val responseCode = data.getIntExtra("RESPONSE_CODE", 0)
                val purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA")

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        val jo = JSONObject(purchaseData)
                        val productId = jo.getString("productId")
                        println(productId)
                        val spf = PreferenceManager.getDefaultSharedPreferences(this)
                        spf.edit().putBoolean("item001", true).commit()
                        onBackPressed()
                    } catch (e: JSONException) {

                        e.printStackTrace()
                    }

                } else {
                }
            }
        }
    }


    /**
     * ファイル一つが選択された時の処理。
     *
     * @param file
     */
    fun onFileSelect(file: File) {
        val filePath = file.path
        if (filePath.endsWith(".oud") || filePath.endsWith(".oud2")) {
            val diaFile = DiaFile(this, file)
            val db = DBHelper(this)
            db.addHistory(filePath)
            db.addNewFileToLineData(filePath, diaFile.getDiaNum())
            diaFiles.add(diaFile)
            diaFilesIndex.add(0, diaFiles.size - 1)
            menuFragment!!.createMenu()
            openLineTimeTable(diaFilesIndex[0],0,0)
        }
    }

    /**
     * 複数ファイルが選択されたときの処理
     * @param files
     */
    fun onFileListSelect(files: Array<File>) {
        for (i in files.indices) {
            onFileSelect(files[files.size - 1 - i])
        }
    }

    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private fun openFragment(fragment: AOdiaFragmentInterface) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment.fragment)
        fragmentTransaction.commit()
        fragmentIndex++
        fragments.add(fragmentIndex, fragment)
        findViewById<View>(R.id.backFragment).visibility = if (fragmentIndex == 0) View.INVISIBLE else View.VISIBLE
        findViewById<View>(R.id.proceedFragment).visibility = if (fragmentIndex < fragments.size - 1) View.VISIBLE else View.INVISIBLE


    }

    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private fun selectFragment(fragment: AOdiaFragmentInterface) {
        //もしメニューが開いていたら閉じる
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment.fragment)
        fragmentTransaction.commit()

    }

    /**
     * DiaFileを閉じる
     * DiaFileを閉じるときはリソースの解放とそのDiaFileを使用していたfragmentを閉じる動作が必要
     * @param index
     * @param menuIndex
     */
    fun killDiaFile(index: Int, menuIndex: Int) {
        var i = 0
        while (i < fragments.size) {
            try {
                val fragment = fragments[i] as AOdiaFragment
                if (fragment.diaFile === diaFiles[index]) {
                    killFragment(i)
                    i--
                }
            } catch (e: Exception) {
            }

            i++
        }
        diaFilesIndex.removeAt(menuIndex)
        menuFragment!!.createMenu()

    }

    /**
     * 指定されたIndexのFragmentをkillする
     * @param index
     */
    private fun killFragment(index: Int) {
        if (index < 0) {
            return
        }
        try {
            fragments.removeAt(index)
            if (fragmentIndex >= index) {
                fragmentIndex--
            }
            findViewById<View>(R.id.backFragment).visibility = if (fragmentIndex == 0) View.INVISIBLE else View.VISIBLE
            findViewById<View>(R.id.proceedFragment).visibility = if (fragmentIndex < fragments.size - 1) View.VISIBLE else View.INVISIBLE
            if (fragmentIndex == -1) {
                if (fragments.size > 0) {
                    fragmentIndex++
                    selectFragment(fragments[0])

                } else {
                    openHelp()
                }
            } else {
                selectFragment(fragments[fragmentIndex])
            }
        } catch (e: Exception) {
            SdLog.log(e)
        }

    }

    /**
     */
    fun killFragment(fragment: AOdiaFragment) {
        killFragment(fragments.indexOf(fragment))

    }


    /**
     * ファイルダイアログを開く
     */
    fun openFileDialog() {
        if (storagePermission) {
            val fragment = FileSelectFragment()
            openFragment(fragment)
        }
    }

    /**
     * ヘルプを開く
     */
    fun openHelp() {
        val helpFragment = HelpFragment()
        openFragment(helpFragment)
    }

    /**
     * コメント画面を開く。
     */
    fun openComment(fileNum: Int) {
        val comment = CommentFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        comment.arguments = args
        openFragment(comment)
    }

    /**
     * 駅時刻の目次を開く。
     */
    fun openStationTimeTableIndex(fileNum: Int) {
        val fragment = StationInfoIndexFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        fragment.arguments = args
        openFragment(fragment)
    }

    /**
     * 駅時刻表を開く。
     */
    fun openStationTimeTable(fileNum: Int, diaNum: Int, direct: Int, station: Int) {
        val fragment = StationInfoFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        args.putInt("diaN", diaNum)
        args.putInt("direct", direct)
        args.putInt("station", station)
        fragment.arguments = args
        openFragment(fragment)
    }

    /**
     * ダイヤグラムを開く
     */
    fun openDiagram(fileNum: Int, diaNum: Int) {

        val fragment = DiagramFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        args.putInt("diaN", diaNum)
        fragment.arguments = args
        openFragment(fragment)


    }

    fun openDiaOrTimeFragment(fileNum: Int, diaNum: Int, direct: Int) {
        try {
            if (direct < 2) {
                openLineTimeTable(fileNum, diaNum, direct)
            } else {
                openDiagram(fileNum, diaNum)
            }
        } catch (e: Exception) {
            SdLog.log(e)
        }

    }

    /**
     * 路線時刻表を開く
     */
    private fun openLineTimeTable(fileNum: Int, diaNum: Int, direct: Int) {
        openLineTimeTable(fileNum, diaNum, direct, -1)
    }

    /**
     * 路線時刻表を開いたのち
     * 指定列車まで移動する
     */
    fun openLineTimeTable(fileNum: Int, diaNum: Int, direct: Int, train: Int) {
        try {

            val fragment :Fragment= LineTrainTimeFragment()
            val args = Bundle()
            args.putInt("fileNum", fileNum)
            args.putInt("diaNum", diaNum)
            args.putInt("direction", direct)
            fragment.arguments = args
            openFragment(fragment as AOdiaFragmentInterface)
        } catch (e: Exception) {
            SdLog.log(e)
        }

    }

    /**
     * 運用表を開く
     */
    fun openOperationFragment(fileNum: Int, diaNum: Int, operationNum: Int) {
        try {
            val fragment = OperationFragment()
            val args = Bundle()
            args.putInt("fileNum", fileNum)
            args.putInt("diaNum", diaNum)
            fragment.arguments = args
            openFragment(fragment)
        } catch (e: Exception) {
            SdLog.log(e)
        }

    }


    /**
     * diaFileが指定されたとき、そのdiaFileがどのDiaFIleIndexに対応するかを調べる
     * @param diaFile
     * @return
     */
    private fun getDiaFileIndexNumByFragment(diaFile: AOdiaDiaFile): Int {
        for (i in diaFilesIndex.indices) {
            if (diaFiles[diaFilesIndex[i]] === diaFile) {
                return i
            }
        }
        return 0

    }

    /**
     * Fragmentを表す文字列からFragmentを生成し、contentViewにセットする。
     * @param str
     * @return
     */
    private fun openFragment(str: String): Boolean {
        try {
            val strs = str.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (strs[0] == DBHelper.HELP) {
                openHelp()
                return true
            }
            if (strs[0] == DBHelper.COMMENT) {
                openComment(Integer.parseInt(strs[1]))
                return true
            }
            if (strs[0] == DBHelper.STATION_TIME_INDEX) {
                openStationTimeTableIndex(Integer.parseInt(strs[1]))
                return true
            }
            if (strs[0] == DBHelper.STATION_TIME_TABLE) {
                openStationTimeTable(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]), Integer.parseInt(strs[4]))
                return true
            }
            if (strs[0] == DBHelper.DIAGRAM) {
                openDiaOrTimeFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 2)
                return true
            }
            if (strs[0] == DBHelper.LINE_TIME_TABLE) {
                openDiaOrTimeFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]))
                return true
            }
            return false
        } catch (e: Exception) {
            SdLog.log(e)
            return false
        }

    }


    /**
     * DiaFileをメニュー上部に移動
     * メニューの並び順はdiaFilesIndexに依存するので。
     * diaFilesIndexのみ変更すればよい
     * @param menuIndex
     */
    fun upDiaFile(menuIndex: Int) {
        if (menuIndex == 0) return
        diaFilesIndex.add(menuIndex - 1, diaFilesIndex[menuIndex])
        diaFilesIndex.removeAt(menuIndex + 1)
        menuFragment!!.createMenu()

    }

    /**
     * データベースを初期化する
     */
    fun resetDetabase() {

        val alertDlg = AlertDialog.Builder(this)
        alertDlg.setTitle("確認")
        alertDlg.setMessage("アプリ内部データを初期化します（oudファイルは消去されません）")
        alertDlg.setPositiveButton(
                "OK"
        ) { dialog, which ->
            deleteDatabase(DBHelper.DETABASE_NAME)
            val intent = Intent()
            intent.setClass(this@AOdiaActivity, AOdiaActivity::class.java)
            startActivity(intent)
        }
        alertDlg.setNegativeButton(
                "Cancel"
        ) { dialog, which ->
            // Cancel ボタンクリック処理
        }

        alertDlg.create().show()
    }


    /**
     * 設定ファイルを閉じたとき
     *
     */
    fun onCloseSetting() {
        val intent = Intent()
        intent.setClassName(this, AOdiaActivity::class.java.name)
        startActivity(intent)
    }

    /**
     * 設定を反映させる
     */
    private fun setting() {
        try {
            val scale = resources.displayMetrics.density
            val spf = PreferenceManager.getDefaultSharedPreferences(this)
            val textSize = Integer.parseInt(spf.getString("textsize2", "30"))
            if (textSize > 0 && textSize < 100) {
                KLView.textSize=(textSize / 3.0f * scale).toInt()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveFile() {
/*        if (!payment!!.buyCheck("001")) {
            payment!!.buy("001")
            return
        }
        */

        try {
            val saveFile = fragments[fragments.size - 1].diaFile
            FileSaveDialog(this,saveFile).show()
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)

        } catch (e: Exception) {
            SdLog.log(e)
            Toast.makeText(this, "ファイルを保存時にエラーが発生しました。", Toast.LENGTH_LONG).show()
        }
    }


    fun openEditStation(fileNum: Int) {
        val fragment = EditStationFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        fragment.arguments = args
        openFragment(fragment)
    }
    fun openEditTrainType(fileNum: Int) {
        val fragment = EditTrainTypeFragment()
        val args = Bundle()
        args.putInt("fileNum", fileNum)
        fragment.arguments = args
        openFragment(fragment)
    }
    fun addDiaFile(diaFile:AOdiaDiaFile){
        diaFilesIndex.add(0,diaFiles.size)
        diaFiles.add(diaFile)
        menuFragment!!.createMenu()
        openLineTimeTable(diaFilesIndex[0],0,0)
    }
    fun closeMenu(){
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)

    }


}
