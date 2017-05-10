package com.fc2.web.kamelong.aodia.GTFS;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fc2.web.kamelong.aodia.R;

/**
 * Created by kame on 2017/05/10.
 */

public class GtfsStationRecyclerViewAdapter extends RecyclerView.Adapter {
    LayoutInflater mLayoutInflater;
    private String[] data;
    public GtfsStationRecyclerViewAdapter(Context context,String[] dataList){
data=dataList;
        mLayoutInflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 3
        String data =this.data[position];
        ((ViewHolder)(holder)).text.setText(data);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View v) {
            super(v);
            // 2
            text = (TextView) v;
        }
    }
}
