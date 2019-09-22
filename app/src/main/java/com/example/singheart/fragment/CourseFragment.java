package com.example.singheart.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.singheart.R;
import com.example.singheart.Util.FileUtil;
import com.example.singheart.Util.handleDateUtil;
import com.example.singheart.activity.MainActivity;
import com.example.singheart.adapter.WeekAdapter;
import com.example.singheart.entity.Course;
import com.example.singheart.entity.Week;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class CourseFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private int WhichWeek = 7;
    private ArrayList<Course> mCourses = new ArrayList();
    private List<Integer> colorList = null;
    private List<Week> mWeeks;
    private WeekAdapter mWeekAdapter = null;
    private View rootView = null;
    private Spinner mSpinner;
    private int week = 0;
    private int resIdFlag = 0;
    public int[] resid = new int[]
            {
                    R.drawable.course_bg1, R.drawable.course_bg2, R.drawable.course_bg3,
                    R.drawable.course_bg4, R.drawable.course_bg5
            };

    public static CourseFragment newInstance(int resIdFlag) {
        CourseFragment newFragment = new CourseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resIdFlag", resIdFlag);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_course, null, false);
        SelectRes();
        String strJson = null;
        try {
            strJson = FileUtil.getJson("course_date.json", getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseEasyJson(strJson);
        WhichWeek = handleDateUtil.judgeWhichWeek();
        week = handleDateUtil.get_day_in_week();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout left_view = (LinearLayout) rootView.findViewById(R.id.left_view);
        int height = 180; //一节课视图高度
        TextView day_of_week = null;
        int day_of_week_id = getResources().getIdentifier("day" + (week - 1) + "_of_week","id",getActivity().getPackageName());
        day_of_week = rootView.findViewById(day_of_week_id);
        day_of_week.setBackgroundColor(getResources().getColor(R.color.week_selected));
        //创建左侧节数视图
        for (int i = 1; i <= 12; i++) {
            TextView textView = new TextView(getActivity());
            textView.setText(String.valueOf(i));
            textView.setTextColor(getResources().getColor(R.color.text_color));
            textView.setBackgroundColor(getResources().getColor(R.color.left_view_bg));
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            left_view.addView(textView);
        }
        LinearLayout all_bg = rootView.findViewById(R.id.all_course_bg);
        all_bg.setBackgroundResource(resid[resIdFlag]);
        changeByWeek();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSpinner();
    }

    public void changeByWeek() {
        assert WhichWeek >= 0;
        int height = 180;   //每小节课间有间隔距离
        RelativeLayout day = null;
        for (int i = 0; i < mCourses.size(); i++) {
            for (int j = 0; j < mCourses.get(i).Week_NUM.size(); j++) {
                if (WhichWeek == mCourses.get(i).Week_NUM.get(j)) {
                    switch (mCourses.get(i).getCol()) {
                        case 6:
                            day = rootView.findViewById(R.id.sunday);
                            break;
                        case 5:
                            day = rootView.findViewById(R.id.saturday);
                            break;
                        case 4:
                            day = rootView.findViewById(R.id.friday);
                            break;
                        case 3:
                            day = rootView.findViewById(R.id.thursday);
                            break;
                        case 2:
                            day = rootView.findViewById(R.id.wednesday);
                            break;
                        case 1:
                            day = rootView.findViewById(R.id.tuesday);
                            break;
                        case 0:
                            day = rootView.findViewById(R.id.monday);
                    }
                    View one_course = LayoutInflater.from(getActivity()).inflate(R.layout.course_card, null, false);
                    one_course.setY(mCourses.get(i).getRow() * (height + 6));  //课程起始位置
                    one_course.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height * mCourses.get(i).getSpan())); //课程跨度，几节课
                    TextView courseContent = one_course.findViewById(R.id.text_view);
                    courseContent.setText(mCourses.get(i).getCourseName() + "\n" + mCourses.get(i).getPosition());
                    Random myRandom = new Random();
                    int randomId = myRandom.nextInt(colorList.size());
                    int ranColor = colorList.get(randomId);
                    courseContent.setBackgroundColor(ranColor);

                    day.addView(one_course);
                }
            }
        }
    }

    public void initSpinner() {
        mSpinner = rootView.findViewById(R.id.spinner_week);
        mWeeks = new ArrayList<Week>();
        mWeeks.add(new Week("快乐假期"));
        for (int i = 0; i < 24; i++) {
            mWeeks.add(new Week("第" + String.valueOf(i + 1) + "周"));
        }
        mWeekAdapter = new WeekAdapter(mWeeks, getActivity());
        mSpinner.setAdapter(mWeekAdapter);
        mSpinner.setOnItemSelectedListener(this);
        if (WhichWeek == -1) {
            mSpinner.setSelection(0);
        } else {
            mSpinner.setSelection(WhichWeek + 1);
        }
    }



    public void parseEasyJson(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            Log.e("jsonArray",jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Course course = new Course();
                course.setCourseName(jsonObject.getString("name"));
                course.setPosition(jsonObject.getString("room"));
                course.setWeek(jsonObject.getString("week"));
                course.setCol(jsonObject.getString("col"));
                course.setRow(jsonObject.getString("row"));
                course.setSpan(jsonObject.getInt("span"));
                course.tidyDate();
                mCourses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void SelectRes() {
        Bundle bundle = getArguments();
        resIdFlag = bundle.getInt("resIdFlag",3);
        colorList = new ArrayList<Integer>();
        Log.e("resIdFlag",String.valueOf(resIdFlag));
        switch (resIdFlag){
            case 0:
                colorList.add(getResources().getColor(R.color.first_course_bg1));
                colorList.add(getResources().getColor(R.color.first_course_bg2));
                colorList.add(getResources().getColor(R.color.first_course_bg3));
                colorList.add(getResources().getColor(R.color.first_course_bg4));
                break;
            case 1:
                colorList.add(getResources().getColor(R.color.second_course_bg1));
                colorList.add(getResources().getColor(R.color.second_course_bg2));
                colorList.add(getResources().getColor(R.color.second_course_bg3));
                colorList.add(getResources().getColor(R.color.second_course_bg4));
                break;
            case 2:
                colorList.add(getResources().getColor(R.color.third_course_bg1));
                colorList.add(getResources().getColor(R.color.third_course_bg2));
                colorList.add(getResources().getColor(R.color.third_course_bg3));
                colorList.add(getResources().getColor(R.color.third_course_bg4));
                break;
            case 3:
                colorList.add(getResources().getColor(R.color.fourth_course_bg1));
                colorList.add(getResources().getColor(R.color.fourth_course_bg2));
                colorList.add(getResources().getColor(R.color.fourth_course_bg3));
                colorList.add(getResources().getColor(R.color.fourth_course_bg4));
                break;
            case 4:
                colorList.add(getResources().getColor(R.color.fifth_course_bg1));
                colorList.add(getResources().getColor(R.color.fifth_course_bg2));
                colorList.add(getResources().getColor(R.color.fifth_course_bg3));
                colorList.add(getResources().getColor(R.color.fifth_course_bg4));
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        WhichWeek = position - 1;
        removeAll();
        changeByWeek();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void removeAll() {
        int[] relative_list = new int[]
                {
                        R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday
                };
        for (int i = 0; i < relative_list.length; i++) {
            RelativeLayout layout = rootView.findViewById(relative_list[i]);
            layout.removeAllViews();
        }
    }

}
