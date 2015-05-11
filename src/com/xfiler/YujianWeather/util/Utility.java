package com.xfiler.YujianWeather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.xfiler.YujianWeather.db.YujianWeatherDB;
import com.xfiler.YujianWeather.modeal.City;
import com.xfiler.YujianWeather.modeal.County;
import com.xfiler.YujianWeather.modeal.Province;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2015/5/11.
 * 解析和处理服务器返回的数据
 */
public class Utility {
    //处理省级数据
    public synchronized static boolean handleProvinceResponse(YujianWeatherDB yujianWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //
                    yujianWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    //处理市级数据
    public synchronized static boolean handleCityResponse(YujianWeatherDB yujianWeatherDB, String response,int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String p : allCities) {
                    String[] array = p.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //
                    yujianWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    //处理县级数据
    public synchronized static boolean handleCountyResponse(YujianWeatherDB yujianWeatherDB, String response,int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String p : allCounties) {
                    String[] array = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //
                    yujianWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

/**
 * 解析服务器返回的JSON数据，并将解析的数据存储到本地
 */
    public static void handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherObject=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherObject.getString("city");
            String weatherCode=weatherObject.getString("cityid");
            String temp1=weatherObject.getString("temp1");
            String temp2=weatherObject.getString("temp2");
            String weatherDesp=weatherObject.getString("weather");
            String publishTime=weatherObject.getString("ptime");
           saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
/**
 * 将服务器返回的天气存储到SharedPreferences文件
 */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat format=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date",format.format(new Date()));
        editor.commit();

    }
}
