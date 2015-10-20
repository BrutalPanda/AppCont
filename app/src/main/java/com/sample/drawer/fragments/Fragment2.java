package com.sample.drawer.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.drawer.MyShitMasterpice.DB;
import com.sample.drawer.R;
import com.sample.drawer.utils.Order;
import com.sample.drawer.utils.OrderAdapter;
import com.sample.drawer.utils.Unit;
import com.sample.drawer.views.Readout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class Fragment2 extends Fragment {

    private ArrayList<Order> m_orders = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    private ArrayList<Unit> Units;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup thiscontainer = container;
        final View rootView =
                inflater.inflate(R.layout.fragment_2, container, false);

        ListView lvf1 = (ListView) rootView.findViewById(R.id.lvMain2);
        DB dbase = new DB(thiscontainer.getContext());
        try {
            Units = dbase.makeUnits(1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(thiscontainer.getContext(), "Ошибка базы", Toast.LENGTH_LONG);
        }
        m_orders = new ArrayList<Order>();
        this.m_adapter = new OrderAdapter(thiscontainer.getContext(), R.layout.listview_row, m_orders);
        lvf1.setAdapter(this.m_adapter);

        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();

        lvf1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(rootView.getContext(), Readout.class);
                /*intent.putExtra("done","true");
                intent.putExtra("Name", m_orders.get(position).getFIO());
                intent.putExtra("position", Integer.toString(position));
                intent.putExtra("account", m_orders.get(position).getAccount());*/
                intent.putExtra("selected_id",m_orders.get(position).getId());
                startActivity(intent);
            }


        });

        return rootView;
    }




    private void getOrders(){
        ArrayList<HashMap<String,String>> data = null;
        try {
            m_orders = new ArrayList<Order>();
                /*for (int i = 0; i < data.size(); i++) {
                    m_orders.add(new Order(data.get(i).get("address"), data.get(i).get("FIO"), data.get(i).get("account")));
                }*/
            for(int i=0;i<Units.size();i++){
                m_orders.add(new Order(Units.get(i).id,Units.get(i).address, Units.get(i).FIO, Units.get(i).account));
            }
            Thread.sleep(20);
            Log.i("ARRAY", "" + m_orders.size());
        } catch (Exception e) {
            Log.e("BACKGROUND_PROC", e.getMessage());
        }
        getActivity().runOnUiThread(returnRes);


    }

    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_orders != null && m_orders.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++)
                    m_adapter.add(m_orders.get(i));
            }

            m_adapter.notifyDataSetChanged();
        }
    };
}

