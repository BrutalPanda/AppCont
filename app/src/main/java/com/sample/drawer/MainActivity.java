package com.sample.drawer;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.sample.drawer.MyShitMasterpice.BackgroundService;
import com.sample.drawer.MyShitMasterpice.DB;
import com.sample.drawer.MyShitMasterpice.GPSTracker;
import com.sample.drawer.MyShitMasterpice.OpenFileDialog;
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
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    private Drawer.Result drawerResult = null;
    private AccountHeader.Result headerResult = null;
    private static long back_pressed;
    ArrayList<Unit> Units = new ArrayList<Unit>();

    final String LOG_TAG = "MainActivity::::";
    final String[] possible_path = {"/mnt/sdcard0","/mnt/sdcard1","/mnt/sdcard2","/storage/sdcard0","/storage/sdcard1"};
    ProgressBar pbCount;
    String FILENAME = "";
    ProgressDialog pd;
    int cnt;
    Handler h;
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






        h = new Handler();

        pd = new ProgressDialog(this);
        pd.setTitle("Заполнение базы данных");
        pd.setMessage("Пожалуйста дождитесь окончания операции!");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);




        final DB dbase = new DB(this);

        boolean complete = true;



        if (dbase.isEmpty()) {
            Toast.makeText(getApplicationContext(), "База данных пуста! Выберите файл для загрузки.", Toast.LENGTH_LONG).show();
            OpenFileDialog fileDialog = new OpenFileDialog(this);
            fileDialog.setOpenDialogListener(new OpenFileDialog.OpenDialogListener(){
                @Override
                public void OnSelectedFile(String fileName){
                    FILENAME = fileName;
                    final DB dbase = new DB(getApplicationContext());
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
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });
            fileDialog.show();

        }



       // writeFile(source);
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
