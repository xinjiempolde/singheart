package com.example.singheart.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.singheart.R;
import com.example.singheart.Util.SQliteUtil;

public class InfoActivity extends AppCompatActivity {
    private TextView tv_name, tv_college, tv_major, tv_class, tv_school_area;
    private ImageView back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
    }

    //从数据库获取信息显示到界面
    public void initView(){
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_name = findViewById(R.id.name);
        tv_college = findViewById(R.id.college);
        tv_major = findViewById(R.id.major);
        tv_class = findViewById(R.id.myclass);
        tv_school_area = findViewById(R.id.school_area);
        SQliteUtil sQliteUtil = new SQliteUtil(InfoActivity.this);
        SQLiteDatabase db = sQliteUtil.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM UserInfo",null);
        //存在数据才返回true
        if(cursor.moveToFirst())
        {
           String name = cursor.getString(cursor.getColumnIndex("name"));
           String college = cursor.getString(cursor.getColumnIndex("college"));
           String major = cursor.getString(cursor.getColumnIndex("major"));
           String myclass = cursor.getString(cursor.getColumnIndex("class"));
           String school_area = cursor.getString(cursor.getColumnIndex("school_area"));
           tv_name.setText(name);
           tv_college.setText(college);
           tv_major.setText(major);
           tv_class.setText(myclass);
           tv_school_area.setText(school_area);
        }
        cursor.close();
    }
}
