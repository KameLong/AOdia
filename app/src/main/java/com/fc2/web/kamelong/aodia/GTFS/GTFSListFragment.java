package com.fc2.web.kamelong.aodia.GTFS;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;

import java.util.ArrayList;
import java.util.Arrays;


public class GTFSListFragment extends Fragment {
    MainActivity activity;
    RecyclerView stationRecyclerView;
    RecyclerView useStationRecyclerView;
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
            final GtfsStationRecyclerViewAdapter adapter = new GtfsStationRecyclerViewAdapter(getActivity(), activity.gtfs.getStationList());
            final GtfsStationRecyclerViewAdapter useAdapter = new GtfsStationRecyclerViewAdapter(activity, new ArrayList<GTFSStation>());
            stationRecyclerView.setAdapter(adapter);
            ItemTouchHelper stationListItemDecor = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                            ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            final int toPos = target.getAdapterPosition();
                            adapter.notifyItemMoved(fromPos, toPos);

                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            useAdapter.addStation(adapter.data.get(fromPos));
                            adapter.data.remove(fromPos);
                            adapter.notifyItemRemoved(fromPos);
                            useStationRecyclerView.scrollToPosition(useAdapter.data.size() - 1);

                        }
                    });
            stationListItemDecor.attachToRecyclerView(stationRecyclerView);

            useStationRecyclerView = (RecyclerView) activity.findViewById(R.id.useStationList);
            useStationRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            useStationRecyclerView.setAdapter(useAdapter);
            useAdapter.setOnItemClickListener(new GtfsStationRecyclerViewAdapter.onItemClickListener(){
                @Override
                public void onClick(View view,String name) {
                    Toast.makeText(activity, ""+view.getId(), Toast.LENGTH_SHORT).show();

                    Toast.makeText(activity, String.valueOf(useAdapter.data.get(0)), Toast.LENGTH_SHORT).show();
                }
            });
            ItemTouchHelper useStationListItemDecor = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                            ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                            final int fromPos = viewHolder.getAdapterPosition();
                            final int toPos = target.getAdapterPosition();
                            useAdapter.notifyItemMoved(fromPos, toPos);
                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            adapter.addStation(useAdapter.data.get(fromPos));
                            useAdapter.data.remove(fromPos);
                            useAdapter.notifyItemRemoved(fromPos);
                        }
                    });
            useStationListItemDecor.attachToRecyclerView(useStationRecyclerView);


        }catch(Exception e){
            SdLog.log(e);
        }

    }

}
