package com.kamelong.GTFS;

import com.kamelong.JPTI.Calendar;
import com.kamelong.tool.LoadCsv;

/**
 * Created by kame on 2017/11/03.
 */

public class GtfsCalendar {
    boolean monday=false;
    boolean tuesday=false;
    boolean wednesday =false;
    boolean thursday=false;
    boolean friday =false;
    boolean saturday=false;
    boolean sunday=false;
    String serviceID="";
    public GtfsCalendar(LoadCsv csv,int index){
        monday=csv.getData("monday",index).equals("1");
        tuesday=csv.getData("tuesday",index).equals("1");
        wednesday =csv.getData("wednesday",index).equals("1");
        thursday=csv.getData("thursday",index).equals("1");
        friday =csv.getData("friday",index).equals("1");
        saturday=csv.getData("saturday",index).equals("1");
        sunday=csv.getData("sunday",index).equals("1");
       serviceID=csv.getData("service_id",index);
    }
    public void makeJptiCalendar(Calendar calendar){
        if(monday&&sunday){
            calendar.setName("全日");
            return;
        }
        if(monday){
            calendar.setName("平日");
            return;
        }
        if(sunday){
            calendar.setName("休日");
            return;
        }
        if(saturday){
            calendar.setName("土曜日");
            return;
        }
        calendar.setName("その他");
    }

}
