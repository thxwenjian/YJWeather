package com.xfiler.YujianWeather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xfiler.YujianWeather.R;
import com.xfiler.YujianWeather.util.HttpCallbackListener;
import com.xfiler.YujianWeather.util.HttpUtil;
import com.xfiler.YujianWeather.util.Utility;

/**
 * Created by Administrator on 2015/5/11.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        String countycode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countycode)) {
            //有县级代号就去查询天气
            publishText.setText("同步中....");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countycode);
        } else {
            //没有县级代号就直接显示本地天气
            showWeather();
        }
    }

    /**
     * 查询县级代号所对应的天气代号
     */
    public void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气代号所对应的天气
     */
    public void queryWeatherInfo(String weatherCode) {
            String address = "http://www.weather.com.cn/data/cityinfo/"+ weatherCode + ".html";
//        String address="http://www.weather.com.cn/adat/cityinfo/101050605.html";
        queryFromServer(address, "weatherCode");
    }

    public void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    String[] array = response.split("\\|");
                    if (array != null && array.length == 2) {
                        String weatherCode = array[1];
                        queryWeatherInfo(weatherCode);
                    }
                }
                else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityText.setVisibility(View.VISIBLE);
    }
}
