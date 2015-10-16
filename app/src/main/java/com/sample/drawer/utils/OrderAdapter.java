package com.sample.drawer.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sample.drawer.R;

import java.util.ArrayList;

/**
 * Created by Slava-laptop on 29.07.2015.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    private ArrayList<Order> items;
    private Context thisContext;
    public OrderAdapter(Context context, int textViewResourceId, ArrayList<Order> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.thisContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_row, null);
        }
        Order o = items.get(position);
        if (o != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("ФИО: "+o.getFIO());                            }
            if(bt != null){
                bt.setText("Адрес: "+ o.getAddress());
            }
        }
        return v;
    }
}