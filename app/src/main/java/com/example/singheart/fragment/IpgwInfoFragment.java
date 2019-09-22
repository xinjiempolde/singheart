package com.example.singheart.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.singheart.R;
import com.example.singheart.activity.MainActivity;
import com.example.singheart.adapter.NetInfoAdapter;
import com.example.singheart.entity.NetInfo;
import com.scwang.smartrefresh.header.DeliveryHeader;
import com.scwang.smartrefresh.header.PhoenixHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class IpgwInfoFragment extends Fragment implements View.OnClickListener {
    private ListView net_info;
    private View rootView;
    private Context mContext;
    private List<NetInfo> mData;
    private Button btn_disconn;

    public static IpgwInfoFragment newInstance(Bundle bundle) {
        IpgwInfoFragment newFragment = new IpgwInfoFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_disconn = rootView.findViewById(R.id.btn_disconn);
        btn_disconn.setOnClickListener(this);
        mData = new ArrayList<NetInfo>();
        Bundle bundle = getArguments();
        mData.add(new NetInfo("账户余额", bundle.getString("user_balance", "无")));
        mData.add(new NetInfo("已用流量", bundle.getString("sum_bytes", "无")));
        mData.add(new NetInfo("已用时常", bundle.getString("sum_seconds", "无")));
        mData.add(new NetInfo("当前IP", bundle.getString("user_ip", "无")));
        NetInfoAdapter adapter = new NetInfoAdapter(mData, mContext);
        net_info = rootView.findViewById(R.id.net_info);
        net_info.setAdapter(adapter);
        RefreshLayout refreshLayout = (RefreshLayout) rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new PhoenixHeader(mContext));
//设置 Footer 为 球脉冲 样式
        refreshLayout.setRefreshFooter(new BallPulseFooter(mContext).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ipgw_info, null, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("调用createview", "createview");
        return rootView;
    }

    @Override
    public void onClick(View v) {
        MainActivity activity = (MainActivity) getActivity();
        activity.createIpgwInit();
    }
}
