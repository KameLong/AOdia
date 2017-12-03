package com.kamelong.aodia.diadataOld;


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.kamelong.JPTI.Calendar;
import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Route;
import com.kamelong.JPTI.Station;
import com.kamelong.JPTI.Time;
import com.kamelong.JPTI.TrainType;
import com.kamelong.JPTI.Trip;
import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.OuDia.OuDiaTrain;
import com.kamelong.aodia.SdLog;

import java.util.ArrayList;

/**
 * Created by kame on 2017/10/28.
 */

public class Train {
    protected JPTI jpti = null;
    protected AOdiaService service = null;
    protected Calendar calendar = null;
    protected int direct = 0;
    protected String name = "";
    protected String number = "";
    protected String count = "";
    protected ArrayList<Trip> tripList = new ArrayList<>();
    /**
     * 備考
     */
    protected String text = "";


    public static final String DIRECT = "direct";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String COUNT = "count";
    public static final String TEXT = "text";
    public static final String TRIP = "trip";
    public static final String CALENDER = "calendar_id";


    public Train(JPTI jpti, AOdiaService service, JsonObject json) {
        this.jpti = jpti;
        this.service = service;
        direct = json.getInt(DIRECT, 0);
        name = json.getString(NAME, "");
        number = json.getString(NUMBER, "");
        count = json.getString(COUNT, "");
        text = json.getString(TEXT, "");
        calendar = jpti.getCalendar(json.getInt(CALENDER, 0));
        JsonArray tripArray = json.get(TRIP).asArray();
        for (int i = 0; i < tripArray.size(); i++) {
            tripList.add(jpti.getTrip(tripArray.get(i).asInt()));
        }
    }

    public Train(JPTI jpti, AOdiaService service, Calendar calendar, OuDiaFile oudiaFile, OuDiaTrain train, int blockID) {
        this.jpti = jpti;
        this.service = service;
        this.calendar = calendar;
        this.direct = train.getDirect();
        name = train.getName();
        number = train.getNumber();
        text = train.getRemark();
        count = train.getCount();

        ArrayList<Integer> borderList = oudiaFile.getBorders();

        int startStation = 0;
        int routeID = 0;
        for (int border : borderList) {
            if (border - startStation > 0) {
                int useStation = 0;
                for (int i = startStation; i < border + 1; i++) {
                    if (train.getStopType(i) == OuDiaTrain.STOP_TYPE_STOP || train.getStopType(i) == OuDiaTrain.STOP_TYPE_PASS) {
                        useStation++;
                    }
                }
                if (useStation > 0) {
                    Trip trip = jpti.addNewTrip(jpti.getRoute(routeID), calendar, oudiaFile, train, startStation, border, blockID);
                    tripList.add(trip);

                }
                startStation = border;
                if (oudiaFile.getStation(border).border()) {
                    startStation++;
                }
                routeID++;

            }
        }
    }

    public Train(JPTI jpti, AOdiaService service, Trip trip) {
        this.jpti = jpti;
        this.service = service;
        tripList.add(trip);
        calendar = trip.getCalendar();
        direct = trip.getDirect();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public JsonObject makeJSONObject() {
        try {
            JsonObject json = new JsonObject();
            json.add(DIRECT, direct);
            if (name.length() > 0) {
                json.add(NAME, name);
            }
            if (number.length() > 0) {
                json.add(NUMBER, number);
            }
            if (count.length() > 0) {
                json.add(COUNT, count);
            }
            if (text.length() > 0) {
                json.add(TEXT, text);
            }
            if (calendar != null) {
                json.add(CALENDER, jpti.indexOf(calendar));
            }
            JsonArray tripArray = new JsonArray();
            for (Trip trip : tripList) {
                tripArray.add(jpti.indexOf(trip));
            }
            json.add(TRIP, tripArray);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCalendarID() {
        return jpti.indexOf(calendar);
    }

    public int getDirect() {
        return direct;
    }

    public TrainType getTrainType() {
        try {
            return tripList.get(0).getTrainType();
        } catch (Exception e) {
            SdLog.toast("エラー：列車種別取得失敗(Train-getTrainType)");
            return jpti.getTrainType(0);
        }
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getText() {
        return text;
    }

    /**
     * stationからTimeを取得する
     *
     * @return
     */
    public Time searchTime(Station station) {
        for (Trip trip : tripList) {
            if (trip.searchTime(station) != null) {
                return trip.searchTime(station);
            }
        }
        return null;
    }

    public Trip searchTrip(Route route) {
        for (Trip trip : tripList) {
            if (trip.getRoute() == route) {
                return trip;
            }
        }
        return null;
    }

    public ArrayList<Trip> getTrip() {
        return tripList;
    }


}
