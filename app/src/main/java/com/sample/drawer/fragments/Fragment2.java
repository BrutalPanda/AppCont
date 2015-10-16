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

import com.sample.drawer.R;
import com.sample.drawer.utils.Order;
import com.sample.drawer.utils.OrderAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup thiscontainer = container;
        final View rootView =
                inflater.inflate(R.layout.fragment_2, container, false);

        ListView lvf1 = (ListView) rootView.findViewById(R.id.lvMain2);
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
                intent.putExtra("done","true");
                intent.putExtra("Name", m_orders.get(position).getFIO());
                intent.putExtra("position", Integer.toString(position));
                intent.putExtra("account", m_orders.get(position).getAccount());

                startActivity(intent);
            }


        });

        return rootView;
    }


    public ArrayList<HashMap<String,String>> loadData() throws JSONException {

        String json_data = readFile();
        String out_data = readOutFile();
        String out[] = out_data.split("\\{");
        JSONArray jarray = new JSONArray(json_data);
        ArrayList<HashMap<String,String>> enities = new ArrayList<HashMap<String,String>>();
        for (int i=1;i<jarray.length();i++){

            HashMap<String,String> enity = new HashMap<String,String>();
            JSONObject c = jarray.getJSONObject(i);
            enity.put("id",Integer.toString(i));
            enity.put("FIO",c.getString("FIO"));
            enity.put("account",c.getString("account"));
            enity.put("address",c.getString("address"));
            boolean cont = false;
            int cnt =0;

            for (int k=0;k<out.length;k++){
                if (out[k].contains(c.getString("FIO")+" "+c.getString("account"))){
                    JSONArray root_devs_json = c.getJSONArray("devices");

                    for (int j=0;j<root_devs_json.length();j++){
                        JSONObject dev_json = root_devs_json.getJSONObject(j);
                        if (out[k].contains(dev_json.getString("factory_num"))){
                            cnt++;
                        }
                        /*MeteringDevice md = new MeteringDevice(dev_json.getString("name"),dev_json.getString("service"),
                                dev_json.getString("place"),dev_json.getString("type"),dev_json.getString("factory_num"),
                                dev_json.getString("accuracy"),dev_json.getString("next_check"),dev_json.getString("prev_reading"),
                                dev_json.getString("type_reading"),dev_json.getString("date_reading"),"");
                        devs.add(md);*/
                    }
                    if (cnt <= root_devs_json.length()){
                        cont = true;
                    }
                }
            }


            if (cont) {
                enities.add(enity);
            }
        }

        return enities;



    }

    public ArrayList<HashMap<String,String>> loadDataDone(){
        String json_data = readFile();
        return null;
    }

    private String readOutFile(){
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getActivity().openFileInput("out_file")));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;

            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    String readFile() {
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getActivity().openFileInput("file")));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void getOrders(){
        ArrayList<HashMap<String,String>> data = null;
        try {
            data = loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (data == null){
            // если чтение не удалось или задание пустые
        }
        else {
            try {
                m_orders = new ArrayList<Order>();
                for (int i = 0; i < data.size(); i++) {
                    m_orders.add(new Order(data.get(i).get("address"), data.get(i).get("FIO"), data.get(i).get("account")));
                }
                Thread.sleep(20);
                Log.i("ARRAY", "" + m_orders.size());
            } catch (Exception e) {
                Log.e("BACKGROUND_PROC", e.getMessage());
            }
            getActivity().runOnUiThread(returnRes);
        }

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

