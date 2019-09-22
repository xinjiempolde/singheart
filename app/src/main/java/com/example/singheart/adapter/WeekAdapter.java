package com.example.singheart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.singheart.R;
import com.example.singheart.entity.Week;

import java.util.List;

public class WeekAdapter extends BaseAdapter {
    private List<Week> mDate;
    private Context mContext;
    public WeekAdapter(List<Week> Date, Context context){
        this.mDate = Date;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return mDate.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_week,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.textview = convertView.findViewById(R.id.txt);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textview.setText(mDate.get(position).getWeek());
        return convertView;
    }

    private static class ViewHolder{
        TextView textview;
    }
}
