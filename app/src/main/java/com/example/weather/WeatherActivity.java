package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.gson.Daily_forecast;
import com.example.weather.gson.Lifestyle;
import com.example.weather.gson.Weather;
import com.example.weather.service.AutoUpdateService;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.LocationUtils;
import com.example.weather.util.SuggestUtil;
import com.example.weather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private Button navButton;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView humText;
    private TextView flText;
    private TextView visText;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private LinearLayout lifeLayout;
    private TextView winddirText;
    private TextView windscText;
    String weatherId;
    private ImageView bingPicImg;
    private Button navSetting;
    private Button navPosition;
    private boolean flag;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        context = getApplicationContext();
        //初始化各控件
        weatherLayout=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);
        humText=findViewById(R.id.hum_text);
        flText=findViewById(R.id.fl_text);
        visText=findViewById(R.id.vis_text);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        lifeLayout=findViewById(R.id.life_layout);
        winddirText=findViewById(R.id.wind_dir_text);
        windscText=findViewById(R.id.wind_sc_text);
        bingPicImg=findViewById(R.id.bing_pic_img);
        swipeRefresh=findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout=findViewById(R.id.drawer_layout);
        navButton=findViewById(R.id.nav_button);
        navSetting=findViewById(R.id.nav_setting);
        navPosition=findViewById(R.id.nav_location);


        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);

        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather=Utility.handleWeatherResponse(weatherString);
            weatherId=weather.getBasic().getCid();
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
            }
        });
        navPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    getLocation();
                } else {
                    Toast.makeText(context, "no permission", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 采用最好的方式获取定位信息
     */
    private void getLocation() {
        Location location = LocationUtils.getGPSLocation(this);
        if (location == null) {
            Toast.makeText(context, "请打开定位服务并稍等片刻", Toast.LENGTH_SHORT).show();
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtils.addLocationListener(context, LocationManager.GPS_PROVIDER, new LocationUtils.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        String weatherId=location.getLongitude()+","+location.getLatitude();
                        requestWeather(weatherId);
                        Toast.makeText(context, "gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "gps location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String weatherId=location.getLongitude()+","+location.getLatitude();
            requestWeather(weatherId);
            Toast.makeText(this, "best location: lat==" + location.getLatitude() + " lng==" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }
    /*加载必应每日一图*/
    private void loadBingPic() {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(context).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(context).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /*根据天气id请求城市天气信息*/
    public void requestWeather(String weatherId) {

        String weatherUrl="https://free-api.heweather.net/s6/weather?location="+weatherId+
                "&key=6ca4eafd180f4f609823a1c08a8ccf42";


        weatherLayout.setVisibility(View.INVISIBLE);

        Intent intent=new Intent(this,AutoUpdateService.class);
        startService(intent);
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.getStatus())){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(context).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(context,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        this.weatherId=weatherId;
        loadBingPic();
    }
    /*处理并展示Weather实体类中的数据*/
    private void showWeatherInfo(Weather weather) {
        if(weather!=null&&"ok".equals(weather.getStatus())){
            String cityName=weather.getBasic().getLocation();
            String updateTime=weather.getUpdate().getLoc().split(" ")[1];
            String hum="相对湿度 "+weather.getNow().getHum();
            String fl="体感温度 "+weather.getNow().getFl();
            String vis="能 见 度 "+weather.getNow().getVis();
            String degree=weather.getNow().getTmp()+"℃";
            String weatherInfo=weather.getNow().getCond_txt();
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            humText.setText(hum);
            flText.setText(fl);
            visText.setText(vis);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for(Daily_forecast forecast:weather.getDaily_forecast()){
                View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,
                        forecastLayout,false);
                TextView dateText=view.findViewById(R.id.date_text);
                TextView infoText=view.findViewById(R.id.info_text);
                TextView maxText=view.findViewById(R.id.max_text);
                TextView minText=view.findViewById(R.id.min_text);
                dateText.setText(forecast.getDate());
                infoText.setText(forecast.getCond_txt_d());
                maxText.setText(forecast.getTmp_max());
                minText.setText(forecast.getTmp_min());
                forecastLayout.addView(view);
            }

            winddirText.setText(weather.getNow().getWind_dir());
            windscText.setText(weather.getNow().getWind_sc()+"级");

            lifeLayout.removeAllViews();
            List<Lifestyle>lifestyleList = SuggestUtil.lifeSuggest(weather,context);
            for (Lifestyle lifestyle:lifestyleList) {
                View view=LayoutInflater.from(this).inflate(R.layout.lifestyle_item,
                        lifeLayout,false);
                TextView lifeText=view.findViewById(R.id.life_text);
                String life=lifestyle.getType()+":"+lifestyle.getBrf()+"  "+lifestyle.getTxt();
                lifeText.setText(life);
                lifeLayout.addView(view);
            }
            weatherLayout.setVisibility(View.VISIBLE);

            Intent intent=new Intent(this,AutoUpdateService.class);
            startService(intent);
        }else{
            Toast.makeText(context,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //swipeRefresh.setRefreshing(true);
        Toast.makeText(context,"请刷新重试",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPermission();//针对6.0以上版本做权限适配
    }
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
    }
    /**
     * 权限的结果回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            flag = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
