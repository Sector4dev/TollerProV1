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
    private ArrayList<String> pDataset;

    MainAdapter(ArrayList<String> pDataset) {
        this.pDataset = pDataset;
    }


    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        holder.ptitle.setText(pDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return pDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ptitle;
        public ViewHolder(View itemView) {
            super(itemView);
            ptitle=(TextView)itemView.findViewById(R.id.ptitles);
        }
    }
}
