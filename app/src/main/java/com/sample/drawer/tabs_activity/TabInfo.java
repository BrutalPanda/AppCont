package com.sample.drawer.tabs_activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.drawer.MyShitMasterpice.DB;
import com.sample.drawer.R;
import com.sample.drawer.utils.MeteringDevice;
import com.sample.drawer.utils.Unit;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Slava-laptop on 27.07.2015.
 */
public class TabInfo extends Activity implements View.OnClickListener {
    ImageView t1;
    ImageView t2;
    ImageView t3;
    ImageView em;
    Unit data = null;
    DB dbase = new DB(this);

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_info);
        Intent intent = getIntent();
        Object obj = intent.getExtras();

        data = intent.getParcelableExtra("data");
        fillTable();

        t1 = (ImageView) findViewById(R.id.account_tel1_img);
        t2 = (ImageView) findViewById(R.id.account_tel2_img);
        t3 = (ImageView) findViewById(R.id.account_tel3_img);
        em = (ImageView) findViewById(R.id.account_email_img);

        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        t3.setOnClickListener(this);
        em.setOnClickListener(this);

        findViewById(R.id.account_tel1).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setInfoToDB();
                }
            }
        });
        findViewById(R.id.account_tel2).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setInfoToDB();
                }
            }
        });
        findViewById(R.id.account_tel3).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setInfoToDB();
                }
            }
        });
        findViewById(R.id.account_email).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setInfoToDB();
                }
            }
        });



    }


    @Override
    public void onClick(View v){
        Intent intent;
        TextView tv;
        switch (v.getId()){
            case R.id.account_email_img:
                tv = (TextView) findViewById(R.id.account_email);
                try {
                    intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + tv.getText()));
                    startActivity(intent);
                }catch (Exception ex){
                    Toast.makeText(this,"Ошибка в вызове приложения!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.account_tel1_img:
                tv = (TextView) findViewById(R.id.account_tel1);
                try {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+tv.getText()));
                    startActivity(intent);
                }catch (Exception ex){
                    Toast.makeText(this,"Ошибка в вызове приложения!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.account_tel2_img:
                tv = (TextView) findViewById(R.id.account_tel2);
                try {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+tv.getText()));
                    startActivity(intent);
                }catch (Exception ex){
                    Toast.makeText(this,"Ошибка в вызове приложения!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.account_tel3_img:
                tv = (TextView) findViewById(R.id.account_tel3);
                try {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+tv.getText()));
                    startActivity(intent);
                }catch (Exception ex){
                    Toast.makeText(this,"Ошибка в вызове приложения!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void fillTable(){
        Unit enity = data;
        TextView tv1= (TextView)findViewById(R.id.account_abonent);
        TextView tv2= (TextView)findViewById(R.id.account_area);
        TextView tv3= (TextView)findViewById(R.id.account_room);
        TextView tv4= (TextView)findViewById(R.id.account_living_people);
        TextView tv5= (TextView)findViewById(R.id.account_tel1);
        TextView tv6= (TextView)findViewById(R.id.account_tel2);
        TextView tv7= (TextView)findViewById(R.id.account_tel3);
        TextView tv8= (TextView)findViewById(R.id.account_email);
        TextView tv9= (TextView)findViewById(R.id.account_ls);

        HashMap<String,String> dat = dbase.getCurrentInfo(data.id);
        tv1.setText(enity.FIO);
        tv2.setText(enity.area);
        tv3.setText(enity.room);
        tv4.setText(enity.number_of_living);
        tv9.setText(enity.account);
        if (dat.size()>0) {
            try{
                tv5.setText(dat.get(dbase.INFO_TEL+"1"));
            }catch(Exception ex){
                tv5.setText(enity.tel1);
            }
            try{
                tv6.setText(dat.get(dbase.INFO_TEL+"2"));
            }catch(Exception ex){
                tv6.setText(enity.tel2);
            }
            try{
                tv7.setText(dat.get(dbase.INFO_TEL+"3"));
            }catch(Exception ex){
                tv7.setText(enity.tel3);
            }
            try{
                tv8.setText(dat.get(dbase.INFO_EMAIL));
            }catch(Exception ex){
                tv8.setText(enity.email);
            }
        }else {
            tv5.setText(enity.tel1);
            tv6.setText(enity.tel2);
            tv7.setText(enity.tel3);
            tv8.setText(enity.email);
        }

    }



    private void setInfoToDB(){
        HashMap<String,String> dat = new HashMap<String,String>();
        TextView tv = (TextView) findViewById(R.id.account_email);
        dat.put(dbase.INFO_EMAIL,tv.getText().toString());
        //Toast.makeText(this, tv.getText(), Toast.LENGTH_SHORT);
        tv = (TextView) findViewById(R.id.account_tel1);
        dat.put(dbase.INFO_TEL + "2",tv.getText().toString());
        //Toast.makeText(this, tv.getText(), Toast.LENGTH_SHORT);
        tv = (TextView) findViewById(R.id.account_tel2);
        dat.put(dbase.INFO_TEL+"3",tv.getText().toString());
        //Toast.makeText(this,tv.getText(),Toast.LENGTH_SHORT);
        tv = (TextView) findViewById(R.id.account_tel3);
        dat.put(dbase.INFO_TEL + "1",tv.getText().toString());
        //Toast.makeText(this,tv.getText(),Toast.LENGTH_SHORT);
        dbase.setInfo(data.id, dat);
    }
}
