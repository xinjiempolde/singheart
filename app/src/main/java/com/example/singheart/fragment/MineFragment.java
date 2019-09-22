package com.example.singheart.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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

public class MineFragment extends Fragment implements View.OnClickListener {
    private int resIdFlag = 0;
    private View rootView;
    private View user_info;
    private Button btn_about_us;
    private Button btn_setting;
    private Button btn_logout;
    private Button btn_course_bg;
    private SharedPreferences mSharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        SQliteUtil sQliteUtil = new SQliteUtil(getActivity());
        SQLiteDatabase db = sQliteUtil.getReadableDatabase();
        TextView textView = rootView.findViewById(R.id.head_name);
        TextView head_content = rootView.findViewById(R.id.head_content);
        Cursor cursor =  db.rawQuery("SELECT * FROM UserInfo",null);
        //存在数据才返回true
        if(cursor.moveToFirst())
        {
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

    public void bindViews(){
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mime,null,false);
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
        switch (v.getId()){
            case R.id.btn_logout:
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认框")
                        .setMessage("你确定要退出登陆吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSharedPreferences = getActivity().getSharedPreferences("LoginState", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean("isLogin",false);
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
                getActivity().startActivityForResult(it,0x123);
                break;
            case R.id.btn_settings:
                Toast.makeText(getActivity(),"正在开发",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_about_us:
                Toast.makeText(getActivity(),"正在开发",Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_info:
                startActivity(new Intent(getActivity(), InfoActivity.class));
        }
    }

}
