package com.example.singheart.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singheart.R;
import com.example.singheart.Util.SQliteUtil;
import com.example.singheart.activity.InfoActivity;
import com.example.singheart.activity.LoginActivity;
import com.example.singheart.activity.MainActivity;
import com.example.singheart.activity.setBgActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MineFragment extends Fragment implements View.OnClickListener {
    private int resIdFlag = 0;
    private View rootView;
    private View user_info;
    private Button btn_about_us;
    private Button btn_setting;
    private Button btn_logout;
    private Button btn_course_bg;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    Context mContext;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),"服务器正在维护",Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),"您似乎没有连接网络",Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(),"已是最新版本",Toast.LENGTH_SHORT).show();
                    break;
                case 0x004:
                    mProgressDialog.dismiss();
                    new AlertDialog.Builder(getActivity())
                            .setTitle("确认框")
                            .setMessage("检测到新版本，你确定要下载最新版本吗？")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    download_apk();
                                }
                            })
                            .setNegativeButton("取消",null).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        SQliteUtil sQliteUtil = new SQliteUtil(getActivity());
        SQLiteDatabase db = sQliteUtil.getReadableDatabase();
        TextView textView = rootView.findViewById(R.id.head_name);
        TextView head_content = rootView.findViewById(R.id.head_content);
        Cursor cursor = db.rawQuery("SELECT * FROM UserInfo", null);
        //存在数据才返回true
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String myclass = cursor.getString(cursor.getColumnIndex("class"));
            head_content.setText("我的班级:" + myclass);
            textView.setText(name);
        }
        cursor.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView;
    }

    public void bindViews() {
        mContext = getActivity();
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mime, null, false);
        btn_logout = rootView.findViewById(R.id.btn_logout);
        btn_course_bg = rootView.findViewById(R.id.btn_coourse_bg);
        btn_about_us = rootView.findViewById(R.id.btn_about_us);
        btn_setting = rootView.findViewById(R.id.btn_settings);
        user_info = rootView.findViewById(R.id.user_info);
        user_info.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_course_bg.setOnClickListener(this);
        btn_about_us.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认框")
                        .setMessage("你确定要退出登陆吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSharedPreferences = getActivity().getSharedPreferences("LoginState", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean("isLogin", false);
                                editor.commit();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            case R.id.btn_coourse_bg:
                Intent it = new Intent(getActivity(), setBgActivity.class);
                getActivity().startActivityForResult(it, 0x123);
                break;
            case R.id.btn_settings:
                check_version();
                break;
            case R.id.btn_about_us:
                Toast.makeText(getActivity(), "正在开发", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_info:
                startActivity(new Intent(getActivity(), InfoActivity.class));
        }
    }

    private void check_version(){
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("获取版本信息");
        mProgressDialog.setMessage("正在获取版本信息，请稍等···");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("http://39.96.26.6:8080/version.html")
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                OkHttpClient baidu_client = new OkHttpClient.Builder().build();
                Request baidu_request = new Request.Builder()
                        .url("https://www.baidu.com")
                        .get()
                        .build();
                Call baidu_call = baidu_client.newCall(baidu_request);
                baidu_call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.sendEmptyMessage(0x002);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        mHandler.sendEmptyMessage(0x001);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                PackageManager manager = getActivity().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                int now_version = info.versionCode;
                int latest_version = Integer.parseInt(response.body().string());
                if (latest_version > now_version){
                    mHandler.sendEmptyMessage(0x004);
                }else {
                    mHandler.sendEmptyMessage(0x003);
                }
            }
        });
    }

    private void download_apk(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("下载中");
        dialog.setMessage("正在下载中，请稍后~");
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://39.96.26.6:8080/singheart1.1.1.apk");
                    URLConnection conn = url.openConnection();
                    InputStream is = conn.getInputStream();
                    int contentLength = conn.getContentLength();
                    String dirName = getActivity().getExternalFilesDir(null) + "/MyDownLoad/";
                    File file = new File(dirName);
                    //不存在创建
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    //下载后的文件名
                    String fileName = dirName + "singheart1.1.1.apk";
                    File file1 = new File(fileName);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    //创建字节流
                    byte[] bs = new byte[1024];
                    int len;
                    OutputStream os = new FileOutputStream(fileName);
                    //写数据
                    while ((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }
                    //完成后关闭流
                    os.close();
                    is.close();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileProvider", file1);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        Log.e("uri",uri.toString());
                    } else {
                        intent.setDataAndType(Uri.fromFile(file1), "application/vnd.android.package-archive");
                    }
                    dialog.dismiss();
                    getActivity().finish();
                    startActivity(intent);
                    Log.e("run", "下载完成了~" + file1.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
