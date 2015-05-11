package com.xfiler.YujianWeather.util;

/**
 * Created by Administrator on 2015/5/11.
 */
public interface HttpCallbackListener {
    void onFinish( final String response);
    void onError(Exception e);
}
