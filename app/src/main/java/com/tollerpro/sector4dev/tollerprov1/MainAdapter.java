package com.tollerpro.sector4dev.tollerprov1;

import android.graphics.Color;
import android.support.v7.widget.CardView;
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
    public CardView myCard;
    private ViewHolder myviewHolder;
    private View myView;

    public MainAdapter(ArrayList<String> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        ViewHolder vh= new ViewHolder(v);
        myView=v;
        myviewHolder=vh;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mDataSet.get(position));
    }

    public void changeColorItem(RecyclerView mRecyclerView,int position){
        View v = mRecyclerView.getLayoutManager().findViewByPosition(position);
        v.findViewById(R.id.cardtitle).setBackgroundColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public CardView mCard;
        public ViewHolder(View itemView) {
            super(itemView);
            mTitle=(TextView) itemView.findViewById(R.id.title);
            mCard=(CardView) itemView.findViewById(R.id.cardtitle);
        }
    }
}
