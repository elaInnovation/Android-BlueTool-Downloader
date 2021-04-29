package com.kaplacat.bluetooldownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kaplacat.bluetooldownloader.Models.TagBase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ImageView btnStartScanner, btnStopScanner;
    ImageView btnConnectTag, btnDisconnectTag;
    ListView lstTagScan;
    TextView txtLog;

    ArrayList<String> tagList = new ArrayList<>();
    Handler handler;
    ArrayAdapter<String> arrayAdapter;
    int t = 0;
    String tagSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapUi();
        mapEvent();

        startThread();
    }

    /**
     * Map Ui elements
     */
    private void mapUi()
    {
        this.btnStartScanner = findViewById(R.id.btn_startScanner);
        this.btnStopScanner = findViewById(R.id.btn_stopScanner);
        this.lstTagScan = findViewById(R.id.lst_bleTags);
        this.btnConnectTag = findViewById(R.id.btn_connectTag);
        this.btnDisconnectTag = findViewById(R.id.btn_disconnectTag);
        this.txtLog = findViewById(R.id.txt_log);
        this.txtLog.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * Inititalise array list and start thread bLE
     */
    private void startThread()
    {
        this.arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , this.tagList);
        this.lstTagScan.setAdapter(arrayAdapter);
        runThread();
    }

    /**
     * BLE scan each seconds and add it to facotry
     */
    private void runThread()
    {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t++;
                for(HashMap.Entry<String, TagBase> Tag : ScanFactory.getInstance().getTagNameList().entrySet())
                {
                    if(!tagList.contains(Tag.getKey()))
                    {
                        arrayAdapter.add(Tag.getKey());
                    }
                }
                if(t<100) {
                    handler.postDelayed(this, 1000);    // Stop at 100sec
                }
            }
        }, 1000);
    }

    /**
     * Map buttons events (commands)
     */
    private void mapEvent()
    {
        this.btnStartScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ScannerEla.getInstance().initScanner();
                ScannerEla.getInstance().startScanner();
                ScanFactory.getInstance().clearList();
            }
        });

        this.btnStopScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScannerEla.getInstance().stopScanner();
            }
        });
        this.lstTagScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                tagSelected = (String) adapterView.getItemAtPosition(i);
                txtLog.setText("Tag " + tagSelected + " is selected");
            }
        });
        this.btnConnectTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tagSelected == null) {return;}
                if(tagSelected.equals("")) {return;}

                ConnecterEla.getInstance().setTagToConnect(ScanFactory.getInstance().getTagNameList().get(tagSelected), txtLog);
                ConnecterEla.getInstance().connectToTag(getApplicationContext());
            }
        });
        this.btnDisconnectTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnecterEla.getInstance().disconnectTag();
            }
        });


    }
}