package com.kamelong.aodia.loadGTFS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kamelong.GTFS.GTFS;
import com.kamelong.GTFS.Route;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class RouteViewAdaper extends BaseAdapter {
    private GTFS gtfs;
    ArrayList<Route> routes=new ArrayList<>();
    ArrayList<Route> downRoutes=new ArrayList<>();
    ArrayList<Route> upRoutes=new ArrayList<>();
    private MainActivity activity;
    public RouteViewAdaper(MainActivity activity,GTFS gtfs){
        this.activity=activity;
        this.gtfs=gtfs;
        for(Map.Entry<String,Route> entry:gtfs.route.entrySet()){
            routes.add(entry.getValue());
        }
        routes.sort((o1, o2) -> o1.route_name.compareTo(o2.route_name));

    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Route getItem(int position) {
        return routes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            convertView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.open_gtfs_route, parent, false);
            final TextView textView=convertView.findViewById(R.id.routeName);
            textView.setText(routes.get(position).route_name);
            final TextView stationView=convertView.findViewById(R.id.routeStation);
            stationView.setText(gtfs.stop.get(routes.get(position).getStartStation()).stop_name+"→"+gtfs.stop.get(routes.get(position).getEndStation()).stop_name);

            final CheckBox downCheck=convertView.findViewById(R.id.asDownRoute);
            final CheckBox upCheck=convertView.findViewById(R.id.asUpRoute);
            downCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    downRoutes.add(getItem(position));
                    if(upCheck.isChecked()){
                        upCheck.setChecked(false);
                    }

                }else{
                    downRoutes.remove(getItem(position));
                }
            });
            upCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    upRoutes.add(getItem(position));
                    if(downCheck.isChecked()){
                        downCheck.setChecked(false);
                    }

                }else{
                    upRoutes.remove(getItem(position));
                }
            });
        }catch (Exception e){
            SDlog.log(e);
        }

        return convertView;
    }
}
