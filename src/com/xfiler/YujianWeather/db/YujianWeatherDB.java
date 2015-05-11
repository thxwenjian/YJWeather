package com.xfiler.YujianWeather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.xfiler.YujianWeather.modeal.City;
import com.xfiler.YujianWeather.modeal.County;
import com.xfiler.YujianWeather.modeal.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/11.
 * 数据库操作 封装类
 */
public class YujianWeatherDB {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "yujian_weather";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;
    private static YujianWeatherDB yujianWeatherDB;
    private SQLiteDatabase db;

    private YujianWeatherDB(Context context) {
        YujianWeatherOpenhelper openhelper = new YujianWeatherOpenhelper(context, DB_NAME, null, VERSION);
        db = openhelper.getWritableDatabase();
    }

    /**
     * 获取YujianWeatherDB实例
     */
    public synchronized static YujianWeatherDB getInstance(Context context) {
        if (yujianWeatherDB == null) {
            yujianWeatherDB = new YujianWeatherDB(context);
        }
        return yujianWeatherDB;
    }

    /**
     * 将Province存储到数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();

        Cursor cursor = db.query("Province", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) do {
            Province p = new Province();
            p.setId(cursor.getInt(cursor.getColumnIndex("id")));
            p.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            p.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(p);

        } while (cursor.moveToNext());
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将City存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();

        Cursor cursor = db.query("City", null, "province_id=?",
                new String[]{String.valueOf(provinceId)}, null, null, null, null);
        if (cursor.moveToFirst()) do {
            City c = new City();
            c.setId(cursor.getInt(cursor.getColumnIndex("id")));
            c.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            c.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            c.setProvinceId(provinceId);
            list.add(c);

        } while (cursor.moveToNext());
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
    /**
     * 将Province存储到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }
    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();

        Cursor cursor = db.query("County", null, "city_id=?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);
        if (cursor.moveToFirst()) do {
            County c = new County();
            c.setId(cursor.getInt(cursor.getColumnIndex("id")));
            c.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            c.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            c.setCityId(cityId);
            list.add(c);

        } while (cursor.moveToNext());
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
