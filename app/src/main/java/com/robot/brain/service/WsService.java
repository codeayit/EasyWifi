package com.robot.brain.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.robot.brain.websocket.WsManager;

public class WsService extends Service {


//    http://i.ubolixin.com:9503/
//            :
//    监听地址：   ws://http://i.ubolixin.com:9503/序列号


    public WsService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        WsManager.getInstance().connect(this,"ws://i.ubolixin.com:9503/s");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WsManager.getInstance().disconnect();
    }
}
