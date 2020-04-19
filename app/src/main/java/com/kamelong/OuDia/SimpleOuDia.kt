package com.kamelong.OuDia

import com.kamelong.tool.SDlog
import com.kamelong.tool.ShiftJISBufferedReader
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

class SimpleOuDia(file: File) {
    var name = ""
    @JvmField
    var stationName = ArrayList<String>()

    @Throws(Exception::class)
    private fun loadShiftJis(file: File) {
        val br: BufferedReader = ShiftJISBufferedReader(InputStreamReader(FileInputStream(file), "Shift-JIS"))
        try {
            val nouse = br.readLine()
            loadDiaFile(br) //version info
        } catch (e: Exception) {
            throw e
        } finally {
            br.close()
        }
    }

    @Throws(Exception::class)
    private fun loadDiaFile(br: BufferedReader) {
        var line: String? = ""
        br.readLine() //Rosen.
        name = br.readLine().split("=").dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        line = br.readLine()
        while (line != null) {
            if (line == "Eki.") {
                line = br.readLine()
                stationName.add(line.split("=").dropLastWhile { it.isEmpty() }.toTypedArray()[1])
            }
            if (line == "Ressyasyubetsu.") {
                br.close()
                return
            }
            line = br.readLine()
        }
    }

    init {
        val br = BufferedReader(InputStreamReader(FileInputStream(file)))
        var version = ""
        version = try {
            br.readLine().split("=").dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        } catch (e: NullPointerException) {
            br.close()
            return
        }
        try {
            var v = 1.02
            try {
                v = version.substring(version.indexOf(".") + 1).toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (version.startsWith("OuDia.") || v < 1.03) {
                loadShiftJis(file)
            } else {
                loadDiaFile(br)
            }
        } catch (e: Exception) {
            SDlog.log(e)
        } finally {
            br.close()
        }
    }
}