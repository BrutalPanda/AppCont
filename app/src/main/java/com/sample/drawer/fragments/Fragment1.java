package com.sample.drawer.fragments;

import android.app.ExpandableListActivity;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sample.drawer.MainActivity;
import com.sample.drawer.MyShitMasterpice.GPSTracker;

import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Order;
import com.sample.drawer.utils.OrderAdapter;
import com.sample.drawer.utils.Utils;
import com.sample.drawer.views.Readout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Fragment1 extends Fragment {

    private static final String TAG = "myLogs";
    private ArrayList<String> strings = new ArrayList<String>();

    private ArrayList<Order> m_orders = null;
    private ArrayList<Order> m_orders_dub = null;
    private OrderAdapter m_adapter;
    private OrderAdapter m_adapter_dub = null;
    private Runnable viewOrders;
    private Drawer.Result drawerResult = null;
    private AccountHeader.Result headerResult = null;
    private AutoCompleteTextView search;
    private boolean key_is_down = false;

    final String FILENAME = "file";
    final String LOG_TAG = "MainActivity::::";

    public Fragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup thiscontainer = container;
        final View rootView =
                inflater.inflate(R.layout.fragment_1, container, false);

        ListView lvf1 = (ListView) rootView.findViewById(R.id.lvMain);
        m_orders = new ArrayList<Order>();
        this.m_adapter = new OrderAdapter(thiscontainer.getContext(), R.layout.listview_row, m_orders);
        this.m_adapter_dub = this.m_adapter;

        search = (AutoCompleteTextView) rootView.findViewById(R.id.search);
        lvf1.setAdapter(this.m_adapter);




        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> common = new ArrayAdapter<String>(thiscontainer.getContext(),R.layout.search_common_row,getCommonStrings());
        search.setAdapter(common);

        /*search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (key_is_down) {
                    AutoCompleteTextView tv = (AutoCompleteTextView) rootView.findViewById(R.id.search);
                    String text = tv.getText().toString();
                    for (int i = 0; i < m_orders.size(); i++) {
                        String adr = m_orders.get(i).getAddress();
                        String FIO = m_orders.get(i).getFIO();
                        if (!(adr.toLowerCase().contains(text.toLowerCase()) || FIO.toLowerCase().contains(text.toLowerCase()))) { // проблема в том, чтобы восстанавливать все, когда поле пустеет
                            Order tmp = m_orders.get(i);
                            m_orders.remove(i);
                            m_adapter.remove(tmp);
                        }
                    }
                } else {
                    key_is_down = true;
                }
                return false;
            }
        });*/


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                //m_adapter = m_adapter_dub;
                //thiscontainer.cl
                //m_adapter = new OrderAdapter(thiscontainer.getContext(), R.layout.listview_row, m_orders);
                m_adapter.clear();
                viewOrders = new Runnable(){
                    @Override
                    public void run() {
                        getOrders();
                    }
                };
                Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (text.trim().equals("")) {

                }else {
                    for (int i = 0; i < m_orders.size(); i++) {
                        String adr = m_orders.get(i).getAddress();
                        String FIO = m_orders.get(i).getFIO();
                        if (!(adr.toLowerCase().contains(text.toLowerCase()) || FIO.toLowerCase().contains(text.toLowerCase()))) { // проблема в том, чтобы восстанавливать все, когда поле пустеет
                            Order tmp = m_orders.get(i);
                            m_orders.remove(i);
                            m_adapter.remove(tmp);
                        }
                    }
                }
            }
        });
        /*m_ProgressDialog = ProgressDialog.show(SoftwarePassionView.this,
                "Please wait...", "Retrieving data ...", true);*/

        // ������� �������
        //ArrayAdapter<String> adapter;
       // adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, names);
       // ArrayAdapter<String> adapter2;
        //adapter2 = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simp, names);
        // ����������� ������� ������
       // lvf1.setAdapter(adapter);

        /*lvf1.OnItemClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (rootView.getContext(),Readout.class);
                intent.putExtra("Name",lvf1.Select);
                intent.putExtra("id","id");
                startActivity(intent);

            }
        };*/



        lvf1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "clicked");
                Intent intent = new Intent(rootView.getContext(), Readout.class);
                intent.putExtra("done", "false");
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
            enity.put("id", Integer.toString(i));
            enity.put("FIO", c.getString("FIO"));

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
                    if (cnt == root_devs_json.length()){
                        cont = true;
                    }
                }
            }


            if (!cont) {
                strings.add(c.getString("FIO"));
                strings.add(c.getString("address"));
                enities.add(enity);
            }
        }

        return enities;



    }

    private String[] getCommonStrings (){
        String [] ret = new String[strings.size()];
         for (int i=0;i<strings.size();i++){
             ret[i] = strings.get(i);
         }
        return ret;
    }

    String readFile() {
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getActivity().openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;
                Log.d(LOG_TAG, "Readed: "+str);
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
                Log.d(LOG_TAG, "Readed: "+str);
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }





}
