package com.example.webviewapp.common.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.webviewapp.common.base.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFormatUtils {
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
     *
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
     * 从本地assets读取json并格式化为JSONArray
     *
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

    /**
     * 从本地assets读取json并格式化为JSONObject
     *
     * @param assetManager
     * @param filename
     * @return
     */
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
     * 从assets得到json文件中的内容
     *
     * @param context
     * @param fileName
     * @return json字符串
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * @param name
     * @return
     */
    public static int findResource(String name) {
        Application app = BaseApplication.getInstance();
        return app.getResources().getIdentifier(
                name, "drawable", app.getPackageName());
    }

    /**
     * 从文件路径读取字符流
     *
     * @param fileName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String text2Show(int len, String str) {
        if (str.length() <= len) return str;
        String res = str.substring(0, len) + "...";
        return res;
    }
}
