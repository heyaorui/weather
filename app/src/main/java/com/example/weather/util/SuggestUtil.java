package com.example.weather.util;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.weather.WeatherActivity;
import com.example.weather.gson.Lifestyle;
import com.example.weather.gson.Weather;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SuggestUtil {

    private static String now;
    private static Context context;

    public static List<Lifestyle> lifeSuggest(Weather weather,Context context1) {

        now = weather.getNow().getCond_txt();
        context=context1;


        List<Lifestyle> suggests = new ArrayList<>();

        List<Suggest> list = DataSupport.findAll(Suggest.class);
        for (Suggest suggest : list
                ) {
            if (suggest.isSelected()) {
                Lifestyle lifestyle = getLifestyle(suggest);

                if (lifestyle != null) {
                    suggests.add(lifestyle);
                }
            }
        }
        return suggests;
    }

    /*给出当前生活指数的建议*/
    private static Lifestyle getLifestyle(final Suggest suggest) {
        Lifestyle lifestyle = new Lifestyle();

        List<SuggestGrade> terms = DataSupport.where("type=?",
                String.valueOf(suggest.getName())).find(SuggestGrade.class);
        if(terms.size()>0){
            for (SuggestGrade s : terms
                    ) {
                List<String> term = new ArrayList<>();
                Collections.addAll(term, s.getTerm().split("-"));
                if (term.contains(now)) {
                    lifestyle.setType(s.getType());
                    lifestyle.setBrf(s.getBrf());
                    lifestyle.setTxt(s.getTxt());
                    return lifestyle;
                }
            }
        }else{
            String address="http://10.200.0.80:8080/getAllSuggestGrade";
            HttpUtil.sendOKHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(context,"加载失败",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText=response.body().string();
                    Utility.handleSuggestGradeResponse(responseText);
                    Looper.prepare();
                    Toast.makeText(context,"请刷新重试",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            });
        }
        return null;
    }
}
