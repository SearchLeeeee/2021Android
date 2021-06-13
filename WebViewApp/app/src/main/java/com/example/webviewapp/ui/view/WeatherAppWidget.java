package com.example.webviewapp.ui.view;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.example.webviewapp.R;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherAppWidget extends AppWidgetProvider {
    private static final String TAG = "WeatherAppWidget";
    private static int updateTimes = 0;
    private final LocalDayWeatherForecast mLocalDayWeatherForecastToday = new LocalDayWeatherForecast();
    private final LocalDayWeatherForecast mLocalDayWeatherForecastTomorrow = new LocalDayWeatherForecast();
    public AMapLocationClientOption mLocationOption = null;
    private String city;
    private String date;
    private String temperature;
    private String tomorrowTemperature;
    private String weather;
    private String tomorrowWeather;
    private AMapLocationClient mLocationClient = null;
    private final AMapLocationListener mLocationListener = aMapLocation -> {
        if (null == aMapLocation) return;
        if (aMapLocation.getErrorCode() == 0) {
            //可在其中解析amapLocation获取相应内容。
            if (null == city) {
                city = aMapLocation.getCity();
                Log.d(TAG, "城市为: " + city);
                date = mLocalDayWeatherForecastToday.getDate();
                temperature = mLocalDayWeatherForecastToday.getDayTemp();
                tomorrowTemperature = mLocalDayWeatherForecastTomorrow.getDayTemp();
                weather = mLocalDayWeatherForecastToday.getDayWeather();
                tomorrowWeather = mLocalDayWeatherForecastTomorrow.getDayWeather();
            }
            mLocationClient.onDestroy();
        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e(TAG, "location Error, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
        }
    };

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            initTextView(context, appWidgetManager, appWidgetId);
            Log.d(TAG, "onUpdate: ");
        }
    }

    private void initTextView(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_app_widget);
        views.setTextViewText(R.id.location, city + updateTimes);
        views.setTextViewText(R.id.date, date);
        views.setTextViewText(R.id.temperature, temperature);
        views.setTextViewText(R.id.tomorrow_temperature, tomorrowTemperature);
        views.setTextViewText(R.id.weather, weather);
        views.setTextViewText(R.id.tomorrow_weather, tomorrowWeather);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        updateTimes += 1;
        Log.d(TAG, "initTextView: " + city);
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        mLocalDayWeatherForecastToday.setDate(new Date().toString());
        mLocalDayWeatherForecastTomorrow.setDate("2021年6月12日");
        initLocation(context);

    }

    private void initLocation(Context context) {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        // 设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
        //开始定位
        mLocationClient.startLocation();
    }


    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}