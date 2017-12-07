package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Time;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaStation;
import com.kamelong.aodia.diadataOld.AOdiaTrain;
public class TrainTimeView extends KLView {
    private AOdiaDiaFile dia;
    private AOdiaTrain train;
    private AOdiaStation station;
    private JPTI jpti;
    private int direct;
    private boolean secondFrag = false;
    private boolean remarkFrag = false;
    private boolean showPassFrag = false;
    private boolean showTrainName = false;
    //private static final String NOSERVICE_STRING ="∙ ∙";
    //    private static final String NOSERVICE_STRING =":  :";
    private static final String NOVIA_STRING = "| |";
    private static final String PASS_STRING = "レ";
    private static final String NODATA_STRING = "○";

    private TrainTimeView(Context context) {
        super(context);
    }

    TrainTimeView(Context context, TimeTableFragment timeTableFragment, AOdiaDiaFile diaFile, AOdiaTrain t, int d) {
        this(context);
        dia = diaFile;
        train = t;
        direct = d;

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag = spf.getBoolean("secondSystem", secondFrag);
        remarkFrag = spf.getBoolean("remark", remarkFrag);
        showPassFrag = spf.getBoolean("showPass", showPassFrag);
        showTrainName = spf.getBoolean("trainName", showTrainName);


    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long time = System.currentTimeMillis();
        drawTime(canvas);
        if (remarkFrag) {
            drawRemark(canvas);
        }
    }

