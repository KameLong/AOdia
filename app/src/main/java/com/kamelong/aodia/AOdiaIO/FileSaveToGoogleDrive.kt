package com.kamelong.aodia.AOdiaIO

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import java.io.File
import java.util.*

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 端末内にファイルを保存するためのView
 */
class FileSaveToGoogleDrive constructor(context: Context, attr: AttributeSet? = null) : LinearLayout(context, attr), OpenDirectory {
    var currentDirectoryPath = ""
    fun setLineFile(lineFile:LineFile){
        println("path:" + lineFile.filePath)
        try {
            if(lineFile.filePath==""){
                (findViewById<View>(R.id.fileName) as EditText).setText(lineFile.name);
            }else{
                (findViewById<View>(R.id.fileName) as EditText).setText(lineFile.filePath.substring(lineFile.filePath.lastIndexOf("/") + 1, lineFile.filePath.lastIndexOf(".")))
            }
        } catch (e: Exception) {
            SDlog.log(e)
        }
        val saveStyle = findViewById<RadioGroup>(R.id.savestyle)
        saveStyle.setOnCheckedChangeListener { radioGroup, i ->
            if (i != R.id.oud2) {
                AlertDialog.Builder(context)
                        .setTitle("警告")
                        .setMessage("oud形式で保存すると、発着番線、路線外発着情報など、一部の情報が失われることがあります")
                        .setPositiveButton("OK") { dialog: DialogInterface?, which: Int -> }
                        .show()
            }
        }


        if (lineFile.filePath.endsWith("oud2")) {
            saveStyle.check(R.id.oud2)
        } else {
            saveStyle.check(R.id.oud)
        }
        findViewById<View>(R.id.saveButton).setOnClickListener(OnClickListener {
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("/")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("\\")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains(":")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("*")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("?")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("!")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("<")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains(">")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。")
                return@OnClickListener
            }
            if ((findViewById<View>(R.id.fileName) as EditText).text.toString().contains("|")) {
                SDlog.toast("ファイル名に使用できない文字が含まれています。")
                return@OnClickListener
            }
            var savePath = currentDirectoryPath + "/" + (findViewById<View>(R.id.fileName) as EditText).text
            savePath += if (saveStyle.checkedRadioButtonId == R.id.oud2) {
                ".oud2"
            } else {
                ".oud"
            }
            try {
                val file = File(context.getCacheDir().toString() + "/temp.oud")
                file.delete()
                lineFile.saveToOuDiaFile(context.getCacheDir().toString() + "/temp.oud")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/oud"
            intent.setPackage("com.google.android.apps.docs")
//                    intent.setClassName("com.google.android.apps.docs","com.google.android.apps.docs.shareitem.UploadSharedItemActivity");
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            //                    intent.setClassName("com.google.android.apps.docs","com.google.android.apps.docs.shareitem.UploadSharedItemActivity");
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            intent.putExtra(Intent.EXTRA_SUBJECT, File(lineFile.filePath).name)
            intent.putExtra(Intent.EXTRA_TEXT, "hoge")
            val uri = FileProvider.getUriForFile(context, "com.kamelong.aodia.fileprovider", File(context.getCacheDir().toString() + "/temp.oud"))
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            (context as MainActivity).startActivityForResult(intent, 1)



        })


    }

    /**
     * LineFileが設定されたら、そのLineFileのfilePathに保存できるよう、ディレクトリ移動する
     * @param lineFile 保存するLineFile
     */


    /**
     * フォルダ内内ファイルリストを作成する
     * このメソッドを呼び出すとtab内のListViewを更新する。
     * @param directorypath 表示したいディレクトリ
     */
    override fun openDirectory(directorypath: String) {
        try {
            val file = File(directorypath)
            if (file.isDirectory) {
                currentDirectoryPath = directorypath
                val fileListView = findViewById<ListView>(R.id.fileList)
                val adapter = FileListAdapter(context, file.path, this)
                fileListView.adapter = adapter
                (findViewById<View>(R.id.pathView) as TextView).text = file.path
                fileListView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
                    try {
                        openDirectory(adapter.getItem(position).path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (file.exists()) {
                (findViewById<View>(R.id.fileName) as EditText).setText(file.name.substring(0, file.name.lastIndexOf(".")))
            } else {
                MakeNewDirectoryDialog(context, currentDirectoryPath, this).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.filesave_googledrive, this)
        val saveStyle = findViewById<RadioGroup>(R.id.savestyle)
        saveStyle.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.oud -> (findViewById<View>(R.id.textView4) as TextView).text = ".oud"
                R.id.oud2 -> (findViewById<View>(R.id.textView4) as TextView).text = ".oud2"
            }
        }
    }
}