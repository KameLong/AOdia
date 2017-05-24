package com.fc2.web.kamelong.aodia.GTFS;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.oudia.Station;

import java.util.ArrayList;
import java.util.Arrays;


public class GtfsStationRecyclerViewAdapter extends RecyclerView.Adapter {
    LayoutInflater mLayoutInflater;
    public ArrayList<GTFSStation> data;
    private onItemClickListener listener;
    public GtfsStationRecyclerViewAdapter(Context context,ArrayList<GTFSStation> stationList){
        data= stationList;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // 3
        String data =this.data.get(position).getName();
        ((ViewHolder)(holder)).text.setText(data);
        ((ViewHolder)(holder)).text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // listener.onClick(view,GtfsStationRecyclerViewAdapter.this.data.get(holder.getAdapterPosition()));
            }
        });
    }
    public interface onItemClickListener {
        void onClick(View view, String name);
    }
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public ViewHolder(View v) {
            super(v);
            // 2
            text = (TextView) v;
        }

    }
    public void addStation(GTFSStation station){
        data.add(station);
        notifyDataSetChanged();

    }
}
