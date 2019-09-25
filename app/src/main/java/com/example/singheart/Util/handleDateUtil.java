package com.example.singheart.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.singheart.activity.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Handler;

//数据处理类，含有大量处理数据的函数
public class handleDateUtil {
    public static void handleCourseDate(String source, Context context) {
        ArrayList<String> course_date = RegexUtil.getComplexResulte("(?s)(?<=var teachers).*?(?=var teachers)|(?<=var teachers).*?(?=table0\\.marshalTable)", source);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < course_date.size(); i++) {
            String eachCourse = course_date.get(i);
            String name = RegexUtil.getUniqueResulte("(?<=,\")[^\\d]*?(?=\\(A)", eachCourse, 0);
            String room = RegexUtil.getUniqueResulte("(?<=\\\")[\\u4e00-\\u9fa50-9A-Z()（）-]{1,}(?=\\(浑南校区)|(?<=\\\")[\\u4e00-\\u9fa50-9A-Z()（）]{1,}(?=\\(南湖校区)", eachCourse, 0);
            String week = RegexUtil.getUniqueResulte("(?<=\\\")[\\d]{40,}(?=\\\")", eachCourse, 0);
            String col = RegexUtil.getUniqueResulte("(?<=\\=)[0-7]{1}(?=\\*unitCount\\+)", eachCourse, 0);
            String row = RegexUtil.getUniqueResulte("(?<=\\+)[\\d]{1,}(?=;)", eachCourse, 0);
            int span = RegexUtil.getCount("(?<=\\+)[\\d]{1,}(?=;)", eachCourse);
            Log.e("name",name);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("room",room);
                jsonObject.put("week",week);
                jsonObject.put("col",col);
                jsonObject.put("row",row);
                jsonObject.put("span",String.valueOf(span));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtil.saveString("course_date.json",context,jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //处理用户信息
    //将从教务处获取的信息整理并保存到数据库
    //成功获取信息返回true，失败返回false
    public static boolean handleUserDate(String pageDate, Context context){
        String name = RegexUtil.getUniqueResulte("姓名[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String en_name = RegexUtil.getUniqueResulte("英文名[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String gender= RegexUtil.getUniqueResulte("性别[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String grade= RegexUtil.getUniqueResulte("年级[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String es= RegexUtil.getUniqueResulte("学制[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String type= RegexUtil.getUniqueResulte("项目[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String edu_level= RegexUtil.getUniqueResulte("学历层次[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String college= RegexUtil.getUniqueResulte("院系[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String major= RegexUtil.getUniqueResulte("专业[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String school_area= RegexUtil.getUniqueResulte("所属校区[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String myclass= RegexUtil.getUniqueResulte("所属班级[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String in_time= RegexUtil.getUniqueResulte("入校时间[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        String out_time= RegexUtil.getUniqueResulte("毕业时间[\\s\\S]*?<td>(.*?)</td>",pageDate,1);
        //用户名不为空说明成功获取到用户信息
        if (!name.equals("")){
            SQliteUtil sQliteUtil = new SQliteUtil(context);
            SQLiteDatabase db = sQliteUtil.getReadableDatabase();
            db.execSQL("INSERT INTO UserInfo(name,en_name,gender,grade,es,type,edu_level,college,major,school_area,class,in_time,out_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    , new String[]{name,en_name,gender,grade,es,type,edu_level,college,major,school_area,myclass,in_time,out_time});
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通过月份和日期判断本周是第几周
     * WhichWeek是学校的校历周期
     */
    public static int judgeWhichWeek() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int WhichWeek = -1;
        //2019年秋季学期9月8日开始
        //先获取9月8日是一年的第几天
        int arr[] = {31,28,31,30,31,30,31,31,30,31,30,31};
        int sum = 0;
        for(int i = 0;i < 9-1; i++) {
            sum += arr[i];
        }
        int init_day_in_year = sum +8;
        int day_in_year = c.get(Calendar.DAY_OF_YEAR);  //获取今天是一年的第几天

        if (day_in_year >= init_day_in_year){
            WhichWeek = (day_in_year - init_day_in_year)/7;  //获取校历的第几周
            if (WhichWeek > 24){
                WhichWeek = -1; //2019年秋季学期只有24周
            }
        } else {
            WhichWeek = -1;
        }
        return WhichWeek;
    }

    //判断今天是星期几
    public static int get_day_in_week(){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int week = c.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    //处理校园网使用情况
    public static Bundle handleNetInfo(String content){
        String[] strArray = content.split("[,]");
        DecimalFormat decimalFormat =new DecimalFormat("0.00");
        Bundle bundle = new Bundle();
        bundle.putString("sum_bytes",decimalFormat.format(Float.parseFloat(strArray[0])/1000000) + "M");
        float second = Float.parseFloat(strArray[1]);
        int hour = (int)second / 3600;
        int min = (int)((second % 3600)/60);
        bundle.putString("sum_seconds",hour +"时" + min + "分");
        bundle.putString("user_balance","￥" + strArray[2]);
        bundle.putString("user_ip",strArray[5]);
        return bundle;
    }
    
}
