/*******************************************************************************
 * Copyright 2014 AISpeech
 ******************************************************************************/
package com.robot.brain.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.robot.baseapi.base.BaseActivity;
import com.robot.brain.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ListView listView = (ListView) findViewById(R.id.activity_list);
        ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> item = new HashMap<String, Object>();

        item = new HashMap<String, Object>();
        item.put("activity_name", "授权");
        item.put("activity_class", AuthActivity.class);
        listItems.add(item);
        
//        item = new HashMap<String, Object>();
//        item.put("activity_name", "云端语音识别");
//        item.put("activity_class", CloudASR.class);
//        listItems.add(item);
//
//        item = new HashMap<String, Object>();
//        item.put("activity_name", "本地语法编译");
//        item.put("activity_class", LocalGrammar.class);
//        listItems.add(item);
//
//        item = new HashMap<String, Object>();
//        item.put("activity_name", "本地合成");
//        item.put("activity_class", LocalTTS.class);
//        listItems.add(item);

//        item = new HashMap<String, Object>();
//        item.put("activity_name", "本地唤醒DNN");
//        item.put("activity_class", WakeUpCloudAsr.class);
//        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put("activity_name", "语音识别");
        item.put("activity_class", AsrActivity.class);
        listItems.add(item);

        item = new HashMap<String, Object>();
        item.put("activity_name", "语音合成");
        item.put("activity_class", TtsActivity.class);
        listItems.add(item);
        
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.list_item,
                new String[] { "activity_name" }, new int[] { R.id.text_item });

        listView.setAdapter(adapter);
        listView.setDividerHeight(2);

        listView.setOnItemClickListener(this);
        
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<?, ?> map = (HashMap<?, ?>) parent.getAdapter().getItem(position);
        Class<?> clazz = (Class<?>) map.get("activity_class");
        Intent it = new Intent(this, clazz);
        this.startActivity(it);
    }
}
