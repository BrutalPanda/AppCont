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
import android.widget.Toast;

import com.sample.drawer.MyShitMasterpice.DB;
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
    boolean noDevices = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.readout);

        tvView = (TextView) findViewById(R.id.account_text);


        DB dbase = new DB(this);

        Unit data = null;
        try {
            String tmp = intent.getStringExtra("position");
            //data = loadData(intent.getStringExtra("Name"),intent.getStringExtra("account"));
            int id = 0;
            id = intent.getIntExtra("selected_id",id);
            data = dbase.getUnit(id);
            intent.putExtra("data",data);
            tvView.setText(data.FIO+" "+data.account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpec;
        Intent int1 = null;
        Intent int2 = null;
        Intent int3 = null;
        if (data.devices==null){
            noDevices = true;
            Toast.makeText(this.getBaseContext(), "У текущего ЛС нет приборов учета!", Toast.LENGTH_LONG).show();
        }
        if (data.passed==0) {
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

        if(!noDevices) {
            tabSpec = tabHost.newTabSpec("Metering");
            tabSpec.setIndicator("Приборы учета");
            tabSpec.setContent(int3);
            tabHost.addTab(tabSpec);
        }

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




}
