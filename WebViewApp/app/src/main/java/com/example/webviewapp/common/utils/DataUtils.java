package com.example.webviewapp.common.utils;

import android.app.Application;
import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.webviewapp.common.base.BaseApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DataUtils {
    /**
     * 获取Realm数据库64位秘钥
     *
     * @param key 秘钥字符
     * @return 秘钥
     */
    public static byte[] getRealmKey(String key) {
        StringBuilder newKey = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            newKey.append(key);
        }
        return newKey.toString().getBytes();
    }

    /**
     * 毫秒转成 mm:ss 的字符串形式
     * @param time 毫秒数
     * @return
     */
    public static String time2Str(Long time) {
        if (time == null) return "";

        long seconds = time / 1000;
        long m = seconds / 60, s = seconds % 60;

        String mm = m > 9 ? m + "" : "0" + m;
        String ss = s > 9 ? s + "" : "0" + s;

        return mm + ":" + ss;
    }

    /**
     * 毫秒转成日期的字符串形式 1970年1月1日星期四
     *
     * @param time 毫秒数
     * @return
     */
    public static String time2Date(Long time) {
        if (time == null) return "";
        Date date = new Date();
        date.setTime(time);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        return dateFormat.format(date);
    }

    /**
     * @param assetManager
     * @param filename
     * @return
     */
    public static JSONArray readJSONArray(AssetManager assetManager, String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return JSON.parseArray(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject readJSONObject(AssetManager assetManager, String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return JSON.parseObject(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public static int findResource(String name) {
        Application app = BaseApplication.getInstance();
        return app.getResources().getIdentifier(
                name, "drawable", app.getPackageName());
    }
}
