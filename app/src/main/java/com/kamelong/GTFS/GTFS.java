package com.kamelong.GTFS;

import com.kamelong.JPTI.Calendar;
import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Station;
import com.kamelong.JPTI.Time;
import com.kamelong.JPTI.Trip;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.tool.LoadCsv;
import com.orangesignal.csv.manager.CsvEntityManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class GTFS {
    protected AOdiaActivity activity;
    protected String filePath="";
    List<GtfsRoute> routes=new ArrayList<>();
    List<GtfsStopTime>stopTime=new ArrayList<>();
    GtfsAgency agency=null;
    List<GtfsCalendar>calendars=new ArrayList<>();
    List<GtfsStop>stops=new ArrayList<>();
    List<GtfsTrips>trips=new ArrayList<>();


    public AOdiaActivity getActivity() {
        return activity;
    }

    /**
     * ファイルオープン(GTFS)
     * GTFSが記述されたzipファイルを開く
     */
    public GTFS(AOdiaActivity a,File GTFSzip){
            activity=a;
            filePath=GTFSzip.getPath();
    }
    public boolean isGTFS(){
        try {
            ZipFile zipFile = new ZipFile(filePath);
            if (getEntry(zipFile,"agency.txt") == null) {
                return false;
            }
            if (getEntry(zipFile,"calendar.txt") == null) {
                return false;
            }
            if (getEntry(zipFile,"routes.txt") == null) {
                return false;
            }
            if (getEntry(zipFile,"stop_times.txt") == null) {
                return false;
            }
            if (getEntry(zipFile,"stops.txt") == null) {
                return false;
            }
            if (getEntry(zipFile,"trips.txt") == null) {
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public ZipEntry getEntry(ZipFile zipFile,String fileName){

        return zipFile.getEntry(fileName);

    }
    /**
     * zip解凍
     * @param inputFile 解凍するzipファイル
     * @param outputDir 解凍先フォルダ
     * @throws Exception
     */
    protected void decompressZip( String inputFile , String outputDir ) throws Exception
    {
        // zipファイルの読込
        // try-with-resource構文でファイルcloseしている
        try(    FileInputStream         fis     = new FileInputStream( inputFile );
                ZipInputStream          archive = new ZipInputStream( fis ) )
        {
            // エントリーを1つずつファイル・フォルダに復元
            ZipEntry entry   = null;
            while( ( entry = archive.getNextEntry() ) != null )
            {
                // ファイルを作成
                File    file    = new File( outputDir + "/" + entry.getName() );

                // フォルダ・エントリの場合はフォルダを作成して次へ
                if( entry.isDirectory() )
                {
                    file.mkdirs();
                    continue;
                }

                // ファイル出力する場合、
                // フォルダが存在しない場合は事前にフォルダ作成
                if( !file.getParentFile().exists() ){ file.getParentFile().mkdirs(); }

                // ファイル出力
                try(    FileOutputStream        fos = new FileOutputStream( file ) ;
                        BufferedOutputStream bos = new BufferedOutputStream( fos ) )
                {
                    // エントリーの中身を出力
                    int     size    = 0;
                    byte[]  buf     = new byte[ 1024 ];
                    while( ( size = archive.read( buf ) ) > 0 )
                    {
                        bos.write( buf , 0 , size );
                    }
                }
            }
        }
    }
    public boolean load(){
        try {
            decompressZip(filePath, activity.getExternalCacheDir().getPath()+"/GTFS");
            try {
                LoadCsv routeCsv=new LoadCsv(new FileInputStream(new File(activity.getExternalCacheDir().getPath()+"/GTFS/routes.txt")));
                for(int i=0;i<routeCsv.dataNum();i++){
                    routes.add(new GtfsRoute(routeCsv,i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(routes.get(0).routeID);

            try {
                LoadCsv stopTimeCsv=new LoadCsv(new FileInputStream(new File(activity.getExternalCacheDir().getPath()+"/GTFS/stop_times.txt")));
                for(int i=0;i<stopTimeCsv.dataNum();i++){
                    stopTime.add(new GtfsStopTime(stopTimeCsv,i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                LoadCsv calendarCsv=new LoadCsv(new FileInputStream(new File(activity.getExternalCacheDir().getPath()+"/GTFS/calendar.txt")));
                for(int i=0;i<calendarCsv.dataNum();i++){
                    calendars.add(new GtfsCalendar(calendarCsv,i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                LoadCsv stopCsv=new LoadCsv(new FileInputStream(new File(activity.getExternalCacheDir().getPath()+"/GTFS/stops.txt")));
                for(int i=0;i<stopCsv.dataNum();i++){
                    stops.add(new GtfsStop(stopCsv,i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                LoadCsv tripCsv=new LoadCsv(new FileInputStream(new File(activity.getExternalCacheDir().getPath()+"/GTFS/trips.txt")));
                for(int i=0;i<tripCsv.dataNum();i++){
                    trips.add(new GtfsTrips(tripCsv,i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println(stopTime.get(0).arrivalTime);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }
    public JPTI makeJPTI(){
        JPTI jpti=new JPTI();
        jpti.addTrainType();
//        agency.makeJptiAgency(jpti.addNewAgency());
        for(int i=0;i<stops.size();i++){
            Station s=jpti.addNewStation(stops.get(i).name);
            stops.get(i).makeJptiData(jpti.addNewStop(s),s);
        }
        for(int i=0;i<calendars.size();i++){
            Calendar calendar=jpti.addNewCalendar();
            calendars.get(i).makeJptiCalendar(calendar);
        }
        for(int i=0;i<routes.size();i++){
            routes.get(i).makeJptiRoute(jpti.addNewRoute(),this);
        }
        Trip trip=jpti.addNewTrip(jpti.getRoute(getRouteIndex(getTrip(stopTime.get(0).tripID).routeID)));
        trip.setName(stopTime.get(0).tripID);
        trip.setCalendar(getCalendarIndex(getTrip(stopTime.get(0).tripID).serviceID));
        for(int i=0;i<stopTime.size();i++){
            if(!stopTime.get(i).tripID.equals(trip.getName())){
                trip=jpti.addNewTrip(jpti.getRoute(getRouteIndex(getTrip(stopTime.get(i).tripID).routeID)));
                trip.setName(stopTime.get(i).tripID);
                try {
                    trip.setDirect(Integer.parseInt(getTrip(stopTime.get(i).tripID).direction));
                }catch (NumberFormatException e){
                    trip.setDirect(0);
                }
                trip.setCalendar(getCalendarIndex(getTrip(stopTime.get(i).tripID).serviceID));
            }
            int stopIndex=0;
            for(int z=0;z< stops.size();z++){
                if(stops.get(z).stopID.equals(stopTime.get(i).stopID)){
                    stopIndex=z;
                    break;

                }
            }
            Time time=new Time(jpti,trip,jpti.getStop(stopIndex));
            trip.addTime(stopTime.get(i).getTime(time));

        }
        jpti.makeService();

        return jpti;
    }
    private GtfsTrips getTrip(String tripID){
        for(GtfsTrips t:trips){
            if(t.tripID.equals(tripID)){
                return t;
            }
        }
        return null;
    }
    private int getRouteIndex(String routeID){
        for(int i=0;i<routes.size();i++){
            if(routes.get(i).routeID.equals(routeID)){
                return i;
            }
        }
        return -1;
    }
    private int getCalendarIndex(String calendarID){
        for(int i=0;i<calendars.size();i++){
            if(calendars.get(i).serviceID.equals(calendarID)){
                return i;
            }
        }
        return -1;
    }

}
