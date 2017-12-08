package com.kamelong.aodia.editStation

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.kamelong.aodia.R

/**
 * コピー、貼り付け、挿入、追加、削除を選択させるDialog
 * interfaceを設置すること
 */
class CopyPasteInsertAddDeleteDialog(context: Context,listener:CopyPasteInsertAddDeleteInterface) : AlertDialog(context) {
    val view = LayoutInflater.from(context).inflate(R.layout.cpiad_dialog, null)
    constructor(context: Context,listener:CopyPasteInsertAddDeleteInterface,enable:Boolean):this(context,listener){
        view.findViewById<Button>(R.id.pasteButton).isEnabled=enable
    }
    constructor(context: Context,listener:CopyPasteInsertAddDeleteInterface,copyEnable:Boolean,pasteEnable:Boolean):this(context,listener,pasteEnable){
        view.findViewById<Button>(R.id.copyButton).isEnabled=copyEnable
    }

    init{
        setView(view)
        view.findViewById<Button>(R.id.copyButton).setOnClickListener {
            listener.onClickCopyButton()
            dismiss()
        }
        view.findViewById<Button>(R.id.pasteButton).setOnClickListener {
            listener.onClickPasteButton()
            dismiss()
        }
        view.findViewById<Button>(R.id.insertButton).setOnClickListener {
            listener.onClickInsertButton()
            dismiss()
        }
        view.findViewById<Button>(R.id.addButton).setOnClickListener {
            listener.onClickAddButton()
            dismiss()
        }
        view.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            listener.onClickDeleteButton()
            dismiss()
        }
    }

    /**
     * 各種操作イベントを取得するためのinterface
     */
    interface CopyPasteInsertAddDeleteInterface{
        fun onClickCopyButton()
        fun onClickPasteButton()
        fun onClickInsertButton()
        fun onClickAddButton()
        fun onClickDeleteButton()
    }
}
