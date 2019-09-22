package com.example.singheart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.singheart.R;
import com.example.singheart.entity.Img;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<Img> mDate;
    private Context mContext;
    public GridViewAdapter(List<Img> mDate, Context context){
        this.mDate = mDate;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_img,parent,false);
            holder = new ViewHolder();
            holder.img = convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.img.setImageResource(mDate.get(position).getImgResId());
        return convertView;
    }

    private static class ViewHolder{
        ImageView img;
    }
}
