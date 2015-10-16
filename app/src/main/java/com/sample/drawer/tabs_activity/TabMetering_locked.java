package com.sample.drawer.tabs_activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Slava-laptop on 05.08.2015.
 */
public class TabMetering_locked extends Activity {
    Unit data = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_metering_locked);
        // �������
        Intent intent = getIntent();
        data = intent.getParcelableExtra("data");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getDeviceNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //initialize();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // ���������
        spinner.setPrompt("������� �����");
        // �������� �������
        spinner.setSelection(0);


        fillTable(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // ���������� ������� �������� ��������
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                // Ite iv = (MenuView.ItemView) parent.getSelectedView();
                // Toast.makeText(getBaseContext(), "Position = " + _dev_name, Toast.LENGTH_SHORT).show();

                fillTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    private void fillTable(int index){
        MeteringDevice md = data.devices.get(index);
        TextView tv1 = (TextView)findViewById(R.id.metering_place);
        TextView tv2= (TextView)findViewById(R.id.metering_type);
        TextView tv3= (TextView)findViewById(R.id.metering_factory_num);
        TextView tv4= (TextView)findViewById(R.id.metering_accuracy);
        TextView tv5= (TextView)findViewById(R.id.metering_next_chek);
        TextView tv6= (TextView)findViewById(R.id.metering_prev_reading);
        TextView tv7= (TextView)findViewById(R.id.metering_prev_reading_date);
        TextView tv8= (TextView)findViewById(R.id.metering_type_reading);
        TextView tv9= (TextView)findViewById(R.id.metering_current_reading);
        TextView tv10= (TextView)findViewById(R.id.metering_comment);
        tv9.setTextColor(Color.BLACK);

        tv1.setText(md.place);
        tv2.setText(md.type);
        tv3.setText(md.factory_num);
        tv4.setText(md.accuracy);
        tv5.setText(md.next_check);
        tv6.setText(md.prev_reading);
        tv7.setText(md.date_reading);
        tv8.setText(md.type_reading);
        try {
            tv9.setText(getCurrentReading(data.FIO+" "+data.account,md.factory_num));
            tv10.setText(getCurrentComment(data.FIO+" "+data.account,md.factory_num));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            int cur = 0;
            int last = 0;
            try{last=Integer.parseInt(tv6.getText().toString().trim());}catch(Exception ex){}
            try {cur = Integer.parseInt(tv9.getText().toString().trim());} catch (Exception ex){}
            if (cur<last){
                tv9.setTextColor(Color.RED);
            }else{
                tv9.setTextColor(Color.GREEN);
            }
        }catch (Exception ex){}



    }

    private String getCurrentReading(String FIOLS, String FN) throws JSONException {
        String out_data = readOutFile().replaceAll("\"\"","\",\"");
        /*if (out_data.split("cur_reading").length==2){
            out_data = out_data.replaceFirst(",","");
        }*/
        JSONArray jarr = new JSONArray(out_data);
        for (int i=0;i<jarr.length();i++){
            JSONObject c = jarr.getJSONObject(i);
            if (c.getString("fiols").equals(FIOLS)&&c.getString("num").equals(FN)){
                return c.getString("cur_reading");
            }
        }
        return "";
    }

    private String getCurrentComment(String FIOLS, String FN) throws JSONException {
        String out_data = readOutFile().replaceAll("\"\"","\",\"");
        /*if (out_data.split("cur_reading").length==2){
            out_data = out_data.replaceFirst(",","");
        }*/
        JSONArray jarr = new JSONArray(out_data);
        for (int i=0;i<jarr.length();i++){
            JSONObject c = jarr.getJSONObject(i);
            if (c.getString("fiols").equals(FIOLS)&&c.getString("num").equals(FN)){
                return c.getString("comment");
            }
        }
        return "";
    }


    private String[] getDeviceNames(){
        ArrayList<MeteringDevice> mds = data.devices;
        String out_data = readOutFile();

        String out[] = out_data.split("\\{");
        ArrayList<String> namesList = new ArrayList<String>();

        for (int i=0; i<mds.size();i++){
            boolean contain = false;
            for (int j=0;j<out.length;j++){
                if (out[j].contains(data.FIO+" "+data.account)){
                    if (out[j].contains(mds.get(i).factory_num)){
                        contain = true;
                    }
                }
            }
            if (contain) {
                namesList.add( mds.get(i).name);
            }
        }
        String[] names = new String[namesList.size()];
        for (int i=0;i<namesList.size();i++){
            names[i] = namesList.get(i);
        }
        return names;
    }

    private String readOutFile(){
        String ret = "";
        try {
            // ��������� ����� ��� ������
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("out_file")));
            String str = "";
            // ������ ����������
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
