package com.robot.brain.websocket;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.ayit.klog.KLog;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import com.robot.baseapi.net.JsonParser;
import com.robot.baseapi.other.SafeHandler;
import com.robot.baseapi.other.SafeHandlerMsgCallback;
import com.robot.baseapi.util.NetworkUtil;
import com.robot.brain.sem.ActionConsumer;
import com.robot.brain.sem.AppAction;
import com.robot.brain.sem.Conversation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lny on 2018/3/30.
 */

public class WsManager implements SafeHandlerMsgCallback {

    public static final int MSG_RECONNECT = 1;


    private static WsManager mInstance;

    private WebSocket ws;
    /**
     * WebSocket config
     */
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private String url;

    private static SafeHandler handler = new SafeHandler();

    private WsListener mListener;
    private WsStatus mStatus;
    private Context context;


    private WsManager() {
    }

    public static WsManager getInstance() {
        if (mInstance == null) {
            synchronized (WsManager.class) {
                if (mInstance == null) {
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }

    public void connect() {
        connect(context, url);
    }

    public void connect(Context context, @NonNull String url) {
        this.context = context;
        try {
            /**
             * configUrl其实是缓存在本地的连接地址
             * 这个缓存本地连接地址是app启动的时候通过http请求去服务端获取的,
             * 每次app启动的时候会拿当前时间与缓存时间比较,超过6小时就再次去服务端获取新的连接地址更新本地缓存
             */
            this.url = url;
            if (TextUtils.isEmpty(url)) {
                throw new RuntimeException("url is empty");
            }
            ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
//            KLog.d("WebSocket开始连接");
            handler.regist(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStatus(WsStatus status) {
        this.mStatus = status;
    }

    private WsStatus getStatus() {
        return mStatus;
    }

    public void disconnect() {
        if (ws != null) {
            ws.disconnect();
        }

        if (handler!=null){
            handler.unregist();
            handler = null;
        }

    }

    /**
     * handler 消息
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RECONNECT:
                connect();
                break;

        }
    }

    class WsListener extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            KLog.d("onTextMessage:" + text);
            KLog.json(text);
            JsonParser parser = new JsonParser(text);
//            JSONArray appActions = parser.parseAppActions();
//            boolean isKeep = parser.parseKeepPlay().equals("N") ? false : true;
//            if (appActions != null && !appActions.isEmpty()) {
//                List<AppAction> temp = new ArrayList<AppAction>();
//                for (int i = 0; i < appActions.size(); i++) {
//                    temp.add(new AppAction(appActions.getJSONObject(i)));
//                }
//                Conversation conversation = new Conversation(temp, isKeep);
//                ActionConsumer.getInstance().addConversation(conversation);
//                ActionConsumer.getInstance().excute();
//
//            }
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            setStatus(WsStatus.CONNECT_SUCCESS);
            KLog.d("onConnected");
            handler.removeMessages(MSG_RECONNECT);

        }

        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
            super.onConnectError(websocket, exception);
            setStatus(WsStatus.CONNECT_FAIL);
//            KLog.d("onConnectError:" + exception.getMessage());
            if (NetworkUtil.isWifiConnected(context)) {
                handler.sendEmptyMessage(MSG_RECONNECT);
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            KLog.d("onDisconnected");
            if (handler != null){
                //如果socket 断了  1秒后重连
                KLog.d("尝试重连");
                handler.sendEmptyMessageDelayed(MSG_RECONNECT, 1 * 1000);
            }
        }
    }

    enum WsStatus {
        CONNECT_SUCCESS,//连接成功
        CONNECT_FAIL,//连接失败
        CONNECTING;//正在连接
    }


}
