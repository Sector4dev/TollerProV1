package com.tollerpro.sector4dev.tollerprov1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sector4 Dev on 8/1/2017.
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<String> mDataSet;

    public MainAdapter(ArrayList<String> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ViewHolder vh= new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitle=(TextView) itemView.findViewById(R.id.title);
        }
    }
}