    private void drawTime(Canvas canvas) {
        int startLine = Companion.getTextSize();
        if (showTrainName) {
            canvas.drawLine(0, Companion.getTextPaint().getTextSize() * 7.9f, getWidth(), Companion.getTextPaint().getTextSize() * 7.9f, Companion.getBlackPaint());
            drawTrainName(canvas);
            startLine += 8 * Companion.getTextSize();
        }


        /**
         int startLine2 = textSize;
         for (int i = 0; i < station.getStationNum(); i++) {
         drawText(canvas,"aaaa", 1, startLine2, textPaint, true);
         startLine2+=textSize;
         }
         if(true){
         return;
         }
         **/
        try {
            Companion.getTextPaint().setColor(train.getTrainType().getTextColor().getAndroidColor());

            drawNoService(canvas, startLine, Companion.getTextPaint());
            startLine += Companion.getTextSize();
            drawNoService(canvas, startLine, Companion.getTextPaint());
            startLine += Companion.getTextSize() * 0.2f;
            canvas.drawLine(0, startLine, getWidth(), startLine, Companion.getBlackPaint());
            startLine += Companion.getTextSize() * 1f;

            for (int i = 0; i < station.getStationNum(); i++) {
                int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
                int border = station.border(stationNumber - direct);
                int timeShow = station.getTimeShow(stationNumber, direct);
                if (border == 0 && timeShow == 0) {
                    //発のみ
                    if (station.bigStation(stationNumber) && (train.getStopType(stationNumber) == 0 && (station.border(stationNumber - 1 + direct) == 0) && stationNumber != 0)) {
                        drawText(canvas, "- - - - - - -", 1, startLine, Companion.getTextPaint(), true);
                    } else {
                        drawDepartString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                    }
                    startLine = startLine + Companion.getTextSize();
                }
                if (timeShow == 1) {
                    //発着
                    int backwordStation = stationNumber + (direct * 2 - 1);
                    if (backwordStation < 0 || backwordStation >= station.getStationNum()) {
                        if (train.getTime(stationNumber).getArrivalTime() >= 0) {
                            drawArriveString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                        } else {
                            drawNoService(canvas, startLine, Companion.getTextPaint());
                        }
                    } else {
                        switch (train.getStopType(backwordStation)) {
                            case 0:
                                if (train.getTime(backwordStation) != null && train.getTime(stationNumber).getArrivalTime() >= 0) {
                                    drawArriveString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                                } else {
                                    drawNoService(canvas, startLine, Companion.getTextPaint());
                                }
                                break;
                            case 3:
                                if (train.getTime(backwordStation) != null && train.getTime(stationNumber).getArrivalTime() >= 0) {
                                    drawArriveString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                                } else {
                                    drawNoVia(canvas, startLine, Companion.getTextPaint());
                                }
                                break;
                            default:
                                drawArriveString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);

                                break;
                        }
                    }
                    canvas.drawLine(0, startLine + (int) (Companion.getTextSize() / 5.0f), this.getWidth() - 1, startLine + (int) (Companion.getTextSize() / 5.0f), Companion.getBlackPaint());
                    startLine = startLine + (Companion.getTextSize() * 7 / 6);
                    drawStopNum(canvas, startLine, Companion.getTextPaint(), 88);
                    canvas.drawLine(0, startLine + (int) (Companion.getTextSize() / 5.0f), this.getWidth() - 1, startLine + (int) (Companion.getTextSize() / 5.0f), Companion.getBlackPaint());
                    startLine = startLine + (Companion.getTextSize() * 7 / 6);

                    Companion.getTextPaint().setColor(train.getTrainType().getTextColor().getAndroidColor());

                    int forwordStation = stationNumber + (1 - direct * 2);
                    if (forwordStation < 0 || forwordStation >= station.getStationNum()) {
                        if (train.getTime(stationNumber).getDepartureTime() >= 0) {
                            drawDepartString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                        } else {
                            drawNoService(canvas, startLine, Companion.getTextPaint());
                        }
                    } else {
                        switch (train.getStopType(forwordStation)) {
                            case 0:
                                if (train.getTime(forwordStation) != null && train.getTime(stationNumber).getDepartureTime() >= 0) {
                                    drawDepartString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                                } else {
                                    drawNoService(canvas, startLine, Companion.getTextPaint());
                                }
                                break;
                            case 3:
                                if (train.getTime(forwordStation) != null && train.getTime(stationNumber).getDepartureTime() >= 0) {
                                    drawDepartString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                                } else {
                                    drawNoVia(canvas, startLine, Companion.getTextPaint());
                                }
                                break;
                            default:
                                drawDepartString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                                break;
                        }
                    }
                    startLine = startLine + Companion.getTextSize();
                }
                if (border != 0 || timeShow == 2) {

                    //着のみ
                    drawArriveString(canvas, startLine, Companion.getTextPaint(), train, stationNumber, direct);
                    startLine = startLine + Companion.getTextSize();
                    if (border == 2) {
                        canvas.drawLine(0, startLine - (Companion.getTextSize() * 4 / 5), this.getWidth() - 1, startLine - (Companion.getTextSize() * 4 / 5), Companion.getBlackPaint());
                        startLine = startLine + (Companion.getTextSize() / 6);
                    }
                    if (border == 1) {
                        canvas.drawLine(0, startLine - (Companion.getTextSize() * 2 / 3), this.getWidth() - 1, startLine - (Companion.getTextSize() * 2 / 3), Companion.getBlackBPaint());
                        startLine = startLine + (Companion.getTextSize() / 3);
                    }

                }
            }
            canvas.drawLine(0, startLine - Companion.getTextSize() * 0.8f, getWidth(), startLine - Companion.getTextSize() * 0.8f, Companion.getBlackPaint());
            startLine += Companion.getTextSize() * 0.2f;
            drawNoService(canvas, startLine, Companion.getTextPaint());
            startLine += Companion.getTextSize();
            drawNoService(canvas, startLine, Companion.getTextPaint());
            canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight(), Companion.getBlackPaint());

        } catch (Exception e) {
            SdLog.log(e);
        }
    }

    private void drawTrainName(Canvas canvas) {
        try {
            int heightSpace = 12;
            String value = train.getName();
            value = value.replace('ー', '｜');
            value = value.replace('（', '(');
            value = value.replace('）', ')');
            value = value.replace('「', '┐');
            value = value.replace('」', '└');

            char[] str = value.toCharArray();
            int lineNum = 1;
            int space = heightSpace;
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    lineNum++;
                }
                if (!charIsEng(str[i])) {
                    space--;
                }
                space--;
            }
            space = heightSpace;
            int startX = (int) ((getWidth() - lineNum * Companion.getTextSize() * 1.2f) / 2 + (lineNum - 1) * Companion.getTextSize() * 1.2f);
            int startY = 0;
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int) (Companion.getTextSize() * 1.2f);
                    startY = 0;
                }
                if (charIsEng(str[i])) {
                    space--;
                    canvas.save();
                    canvas.rotate(90, 0, 0);
                    drawText(canvas, String.valueOf(str[i]), startY + 2, (int) (-startX - (Companion.getTextSize() * 0.2f)), Companion.getTextPaint(), false);
                    canvas.restore();
                    startY = startY + (int) Companion.getTextPaint().measureText(String.valueOf(str[i]));
                } else {
                    space = space - 2;
                    startY = startY + Companion.getTextSize();
                    drawText(canvas, String.valueOf(str[i]), startX, startY, Companion.getTextPaint(), false);
                }

            }
        } catch (Exception e) {
            SdLog.log(e);
        }


        float textSize = Companion.getTextPaint().getTextSize();
        if (train.getCount().length() > 0) {
            String gousuu = train.getCount().substring(0, train.getCount().length() - 1);
            drawText(canvas, gousuu, 0, (int) (textSize * 7.0f), Companion.getTextPaint(), true);
            drawText(canvas, "号", 0, (int) (textSize * 8.0f), Companion.getTextPaint(), true);
        }
    }

    private void drawRemark(Canvas canvas) {
        try {
            int startY = (int) (this.getHeight() - 10.5f * Companion.getTextSize());
            canvas.drawLine(0, startY, getWidth(), startY, Companion.getBlackBBPaint());

            if (train.getOperation() != null) {
                if (train.getOperation().getNumber() >= 0) {
                    drawText(canvas, train.getOperation().getNumber() + "", 1, (int) (this.getHeight() - 9.4f * Companion.getTextSize()), Companion.getTextPaint(), true);
                }

            }

            canvas.drawLine(0, startY + 1.2f * Companion.getTextSize(), getWidth(), startY + 1.2f * Companion.getTextSize(), Companion.getBlackPaint());

            int heightSpace = 18;

            String value = train.getText();
            value = value.replace('ー', '｜');
            value = value.replace('（', '(');
            value = value.replace('）', ')');
            value = value.replace('「', '┐');
            value = value.replace('」', '└');
            char[] str = value.toCharArray();
            int lineNum = 1;
            int space = heightSpace;
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    lineNum++;
                }
                if (!charIsEng(str[i])) {
                    space--;
                }
                space--;
            }
            space = heightSpace;
            int startX = (int) ((getWidth() - lineNum * Companion.getTextSize() * 1.2f) / 2 + (lineNum - 1) * Companion.getTextSize() * 1.2f);
            startY = (int) (this.getHeight() - 9.1f * Companion.getTextSize());
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int) (Companion.getTextSize() * 1.2f);
                    startY = (int) (this.getHeight() - 9.1f * Companion.getTextSize());
                }
                if (charIsEng(str[i])) {
                    space--;
                    canvas.rotate(90);
                    canvas.drawText(String.valueOf(str[i]), startY, -startX - (Companion.getTextSize() * 0.2f), Companion.getTextPaint());
                    canvas.rotate(-90);
                    startY = startY + (int) Companion.getTextPaint().measureText(String.valueOf(str[i]));
                } else {
                    space = space - 2;
                    startY = startY + Companion.getTextSize();
                    canvas.drawText(String.valueOf(str[i]), startX, startY, Companion.getTextPaint());
                }

            }
        } catch (Exception e) {
            SdLog.log(e);
        }


    }

    private boolean charIsEng(char c) {
        return c < 256;
    }

    public int getYsize() {
        int result = (int) (Companion.getTextSize() * 3.2f);
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
                    //
                    result = result + Companion.getTextSize();
                    result = result + (Companion.getTextSize() * 7 / 6);
                    i++;
                    break;
            }


        }
        result = result - (Companion.getTextSize() * 4 / 6);

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        result += Companion.getTextSize() * 2.2f;
        if (spf.getBoolean("remark", false)) {
            result = result + (int) (Companion.getTextSize() * 10.6f);
        }
        if (showTrainName) {
            result = result + (int) (Companion.getTextSize() * 8f);
        }
        return result;
    }

    public int getXsize() {
        int lineTextSize = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("lineTimetableWidth", "4")) + 1;
        if (secondFrag) {
            lineTextSize += 3;
        }
        return (int) (Companion.getTextSize() * lineTextSize * 0.5f);
    }

    private String getDepartureTime(AOdiaTrain train, int station, int direct) {
        Time time = train.getTime(station);
        try {
            switch (train.getStopType(station)) {
                case AOdiaTrain.NOSERVICE:
                    return "::";
                case AOdiaTrain.NOVIA:
                    return NOVIA_STRING;
                case AOdiaTrain.PASS:
                    if (showPassFrag && (time.getArrivalTime() >= 0 || time.getDepartureTime() >= 0)) {
                        Companion.getTextPaint().setColor(Color.GRAY);
                    } else {
                        return PASS_STRING;
                    }
            }
            if (time.getDepartureTime() < 0) {
                if (time.getArrivalTime() < 0) {
                    return "○";
                }
                return getArriveTime(train, station, direct);
            }
            int second = time.getDepartureTime();
            int ss = second % 60;
            second = (second - ss) / 60;
            int mm = second % 60;
            second = (second - mm) / 60;
            int hh = second % 60;
            hh = hh % 24;
            String result = "";
            if (secondFrag) {
                result = hh + String.format("%02d", mm) + "-" + String.format("%02d", ss);
            } else {
                result = hh + String.format("%02d", mm);
            }
            return result;
        } catch (Exception e) {
            SdLog.log(e);
        }
        return "○";
    }

    private String getArriveTime(AOdiaTrain train, int station, int direct) {
        Time time = train.getTime(station);
        try {
            switch (train.getStopType(station)) {
                case AOdiaTrain.NOSERVICE:
                    return "・・";
                case AOdiaTrain.NOVIA:
                    return NOVIA_STRING;
                case AOdiaTrain.PASS:
                    if (showPassFrag && (time.getArrivalTime() >= 0 || time.getDepartureTime() >= 0)) {
                        Companion.getTextPaint().setColor(Color.GRAY);
                    } else {
                        return PASS_STRING;
                    }
            }
            if (time.getArrivalTime() < 0) {
                if (time.getDepartureTime() >= 0) {
                    return getDepartureTime(train, station, direct);
                }
                return NODATA_STRING;
            }
            int second = time.getArrivalTime();
            int ss = second % 60;
            second = (second - ss) / 60;
            int mm = second % 60;
            second = (second - mm) / 60;
            int hh = second % 60;
            hh = hh % 24;
            String result = "";
            if (secondFrag) {
                result = hh + String.format("%02d", mm) + "-" + String.format("%02d", ss);
            } else {
                result = hh + String.format("%02d", mm);
            }
            return result;
        } catch (Exception e) {
            SdLog.log(e);
        }
        return "○";
    }

    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag) {
        if (centerFrag) {
            canvas.drawText(text, (this.getWidth() - 2 - paint.measureText(text)) / 2, y, paint);
        } else {
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawDepartString(Canvas canvas, int y, Paint paint, AOdiaTrain train, int station, int direct) {
        Time time = train.getTime(station);
        try {
            switch (train.getStopType(station)) {
                case AOdiaTrain.NOSERVICE:
                    drawNoService(canvas, y, paint);
                    return;
                case AOdiaTrain.NOVIA:
                    drawNoVia(canvas, y, paint);
                    return;
                case AOdiaTrain.PASS:
                    if (showPassFrag && (time.getArrivalTime() >= 0 || time.getDepartureTime() >= 0)) {
                        Companion.getTextPaint().setColor(Color.GRAY);
                    } else {
                        drawPass(canvas, y, paint);
                        return;
                    }
            }
            if (time.getDepartureTime() < 0) {
                if (time.getArrivalTime() < 0) {
                    drawText(canvas, "○", 1, y, paint, true);
                }
                drawArriveString(canvas, y, paint, train, station, direct);
                return;
            }
            int second = time.getDepartureTime();
            int ss = second % 60;
            second = (second - ss) / 60;
            int mm = second % 60;
            second = (second - mm) / 60;
            int hh = second % 60;
            hh = hh % 24;
            String result = "";
            if (secondFrag) {
                result = hh + String.format("%02d", mm) + "-" + String.format("%02d", ss);
            } else {
                result = hh + String.format("%02d", mm);
            }
            drawText(canvas, result, 1, y, Companion.getTextPaint(), true);
            return;

        } catch (Exception e) {
            SdLog.log(e);
        }
        drawText(canvas, "○", 1, y, paint, true);


    }

    private void drawArriveString(Canvas canvas, int y, Paint paint, AOdiaTrain train, int station, int direct) {
        Time time = train.getTime(station);
        try {
            switch (train.getStopType(station)) {
                case AOdiaTrain.NOSERVICE:
                    drawNoService(canvas, y, paint);
                    return;
                case AOdiaTrain.NOVIA:
                    drawNoVia(canvas, y, paint);
                    return;
                case AOdiaTrain.PASS:
                    if (showPassFrag && (time.getArrivalTime() >= 0 || time.getDepartureTime() >= 0)) {
                        Companion.getTextPaint().setColor(Color.GRAY);
                    } else {
                        drawPass(canvas, y, paint);
                        return;
                    }
            }
            if (time.getArrivalTime() < 0) {
                if (time.getDepartureTime() < 0) {
                    drawText(canvas, "○", 1, y, paint, true);
                    return;
                }
                drawDepartString(canvas, y, paint, train, station, direct);
                return;
            }
            int second = time.getArrivalTime();
            int ss = second % 60;
            second = (second - ss) / 60;
            int mm = second % 60;
            second = (second - mm) / 60;
            int hh = second % 60;
            hh = hh % 24;
            String result = "";
            if (secondFrag) {
                result = hh + String.format("%02d", mm) + "-" + String.format("%02d", ss);
            } else {
                result = hh + String.format("%02d", mm);
            }
            drawText(canvas, result, 1, y, Companion.getTextPaint(), true);
            return;
        } catch (Exception e) {
            SdLog.log(e);
        }
        drawText(canvas, "○", 1, y, paint, true);

    }

    private void drawPass(Canvas canvas, int y, Paint paint) {
        canvas.drawLine(getWidth() / 2, y + Companion.getTextSize() * 0.1f, getWidth() / 2, y - Companion.getTextSize() * 0.8f, paint);
        canvas.drawLine(getWidth() / 2, y + Companion.getTextSize() * 0.1f, getWidth() / 2 + Companion.getTextSize() * 0.6f, y - Companion.getTextSize() * 0.5f, paint);

    }

    private void drawNoVia(Canvas canvas, int y, Paint paint) {
        canvas.drawLine(getWidth() * 0.4f, y + Companion.getTextSize() * 0.1f, getWidth() * 0.4f, y - Companion.getTextSize() * 0.8f, paint);
        canvas.drawLine(getWidth() * 0.6f, y + Companion.getTextSize() * 0.1f, getWidth() * 0.6f, y - Companion.getTextSize() * 0.8f, paint);

    }

    private void drawNoService(Canvas canvas, int y, Paint paint) {
        float dotSize = Companion.getTextSize() * 0.1f;
        canvas.drawOval(getWidth() * 0.4f - dotSize, y - Companion.getTextSize() * 0.35f - dotSize, getWidth() * 0.4f + dotSize, y - Companion.getTextSize() * 0.35f + dotSize, paint);
        canvas.drawOval(getWidth() * 0.6f - dotSize, y - Companion.getTextSize() * 0.35f - dotSize, getWidth() * 0.6f + dotSize, y - Companion.getTextSize() * 0.35f + dotSize, paint);

    }

    private void drawStopNum(Canvas canvas, int y, Paint paint, int stopNum) {
        drawText(canvas, stopNum + "", 1, y, paint, true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(getWidth() * 0.5f - Companion.getTextSize() * 1.1f, y - Companion.getTextSize() * 0.8f, getWidth() * 0.5f + Companion.getTextSize() * 1.1f, y + Companion.getTextSize() * 0.1f, paint);
        paint.setStyle(Paint.Style.FILL);

    }

}
