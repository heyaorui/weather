package com.example.weather;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.weather.util.Adapter;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Suggest;
import com.example.weather.util.Utility;
import com.example.weather.util.ViewHolder;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {

    private Button quanxuan;
    private Button fanxuan;
    private Button quanbuxuan;
    private ListView listView;
    private BaseAdapter adapter;
    private List<Suggest> suggestList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        quanxuan = findViewById(R.id.button1);
        fanxuan = findViewById(R.id.button2);
        quanbuxuan = findViewById(R.id.button3);

        listView = findViewById(R.id.list_item);

        suggestList = DataSupport.findAll(Suggest.class);
        if (suggestList.size() > 0) {
            adapter=new Adapter(suggestList,SettingActivity.this);
            listView.setAdapter(adapter);
        } else {
            String url = "http://10.200.0.80:8080/getAllSuggest";
            HttpUtil.sendOKHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    boolean result = Utility.handleSuggestResponse(responseText);
                    if (result) {
                        suggestList = DataSupport.findAll(Suggest.class);
                        adapter=new Adapter(suggestList,SettingActivity.this);
                        listView.setAdapter(adapter);
                    }
                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder= (ViewHolder) view.getTag();
                viewHolder.checkBox.toggle();
                suggestList.get(position).setSelected(!suggestList.get(position).isSelected());
            }
        });

        quanxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < suggestList.size() ; i++) {
                    suggestList.get(i).setSelected(true);
                }
                adapter.notifyDataSetChanged();
            }
        });
        fanxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < suggestList.size() ; i++) {
                    suggestList.get(i).setSelected(!suggestList.get(i).isSelected());
                }
                adapter.notifyDataSetChanged();
            }
        });
        quanbuxuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < suggestList.size() ; i++) {
                    suggestList.get(i).setSelected(false);
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onDestroy() {
        DataSupport.saveAll(suggestList);
        super.onDestroy();
    }
}
