package com.example.weather.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.weather.R;

import java.util.List;

public class Adapter extends BaseAdapter {

    List<Suggest> list;
    Context context;

    public Adapter(List<Suggest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHoder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.listview, null);
            viewHoder = new ViewHolder();
            viewHoder.textView = convertView.findViewById(R.id.text);
            viewHoder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHolder) convertView.getTag();
        }
        viewHoder.textView.setText(list.get(position).getName());
        viewHoder.checkBox.setChecked(list.get(position).isSelected());
        return convertView;
    }
}

