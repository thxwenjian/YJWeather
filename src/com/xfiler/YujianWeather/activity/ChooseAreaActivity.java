package com.xfiler.YujianWeather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.xfiler.YujianWeather.R;
import com.xfiler.YujianWeather.db.YujianWeatherDB;
import com.xfiler.YujianWeather.modeal.City;
import com.xfiler.YujianWeather.modeal.County;
import com.xfiler.YujianWeather.modeal.Province;
import com.xfiler.YujianWeather.util.HttpCallbackListener;
import com.xfiler.YujianWeather.util.HttpUtil;
import com.xfiler.YujianWeather.util.Utility;
import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private YujianWeatherDB yujianWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    //当前选中的级别
    private int currentLevel=0;


    public static final int GET_WEATHER = 1;

    private String response = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_WEATHER:
                    ((TextView)findViewById(R.id.tv_weather)).setText(response);
                    break;
            }
        }
    };
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(currentLevel==LEVEL_COUNTY&&prefs.getBoolean("city_selected",false)){
            Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        Button btn= (Button) findViewById(R.id.btn_weather);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {

                            URL url = new URL("http://www.weather.com.cn/adat/cityinfo/101050605.html");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(10000);
                            connection.setReadTimeout(10000);
                            connection.setDoInput(true);
//                    connection.setDoOutput(true);
                            if (connection.getResponseCode() ==200) {

                            } else {
                            }
                            InputStream in = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder rs = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                rs.append(line);
                            }

                             response=rs.toString();

//                            URL url = new URL("http://www.weather.com.cn/adat/cityinfo/101050605.html");
//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                            connection.setDoInput(true);
//                            if (connection.getResponseCode() == HttpStatus.SC_OK) {
//                                InputStream inputStream = connection.getInputStream();
//                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//                                String s = "";
//                                while ((s = br.readLine()) != null) {
//                                    response += s;
//                                }
//                            }else{
//                                response = "请求失败！";
//                            }
                            handler.sendEmptyMessage(GET_WEATHER);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });



        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        yujianWeatherDB = YujianWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                }else
                if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    //点击进入天气界面
                    String countyCode=countyList.get(i).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    //查询全国所有的省,优先从数据库中查找，如果没有查询到再去服务器查询
    public void queryProvinces() {
        provinceList = yujianWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    //查询全国所有的市,优先从数据库中查找，如果没有查询到再去服务器查询
    public void queryCities() {
        cityList = yujianWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    //查询全国所有的县,优先从数据库中查找，如果没有查询到再去服务器查询
    public void queryCounties() {
        countyList = yujianWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County c : countyList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    //根据传入代号和类型从服务器上查找省市县
    public void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(yujianWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(yujianWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(yujianWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    //通过runOnUIThread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                                ;
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度条
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 隐藏进度条
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据级别返回市列表/省列表/销毁
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
