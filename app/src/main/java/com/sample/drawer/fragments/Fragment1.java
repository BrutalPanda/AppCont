package com.sample.drawer.fragments;

import android.app.ExpandableListActivity;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.io.File;
import java.io.FileInputStream;
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
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sample.drawer.MainActivity;
import com.sample.drawer.MyShitMasterpice.DB;
import com.sample.drawer.MyShitMasterpice.GPSTracker;

import com.sample.drawer.MyShitMasterpice.OpenFileDialog;
import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Order;
import com.sample.drawer.utils.OrderAdapter;
import com.sample.drawer.utils.Unit;
import com.sample.drawer.utils.Utils;
import com.sample.drawer.views.Readout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Fragment1 extends Fragment {

    private static final String TAG = "myLogs";
    private ArrayList<String> strings = new ArrayList<String>();
    String FILENAME = "";
    ProgressDialog pd;
    int cnt;
    Handler h;
    private ArrayList<Order> m_orders = null;
    private OrderAdapter m_adapter;
    private OrderAdapter m_adapter_dub;
    private Runnable viewOrders;
    private ArrayList<Unit> Units;
    private AutoCompleteTextView search;

    final String LOG_TAG = "Fragment1::::";

    public Fragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup thiscontainer = container;
        final View rootView =
                inflater.inflate(R.layout.fragment_1, container, false);

        ListView lvf1 = (ListView) rootView.findViewById(R.id.lvMain);
        DB dbase = new DB(thiscontainer.getContext());
        h = new Handler();

        pd = new ProgressDialog(thiscontainer.getContext());
        pd.setTitle("Заполнение базы данных");
        pd.setMessage("Пожалуйста дождитесь окончания операции!");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        if (dbase.isEmpty()) {
            Toast.makeText(thiscontainer.getContext(), "База данных пуста! Выберите файл для загрузки.", Toast.LENGTH_LONG).show();
            OpenFileDialog fileDialog = new OpenFileDialog(thiscontainer.getContext());
            fileDialog.setOpenDialogListener(new OpenFileDialog.OpenDialogListener(){
                @Override
                public void OnSelectedFile(String fileName){
                    FILENAME = fileName;
                    final DB dbase = new DB(thiscontainer.getContext());
                    //Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();
                    try {
                        String source = readFile();
                        dbase.clearData();
                        final JSONArray jarr = dbase.getSource(source);
                        boolean pre = dbase.preFillRecord(jarr.getJSONObject(0));

                        pd.setMax(jarr.length() - 1);
                        //pd.setIndeterminate(true);
                        pd.show();

                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 1; i < jarr.length(); i++) {
                                    Log.d("array:::", "Элемент = " + i);
                                    cnt = i;
                                    try {
                                        dbase.fillRecord(jarr.getJSONObject(i));
                                    } catch (Exception ex) {
                                    }
                                    //try{TimeUnit.MILLISECONDS.sleep(100);}catch(Exception ex){ex.printStackTrace();};
                                    h.post(updateProgress);
                                }
                            }
                        });

                        t.start();
                        ListView lvf1 = (ListView) rootView.findViewById(R.id.lvMain);

                        try {
                            Units = dbase.makeUnits(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(thiscontainer.getContext(),"Ошибка базы",Toast.LENGTH_LONG);
                        }
                        m_orders = new ArrayList<Order>();

                        m_adapter = new OrderAdapter(thiscontainer.getContext(), R.layout.listview_row, m_orders);
                        m_adapter_dub = m_adapter;

                        search = (AutoCompleteTextView) rootView.findViewById(R.id.search);
                        lvf1.setAdapter(m_adapter);




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

                       // ArrayAdapter<String> common = new ArrayAdapter<String>(thiscontainer.getContext(),R.layout.search_common_row,getCommonStrings());
                       // search.setAdapter(common);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
            fileDialog.show();


        }else {


            try {
                Units = dbase.makeUnits(0);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(thiscontainer.getContext(), "Ошибка базы", Toast.LENGTH_LONG);
            }
            m_orders = new ArrayList<Order>();
            this.m_adapter = new OrderAdapter(thiscontainer.getContext(), R.layout.listview_row, m_orders);
            this.m_adapter_dub = this.m_adapter;


            lvf1.setAdapter(this.m_adapter);


            viewOrders = new Runnable() {
                @Override
                public void run() {
                    getOrders();
                }
            };
            Thread thread = new Thread(null, viewOrders, "MagentoBackground");
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
        search = (AutoCompleteTextView) rootView.findViewById(R.id.search);
        ArrayAdapter<String> common = new ArrayAdapter<String>(thiscontainer.getContext(), R.layout.search_common_row, getCommonStrings());
        search.setAdapter(common);

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
                /*intent.putExtra("done", "false");
                intent.putExtra("Name", m_orders.get(position).getFIO());
                intent.putExtra("position", Integer.toString(position));
                intent.putExtra("account", m_orders.get(position).getAccount());
                 */
                intent.putExtra("selected_id",m_orders.get(position).getId());
                startActivity(intent);
            }


        });



        return rootView;
    }


    Runnable updateProgress = new Runnable(){
        public void run(){
            if (cnt<pd.getMax()){
                pd.setProgress(cnt);
            }else{
                pd.hide();
            }

            //pbCount.setProgress(cnt);
        }
    };

    private String[] getCommonStrings (){
        String [] ret = new String[strings.size()];
         for (int i=0;i<strings.size();i++){
             ret[i] = strings.get(i);
         }
        return ret;
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


    String readFile() {
        String ret = "";
        try {
            File source = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "file");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;
                Log.d(LOG_TAG, "Readed: " + str);
            }
            return ret;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return ret;
    }





}
