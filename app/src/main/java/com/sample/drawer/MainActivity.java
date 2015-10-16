package com.sample.drawer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sample.drawer.MyShitMasterpice.BackgroundService;
import com.sample.drawer.MyShitMasterpice.GPSTracker;
import com.sample.drawer.utils.Unit;
import com.sample.drawer.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    private Drawer.Result drawerResult = null;
    private AccountHeader.Result headerResult = null;
    private static long back_pressed;
    final String FILENAME = "file";
    final String LOG_TAG = "MainActivity::::";
    final String[] possible_path = {"/mnt/sdcard0","/mnt/sdcard1","/mnt/sdcard2","/storage/sdcard0","/storage/sdcard1"};
    ArrayList<Unit> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Для Activity с боковым меню ставьте эту тему,
        // для Activity без бокового меню ставьте тему AppThemeNonDrawer (она прописана по умолчанию в манифесте кстати)
        // иначе будет "сползать" ActionBar
        // Темы находятся в styles.xml
        setTheme(R.style.AppThemeDrawer);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String source = readSourceFile();
        writeFile(source);
        // init Drawer & Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
       // clearOutFile();
        Toast.makeText(this,"Инициализация...",Toast.LENGTH_SHORT).show();
        // Покажем drawer автоматически при запуске
        headerResult = Utils.getAccountHeader(MainActivity.this, savedInstanceState);
        IProfile curProfile = headerResult.getProfiles().get(0);
        String json_data = readFile();
        try {
            JSONArray jarray = new JSONArray(json_data);
            JSONObject c = jarray.getJSONObject(0);
            curProfile.setName("Контролер: "+c.getString("FIO_cont"));
            curProfile.setEmail("Диспетчер: "+c.getString("FIO_disp"));
            curProfile.setSelectable(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        headerResult.clear();
        headerResult.setActiveProfile(curProfile);

        drawerResult = Utils.createCommonDrawer(MainActivity.this, toolbar, headerResult);
        drawerResult.setSelectionByIdentifier(1, false); // Set proper selection


       // Log.d("JSON",obj.toString());
        startService(new Intent(this, BackgroundService.class));
        drawerResult.openDrawer();


    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            // Закрываем меню, если оно показано и при этом нажата системная кнопка "Назад"
            drawerResult.closeDrawer();
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
            }

            back_pressed = System.currentTimeMillis();

        }

    }





    //public Service backEndProcess(){

    //}

    @Override
    public void onPause(){
        Log.d("State", "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy(){
        Log.d("State", "onDestroy");
        stopService(new Intent(this, BackgroundService.class));
        super.onDestroy();

    }



    void writeFile(String str) {

        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            /*bw.write("[{\"FIO_cont\":\"\\u0421\\u043c\\u043e\\u0442\\u0440\\u0438\\u0442\\u0435 \\u043b\\u044e\\u0434\\u0438! \\u041e\\u043d \\u0431\\u044d\\u0442\\u043c\\u0435\\u043d!\",\"FIO_disp\":\"\\u0418\\u0434\\u0438\\u0442\\u0435 \\u043d\\u0430\\u0444\\u0438\\u0433, \\u044f \\u0431\\u044d\\u0442\\u043c\\u0435\\u043d\"},{\"FIO\":\"\\u0424 \\u0418 \\u041e1\",\"account\":\"LS123\",\"address\":\"\\u041b\\u0435\\u043d\\u0438\\u043d\\u0430 12 14\",\"area\":\"45\",\"room\":\"2\",\"number_of_living\":\"3\",\"tel1\":\"111111\",\"tel2\":\"122222\",\"tel3\":\"133333\",\"email\":\"name1@domain.zone\",\"devices\":[{\"name\":\"\\u0421\\u0447\\u0435\\u0442\\u0447\\u0438\\u043a \\u042d\\u041d1\",\"service\":\"\\u042d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"place\":\"\\u043a\\u043e\\u0440\\u0440\\u0438\\u0434\\u043e\\u0440\",\"type\":\"\\u0442\\u0438\\u043f1\",\"factory_num\":\"123\\u0421\\u0410333\",\"accuracy\":\"5\",\"next_check\":\"2016\",\"prev_reading\":\"123\",\"type_reading\":\"\\u0442\\u0438\\u043f_1\",\"date_reading\":\"28.06.15\"},{\"name\":\"\\u0421\\u0447\\u0435\\u0442\\u0447\\u0438\\u043a \\u0413\\u0412\\u0421\\u041d\",\"service\":\"\\u0413\\u0412\\u0421\\u041d\",\"place\":\"\\u0412\\u0430\\u043d\\u043d\\u0430\\u044f\",\"type\":\"\\u0442\\u0438\\u043f2\",\"factory_num\":\"123\\u0421\\u0410\\u0413\\u0412333\",\"accuracy\":\"7\",\"next_check\":\"2016\",\"prev_reading\":\"1213\",\"type_reading\":\"\\u0442\\u0438\\u043f_2\",\"date_reading\":\"27.06.15\"}],\"saldo\":{\"info\":{\"count_rows\":2,\"count_cols\":5},\"header\":[\"\\u0412\\u0438\\u0434 \\u0434\\u043e\\u0433\\u043e\\u0432\\u043e\\u0440\\u0430\",\"\\u0414\\u043e\\u043b\\u0433 \\u043d\\u0430 01.02.2014\",\"\\u041d\\u0430\\u0447\\u0438\\u0441\\u043b\\u0435\\u043d\\u043e \\u0437\\u0430 \\u042f\\u043d\\u0432\\u0430\\u0440\\u044c 2014\",\"\\u041f\\u043e\\u0441\\u043b\\u0435\\u0434\\u043d\\u044f\\u044f \\u043e\\u043f\\u043b\\u0430\\u0442\\u0430\",\"\\u0414\\u0430\\u0442\\u0430 \\u043e\\u043f\\u043b\\u0430\\u0442\\u044b\"],\"0\":[\"\\u0422\\u0435\\u043f\\u043b\\u043e\\u044d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"1 200,00\",\"1 000,00\",\"1 000,00\",\"01.01.2014\"],\"1\":[\"\\u042d\\u043b\\u0435\\u043a\\u0442\\u0440\\u043e\\u044d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"-100,00\",\"120,00\",\"200,00\",\"22.11.2013\"]}},{\"FIO\":\"\\u0424 \\u0418 \\u041e2\",\"account\":\"LS321\",\"address\":\"\\u041c\\u0438\\u0440\\u0430 12 14\",\"area\":\"65\",\"room\":\"3\",\"number_of_living\":\"4\",\"tel1\":\"211111\",\"tel2\":\"222222\",\"tel3\":\"233333\",\"email\":\"name2@domain.zone\",\"devices\":[{\"name\":\"\\u0421\\u0447\\u0435\\u0442\\u0447\\u0438\\u043a \\u042d\\u041d2\",\"service\":\"\\u042d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"place\":\"\\u043a\\u0443\\u0445\\u043d\\u044f\",\"type\":\"\\u0442\\u0438\\u043f1\",\"factory_num\":\"321\\u0421\\u0410333\",\"accuracy\":\"6\",\"next_check\":\"2016\",\"prev_reading\":\"321\",\"type_reading\":\"\\u0442\\u0438\\u043f_1\",\"date_reading\":\"26.06.15\"},{\"name\":\"\\u0421\\u0447\\u0435\\u0442\\u0447\\u0438\\u043a \\u0413\\u0412\\u0421\\u041d2\",\"service\":\"\\u0413\\u0412\\u0421\\u041d\",\"place\":\"\\u0412\\u0430\\u043d\\u043d\\u0430\\u044f \\u043a\\u043e\\u043c\\u043d\\u0430\\u0442\\u0430\",\"type\":\"\\u0442\\u0438\\u043f2\",\"factory_num\":\"321\\u0421\\u0410\\u0413\\u0412333\",\"accuracy\":\"8\",\"next_check\":\"2016\",\"prev_reading\":\"3121\",\"type_reading\":\"\\u0442\\u0438\\u043f_2\",\"date_reading\":\"25.06.15\"}],\"saldo\":{\"info\":{\"count_rows\":2,\"count_cols\":5},\"header\":[\"\\u0412\\u0438\\u0434 \\u0434\\u043e\\u0433\\u043e\\u0432\\u043e\\u0440\\u0430\",\"\\u0414\\u043e\\u043b\\u0433 \\u043d\\u0430 01.02.2014\",\"\\u041d\\u0430\\u0447\\u0438\\u0441\\u043b\\u0435\\u043d\\u043e \\u0437\\u0430 \\u042f\\u043d\\u0432\\u0430\\u0440\\u044c 2014\",\"\\u041f\\u043e\\u0441\\u043b\\u0435\\u0434\\u043d\\u044f\\u044f \\u043e\\u043f\\u043b\\u0430\\u0442\\u0430\",\"\\u0414\\u0430\\u0442\\u0430 \\u043e\\u043f\\u043b\\u0430\\u0442\\u044b\"],\"0\":[\"\\u0422\\u0435\\u043f\\u043b\\u043e\\u044d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"2 200,00\",\"2 000,00\",\"2 000,00\",\"02.02.2014\"],\"1\":[\"\\u042d\\u043b\\u0435\\u043a\\u0442\\u0440\\u043e\\u044d\\u043d\\u0435\\u0440\\u0433\\u0438\\u044f\",\"-200,00\",\"220,00\",\"300,00\",\"33.22.2013\"]}}]");*/
            bw.write(str);
            // закрываем поток Toast.makeText(getBaseContext(), "Файл не найден!", Toast.LENGTH_SHORT).show();
            bw.close();
            Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Log.d(LOG_TAG, "Readed: "+str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    String readSourceFile() {
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
        for (int i=0; i<possible_path.length;i++) {
            try {
                File source = new File(possible_path[i], "file");
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
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
        return ret;
    }

    void clearOutFile() {

        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput("out_file", MODE_PRIVATE)));
            // пишем данные
            bw.write("");
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл очищен");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
