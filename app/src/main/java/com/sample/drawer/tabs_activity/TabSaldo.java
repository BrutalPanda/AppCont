package com.sample.drawer.tabs_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sample.drawer.R;
import com.sample.drawer.utils.Saldo;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class TabSaldo extends Activity {
    Unit data = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_saldo);

        Intent intent = getIntent();
        data = intent.getParcelableExtra("data");
        ArrayList <Saldo> rows = data.saldo;
        TableLayout table = (TableLayout) findViewById(R.id.saldo_table);

        for (int i=0;i<rows.size();i++) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(params);
            TableRow.LayoutParams param = new TableRow.LayoutParams();
            param.setMargins(1, 1, 1, 1);
            Saldo row = rows.get(i);
            for (int j=0;j<row.getSize();j++){
                TextView reson = new TextView(this);

                reson.setText(row.cell.get(j));
                reson.setLayoutParams(param);
                reson.setBackgroundColor(Color.rgb(255, 255, 255));
                reson.setTextColor(Color.rgb(0, 0, 0));

                if ((i>0)&&(j==1)){
                    try {
                        int val = Integer.parseInt(row.cell.get(j).replaceAll(" ","").split(",")[0]);
                        if (val>0){
                            reson.setBackgroundColor(Color.rgb(255, 203, 219));
                        }
                    }catch (Exception ex){

                    }
                }
                tableRow.addView(reson);
            }
            table.addView(tableRow, i);
        }


    }





}
