package com.example.singheart.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.singheart.R;
import com.example.singheart.adapter.GridViewAdapter;
import com.example.singheart.entity.Img;

import java.util.ArrayList;
import java.util.List;

public class setBgActivity extends AppCompatActivity {
    private Context mContext;
    private GridView grid_img;
    private GridViewAdapter mAdapter = null;
    private List<Img> mDate = null;
    public int[] resid = new int[]
            {
                    R.drawable.course_bg1, R.drawable.course_bg2, R.drawable.course_bg3,
                    R.drawable.course_bg4, R.drawable.course_bg5
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bg);
        mContext = setBgActivity.this;
        grid_img = (GridView) findViewById(R.id.grid_img);
        mDate = new ArrayList<Img>();
        for (int i = 0; i < resid.length; i++) {
            mDate.add(new Img(resid[i]));
        }
        mAdapter = new GridViewAdapter(mDate, mContext);
        grid_img.setAdapter(mAdapter);

        grid_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = getIntent();
                Bundle bd = new Bundle();
                bd.putInt("resIdFlag", position);
                it.putExtras(bd);
                setResult(0x123, it);
                finish();
            }
        });
    }
}
