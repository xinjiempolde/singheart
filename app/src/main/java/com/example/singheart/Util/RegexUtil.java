package com.example.singheart.Util;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    //如何结果唯一的话，使用该函数
    //flag=0表示没有分组
    //flag=1表示分组为1
    public static String getUniqueResulte(String rule, String source, int flag) {
        Pattern pattern = Pattern.compile(rule);
        Matcher groupList = pattern.matcher(source);
        if (groupList.find()) {
            return groupList.group(flag);
        } else {
            return "";
        }

    }

    //如果匹配结果有多个，使用该函数
    public static ArrayList<String> getComplexResulte(String rule, String source) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rule);
        Matcher groupList = pattern.matcher(source);
        while (groupList.find()) {
            Log.e("find", "find");
            result.add(groupList.group(0));
        }
        return result;
    }

    //如果有多个匹配结果，返回匹配结果的数目
    public static int getCount(String rule, String source) {
        int count = 0;
        Pattern pattern = Pattern.compile(rule);
        Matcher groupList = pattern.matcher(source);
        while (groupList.find()){
            count++;
        }
        return count;
    }
}
