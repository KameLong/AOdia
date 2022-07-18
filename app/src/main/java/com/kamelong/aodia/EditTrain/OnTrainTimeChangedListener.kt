package com.kamelong.aodia.EditTrain

/**
 * 時刻が変更させた時のイベント
 */
interface OnTimeChangeListener {
    fun onTimeChanged(station: Int, AD: Int)
}