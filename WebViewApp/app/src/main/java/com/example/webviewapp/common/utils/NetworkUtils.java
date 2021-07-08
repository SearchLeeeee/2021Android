package com.example.webviewapp.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static final int NETWORK_TYPE_INVALID = 0;//网络不可用
    public static final int NETWORK_TYPE_MOBILE = 1;//流量
    public static final int NETWORK_TYPE_WIFI = 2;//wifi

    /**
     * 获取当前网络连接状况
     *
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                return NETWORK_TYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return NETWORK_TYPE_MOBILE;
            }
        }
        return NETWORK_TYPE_INVALID;
    }
}
