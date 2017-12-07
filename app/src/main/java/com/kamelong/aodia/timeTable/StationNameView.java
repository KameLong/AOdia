package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaStation;
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

    /**
     * Created by Owner on 2016/11/21.
     */

    class StationNameView extends KLView {
        private AOdiaDiaFile dia;
        private AOdiaStation station;
        private int direct;

        StationNameView(Context context, AOdiaDiaFile diaFile, int d) {
            super(context);
            dia = diaFile;
            direct = d;
        }

        public void onDraw(Canvas canvas) {
            int startLine = (int) Companion.getBlackPaint().getTextSize();
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (spf.getBoolean("trainName", false)) {
                int startX = (int) ((getWidth() - Companion.getTextPaint().getTextSize()) / 2);
                canvas.drawText("列", startX, Companion.getTextPaint().getTextSize() * 1.2f, Companion.getTextPaint());
                canvas.drawText("車", startX, Companion.getTextPaint().getTextSize() * 2.5f, Companion.getTextPaint());
                canvas.drawText("名", startX, Companion.getTextPaint().getTextSize() * 3.8f, Companion.getTextPaint());
                canvas.drawLine(0, Companion.getTextSize() * 7.9f, getWidth(), Companion.getTextSize() * 7.9f, Companion.getBlackPaint());
                startLine = (int) Companion.getBlackPaint().getTextSize() * 9;
            }

            startLine += 1.2 * Companion.getTextSize();
            canvas.drawText("始発", 1, startLine - 0.8f * Companion.getTextSize(), Companion.getBlackPaint());

            canvas.drawLine(0, startLine, getWidth(), startLine, Companion.getBlackPaint());
            startLine += 1 * Companion.getTextSize();
            for (int i = 0; i < station.getStationNum(); i++) {
                int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;

                switch (station.border(stationNumber - direct)) {
                    case 0:
                        switch (station.getTimeShow(stationNumber, direct)) {
                            case 0:
                                //発のみ
                                canvas.drawText(station.getName(stationNumber), 1, startLine, Companion.getBlackPaint());
                                startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                                break;
                            case 1:
                                //発着
//                            canvas.drawText(station.getName(stationNumber), 1,startLine+(int)(textPaint.getTextSize()*4/6), blackBig);
//                            startLine=startLine+(int)(textPaint.getTextSize()*13/6);
                                canvas.drawText(station.getName(stationNumber), 1, startLine, Companion.getBlackPaint());
                                startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                                canvas.drawLine(0, startLine - (int) (Companion.getTextPaint().getTextSize() * 5 / 6), this.getWidth() - 1, startLine - (int) (Companion.getTextPaint().getTextSize() * 5 / 6), Companion.getBlackPaint());
                                startLine = startLine + (int) (Companion.getTextPaint().getTextSize() * 1 / 6);
                                canvas.drawText("発着番線", 1, startLine, Companion.getBlackPaint());

                                startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                                canvas.drawLine(0, startLine - (int) (Companion.getTextPaint().getTextSize() * 5 / 6), this.getWidth() - 1, startLine - (int) (Companion.getTextPaint().getTextSize() * 5 / 6), Companion.getBlackPaint());
                                startLine = startLine + (int) (Companion.getTextPaint().getTextSize() * 1 / 6);
                                canvas.drawText(station.getName(stationNumber), 1, startLine, Companion.getBlackPaint());
                                startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                                break;
                            case 2:
                                //着のみ
                                canvas.drawText(station.getName(stationNumber), 1, startLine, Companion.getBlackPaint());
                                startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                                break;
                        }
                        break;
                    case 1:
                        //着のみ
                        canvas.drawText(station.getName(stationNumber), 1, startLine, Companion.getBlackPaint());
                        startLine = startLine + (int) Companion.getBlackPaint().getTextSize();
                        canvas.drawLine(0, startLine - (int) (Companion.getTextPaint().getTextSize() * 2 / 3), this.getWidth() - 1, startLine - (int) (Companion.getTextPaint().getTextSize() * 2 / 3), Companion.getBlackBPaint());
                        startLine = startLine + (int) (Companion.getTextPaint().getTextSize() * 1 / 3);
                        break;
                    case 2:
                        //発着
                        canvas.drawText(station.getName(stationNumber), 1, startLine + (int) (Companion.getTextPaint().getTextSize() * 4 / 6), blackBig);
                        startLine = startLine + (int) (Companion.getTextPaint().getTextSize() * 13 / 6);
                        i++;
                        break;
                }
            }
            canvas.drawLine(0, startLine - Companion.getTextSize() * 0.8f, getWidth(), startLine - Companion.getTextSize() * 0.8f, Companion.getBlackPaint());
            startLine += 1.2 * Companion.getTextSize();
            canvas.drawText("終着", 1, startLine - 0.6f * Companion.getTextSize(), Companion.getBlackPaint());

            startLine += 1 * Companion.getTextSize();

            if (spf.getBoolean("remark", false)) {
                int startY = (int) (this.getHeight() - 10.5f * Companion.getTextSize());
                canvas.drawLine(0, startY, getWidth(), startY, Companion.getBlackBBPaint());
                canvas.drawText("運用番号", 0, startY + 1.0f * Companion.getTextSize(), Companion.getBlackPaint());
                startY += 1.2f * Companion.getTextSize();
                canvas.drawLine(0, startY, getWidth(), startY, Companion.getBlackPaint());
                int startX = (getWidth() - Companion.getTextSize()) / 2;
                startY = startY + (int) (Companion.getTextSize() * 1.5f);
                canvas.drawText("備", startX, startY, Companion.getBlackPaint());
                startY = startY + (int) (Companion.getTextSize() * 1.5f);
                canvas.drawText("考", startX, startY, Companion.getBlackPaint());
            }
        }

        public int getYsize() {
            int result = Companion.getTextSize();
            for (int i = 0; i < station.getStationNum(); i++) {
                int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
                switch (station.border(stationNumber - direct)) {
                    case 0:
                        switch (station.getTimeShow(stationNumber, direct)) {
                            case 0:
                                //発のみ
                                result = result + Companion.getTextSize();
                                break;
                            case 1:
                                //発着
                                result = result + (Companion.getTextSize() * 7 / 6);
                                result = result + (Companion.getTextSize() * 7 / 6);
                                result = result + Companion.getTextSize();
                                break;
                            case 2:
                                //着のみ
                                result = result + Companion.getTextSize();
                                break;
                        }
                        break;
                    case 1:
                        //着のみ
                        result = result + Companion.getTextSize();
                        result = result + (Companion.getTextSize() / 3);
                        break;
                    case 2:
                        //発着
                        result = result + (Companion.getTextSize() * 7 / 6);
                        result = result + Companion.getTextSize();
                        i++;
                        break;
                }


            }
            result = result - (Companion.getTextSize() * 4 / 6);
            result += Companion.getTextSize() * 4.4f;

            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (spf.getBoolean("remark", false)) {
                result = result + (int) (Companion.getTextSize() * 10.6f);
            }
            if (spf.getBoolean("trainName", false)) {
                result = result + (int) (Companion.getTextSize() * 8f);

            }

            return result;
        }

        public int getStationFromY(int posY) {
            int linePos = 0;

            for (int i = 0; i < station.getStationNum(); i++) {
                int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
                switch (station.border(stationNumber - direct)) {
                    case 0:
                        switch (station.getTimeShow(stationNumber, direct)) {
                            case 0:
                                //発のみ
                                linePos = linePos + Companion.getTextSize();
                                break;
                            case 1:
                                //発着
                                linePos = linePos + (Companion.getTextSize() * 7 / 6);
                                linePos = linePos + Companion.getTextSize();
                                break;
                            case 2:
                                //着のみ
                                linePos = linePos + Companion.getTextSize();
                                break;
                        }
                        break;
                    case 1:
                        //着のみ
                        linePos = linePos + Companion.getTextSize();
                        linePos = linePos + (Companion.getTextSize() / 3);
                        break;
                    case 2:
                        //発着
                        linePos = linePos + (Companion.getTextSize() * 7 / 6);
                        linePos = linePos + Companion.getTextSize();
                        i++;
                        break;
                }
                if (posY < linePos) {
                    if (stationNumber < 0) {
                        stationNumber = 0;
                    }
                    return stationNumber;
                }

            }

            return -1;
        }

        protected int getXsize() {
            return (int) (Companion.getTextPaint().getTextSize() * 5);
        }
    }
