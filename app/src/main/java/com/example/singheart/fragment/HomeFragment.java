package com.example.singheart.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.singheart.R;
import com.example.singheart.Util.RegexUtil;
import com.example.singheart.Util.handleDateUtil;
import com.example.singheart.activity.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private OkHttpClient mClient = null;
    private Context mContext;
    private Bundle net_info_bundle;
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private String stuID = "20184569";
    private String stuPass = "218112";
    private String JSESSIONID = "";
    private String GSESSIONID = "";
    private String SERVERNAME = "";
    private View rootView;
    private Button btn_conn, btn_disconn;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    Toast.makeText(mContext, "您似乎没有连接到网络", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(mContext, "首次使用请连接校园网", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(mContext, "登陆成功", Toast.LENGTH_SHORT).show();
                    MainActivity activity = (MainActivity)getActivity();
                    activity.createIpgwFrag(net_info_bundle);
                    break;
                case 0x004:
                    Toast.makeText(mContext, "请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 0x005:
                    Toast.makeText(mContext, "账户或密码错误！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null, false);
        bindViews();
    }

    public void bindViews() {
        btn_conn = rootView.findViewById(R.id.connect);
        btn_disconn = rootView.findViewById(R.id.disconnect);
        btn_conn.setOnClickListener(this);
        btn_disconn.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect:
                get_info_from_ipgw();
                break;
            case R.id.disconnect:
                break;
        }
    }

    //从网站上获取流量剩余情况
    public void get_info_from_ipgw() {
        final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        mClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Log.e("url", url.toString());
                        for (Cookie cookie : cookies) {
                            if (cookie.name().equals("JSESSIONID")) {
                                JSESSIONID = cookie.value();
                            }
                            if (cookie.name().equals("GSESSIONID")) {
                                GSESSIONID = cookie.value();
                            }
                            if (cookie.name().equals("SERVERNAME")) {
                                SERVERNAME = cookie.value();
                            }
                        }
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();

        final Request get_request = new Request.Builder()
                .url("https://pass.neu.edu.cn/tpass/login?service=https%3A%2F%2Fipgw.neu.edu.cn%2Fsrun_cas.php%3Fac_id%3D1")
                .addHeader("User_Agent", userAgent)
                .addHeader("Connection", "close")
                .get()
                .build();
        Call get_call = mClient.newCall(get_request);
        get_call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //如果失败了，检查问题原因:未连接校园网或没有连接网络
                final Request baidu_request = new Request.Builder()
                        .url("https://www.baidu.com")
                        .get()
                        .build();
                mClient.newCall(baidu_request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.sendEmptyMessage(0x001);
                        return;
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        mHandler.sendEmptyMessage(0x002);
                        return;
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String firstPage = response.body().string();
                //开始构造表单数据
                final String lt = RegexUtil.getUniqueResulte("<input type=\"hidden\" id=\"lt\" name=\"lt\" value=\"(.*?)\" />", firstPage, 1);
                final String execution = RegexUtil.getUniqueResulte("<input type=\"hidden\" name=\"execution\" value=\"(.*?)\" />", firstPage, 1);
                final String rsa = stuID + stuPass + lt;
                final String ul = String.valueOf(stuID.length());
                final String pl = String.valueOf(stuPass.length());
                final String _eventId = "submit";
                RequestBody postDate = new FormBody.Builder()
                        .add("rsa", rsa)
                        .add("ul", ul)
                        .add("pl", pl)
                        .add("lt", lt)
                        .add("execution", execution)
                        .add("_eventId", _eventId)
                        .build();
                final Request post_request = new Request.Builder()
                        .url("https://pass.neu.edu.cn/tpass/login?service=https%3A%2F%2Fipgw.neu.edu.cn%2Fsrun_cas.php%3Fac_id%3D1")
                        .addHeader("User-Agent", userAgent)
                        .addHeader("Connection", "close")
                        .post(postDate)
                        .build();
                Call post_call = mClient.newCall(post_request);
                post_call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.sendEmptyMessage(0x002);
                        return;
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        System.out.println(response.body().string());
                        RequestBody course_date = new FormBody.Builder()
                                .add("action", "get_online_info")
                                .add("key", "43220")
                                .build();
                        String myCookie = "JSESSIONID=" + JSESSIONID + "; " + "SERVERNAME=xk3; " + "GSESSIONID=" + GSESSIONID;
                        Request course_request = new Request.Builder()
                                .url("https://ipgw.neu.edu.cn/include/auth_action.php?k=43220")
                                .addHeader("User-Agent", userAgent)
                                .addHeader("Cookie", myCookie)
                                .post(course_date)
                                .build();
                        final OkHttpClient course_client = new OkHttpClient();
                        final Call course_call = course_client.newCall(course_request);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(100);//休眠2秒
                                    course_call.enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            Log.e("失败", "获取网络信息失败");
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String net_info = response.body().string();
                                            System.out.println(net_info);
                                            net_info_bundle = handleDateUtil.handleNetInfo(net_info);
                                            mHandler.sendEmptyMessage(0x003);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
/*                        if (JSESSIONID.equals("") || GSESSIONID.equals("")){
                            mHandler.sendEmptyMessage(0x005);
                            return;
                        }*/
                    }
                });
            }
        });
    }
}