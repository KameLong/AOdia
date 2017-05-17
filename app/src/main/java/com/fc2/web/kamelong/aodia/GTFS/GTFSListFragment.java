package com.fc2.web.kamelong.aodia.GTFS;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;

import java.util.ArrayList;
import java.util.Arrays;


public class GTFSListFragment extends Fragment {
    MainActivity activity;
    RecyclerView stationRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            activity=(MainActivity) getActivity();

            return inflater.inflate(R.layout.gtfs_list, container, false);
        }catch(Exception e){
            SdLog.log(e);
        }
        return new View(activity);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            stationRecyclerView = (RecyclerView) activity.findViewById(R.id.stationList);
            stationRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
            stationRecyclerView.setLayoutManager(manager);


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, activity.gtfs.getStationName());
            stationRecyclerView.setAdapter(new GtfsStationRecyclerViewAdapter(getActivity(), activity.gtfs.getStationName()));
        }catch(Exception e){
            SdLog.log(e);
        }

    }

}
