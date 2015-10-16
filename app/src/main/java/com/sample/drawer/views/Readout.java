package com.sample.drawer.views;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.sample.drawer.R;
import com.sample.drawer.tabs_activity.TabInfo;
import com.sample.drawer.tabs_activity.TabMetering;
import com.sample.drawer.tabs_activity.TabMetering_locked;
import com.sample.drawer.tabs_activity.TabSaldo;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Saldo;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class Readout extends TabActivity implements View.OnClickListener {
    TextView tvView;
    final String FILENAME = "file";
    final String LOG_TAG = "Readout::::";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.readout);

        tvView = (TextView) findViewById(R.id.account_text);



        tvView.setText(intent.getStringExtra("Name")+" "+intent.getStringExtra("account"));
        Unit data = null;
        try {
            String tmp = intent.getStringExtra("position");
            data = loadData(intent.getStringExtra("Name"),intent.getStringExtra("account"));
            intent.putExtra("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpec;
        Intent int1 = null;
        Intent int2 = null;
        Intent int3 = null;
        if (intent.getStringExtra("done").equals("false")) {
            int1 = new Intent(this, TabInfo.class);
            int1.putExtra("data", data);
            int2 = new Intent(this, TabSaldo.class);
            int2.putExtra("data", data);
            int3 = new Intent(this, TabMetering.class);
            int3.putExtra("data", data);
        }else{
            int1 = new Intent(this, TabInfo.class);
            int1.putExtra("data", data);
            int2 = new Intent(this, TabSaldo.class);
            int2.putExtra("data", data);
            int3 = new Intent(this, TabMetering_locked.class);
            int3.putExtra("data", data);
        }


        tabSpec = tabHost.newTabSpec("info");
        tabSpec.setIndicator("Информация");

        tabSpec.setContent(int1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("saldo");
        tabSpec.setIndicator("Сальдо");
        tabSpec.setContent(int2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Metering");
        tabSpec.setIndicator("Приборы учета");
        tabSpec.setContent(int3);
        tabHost.addTab(tabSpec);
        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @Override
    public void onClick (View v){

    }

    public Unit loadData(String name, String account) throws JSONException {

        String json_data = readFile();
        String out_data = readOutFile();
        JSONArray jarray = new JSONArray(json_data);
            Unit unit = new Unit();
            JSONObject c = null;

            for (int i=1;i<jarray.length();i++) { // от 1 ибо первый элемент - инфа о контролере
                JSONObject tmp = jarray.getJSONObject(i);
                if ((tmp.getString("FIO").equals(name))&&(tmp.getString("account").equals(account))){
                    c = tmp;
                }
               // c = jarray.getJSONObject(i);
            }
            unit.FIO = c.getString("FIO");
            unit.account = c.getString("account");
            unit.address = c.getString("address");
            unit.area = c.getString("area");
            unit.room = c.getString("room");
            unit.number_of_living = c.getString("number_of_living");
            unit.tel1 = c.getString("tel1");
            unit.tel2 = c.getString("tel2");
            unit.tel3 = c.getString("tel3");
            unit.email = c.getString("email");




            JSONArray root_devs_json = c.getJSONArray("devices");
            ArrayList<MeteringDevice> devs = new ArrayList<MeteringDevice>();
            for (int i=0;i<root_devs_json.length();i++){
                JSONObject dev_json = root_devs_json.getJSONObject(i);
                MeteringDevice md = new MeteringDevice(dev_json.getString("name"),dev_json.getString("service"),
                        dev_json.getString("place"),dev_json.getString("type"),dev_json.getString("factory_num"),
                        dev_json.getString("accuracy"),dev_json.getString("next_check"),dev_json.getString("prev_reading"),
                        dev_json.getString("type_reading"),dev_json.getString("date_reading"),"");
                devs.add(md);
            }
            unit.devices = devs;

            JSONObject root_saldo = c.getJSONObject("saldo");
            JSONObject root_saldo_info = root_saldo.getJSONObject("info");
            String count_rows_str = root_saldo_info.getString("count_rows");
            int count_rows = Integer.parseInt(count_rows_str);
            String count_cols_str = root_saldo_info.getString("count_cols");
            int count_cols = Integer.parseInt(count_cols_str);
            ArrayList<String> cells_header = new ArrayList<String>();
            // header
            ArrayList<Saldo> saldo_rows = new ArrayList<Saldo>();
            //JSONArray header = root_saldo.getJSONArray("header");
            JSONObject header = root_saldo.getJSONObject("header");
            for (int j=0;j<count_cols;j++){
                //cells.add(header.getString(Integer.toString(j)));
                cells_header.add(header.getString("i"+j));
            }
            Saldo saldo_header = new Saldo(cells_header);
            saldo_rows.add(saldo_header);

            for (int i=0; i<count_rows;i++){
                ArrayList<String> cells = new ArrayList<String>();
                JSONObject row = root_saldo.getJSONObject("i"+i);
                for (int j=0; j<count_cols;j++){
                    cells.add(row.getString("i"+j));
                }
                Saldo saldo_row = new Saldo(cells);
                saldo_rows.add(saldo_row);
            }

            unit.saldo = saldo_rows;

        return unit;



    }

    String readFile() {
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                ret = ret + str;
               // Log.d(LOG_TAG, "Readed: " + str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String readOutFile(){
        String ret = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("out_file")));
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

}
