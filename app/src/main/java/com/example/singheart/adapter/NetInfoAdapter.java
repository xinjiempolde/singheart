package com.example.singheart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.singheart.R;
import com.example.singheart.entity.NetInfo;
import com.example.singheart.holder.NetInfoHolder;

import java.util.List;

public class NetInfoAdapter extends BaseAdapter {
    private List<NetInfo> mDate;
    private Context mContext;

    public NetInfoAdapter(List<NetInfo> mDate, Context context) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetInfoHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_net_info, parent, false);
            holder = new NetInfoHolder();
            convertView.setTag(holder);
        } else {
            holder = (NetInfoHolder) convertView.getTag();
        }
        holder.titile = convertView.findViewById(R.id.title);
        holder.info = convertView.findViewById(R.id.content);
        holder.titile.setText(mDate.get(position).getTitle());
        holder.info.setText(mDate.get(position).getInfo());
        return convertView;
    }
}
