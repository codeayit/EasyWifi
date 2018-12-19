package com.ayit.easywifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyWifiManager extends BroadcastReceiver {

    private final String TAG = "EasyWifiManager";

    private Context context;

    private EasyWifiStateListener easyWifiStateListener;

    private String currentSsid;


    public void setEasyWifiStateListener(EasyWifiStateListener easyWifiStateListener) {
        this.easyWifiStateListener = easyWifiStateListener;
    }

    private EasyWfifiConnectListener easyWfifiConnectListener;

    public void setEasyWfifiConnectListener(EasyWfifiConnectListener easyWfifiConnectListener) {
        this.easyWfifiConnectListener = easyWfifiConnectListener;
    }

    private EasyScanWifiListener easyScanWifiListener;

    public void setEasyScanWifiListener(EasyScanWifiListener easyScanWifiListener) {
        this.easyScanWifiListener = easyScanWifiListener;
    }

    private EasyUtils easyUtils;

    public EasyWifiManager(@NonNull Context context) {
        this.context = context;
        easyUtils = new EasyUtils(context);
    }

    private static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };


    public void onCreate() {

        IntentFilter filter = new IntentFilter();

        //链接状态  链接类型  android.net.conn.CONNECTIVITY_CHANGE
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //wifi 打开状态  android.net.wifi.WIFI_STATE_CHANGED
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // wifi 链接状态  android.net.wifi.STATE_CHANGE
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // 链接wifi 密码认证 android.net.wifi.supplicant.STATE_CHANGE
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(this, filter);
    }

    public void onDestroy() {
        context.unregisterReceiver(this);
        context = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //网络链接状态  网络链接类型  android.net.conn.CONNECTIVITY_CHANGE

        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            //wifi 打开状态  android.net.wifi.WIFI_STATE_CHANGED
            //上个wifi状态
            int previous_wifi_state = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, -1);
            // 当前wifi状态
            int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            switch (wifi_state) {
                case WifiManager.WIFI_STATE_DISABLING:
                    //关闭中
                    if (easyWifiStateListener != null) {
                        easyWifiStateListener.onDisabling();
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    //已关闭
                    if (easyWifiStateListener != null) {
                        easyWifiStateListener.onDisabled();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    //开启中

                    if (easyWifiStateListener != null) {
                        easyWifiStateListener.onEnableing();
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //已开启
                    if (easyWifiStateListener != null) {
                        easyWifiStateListener.onEnabled();
                    }
//                    handler.removeCallbacksAndMessages(null);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    },5000);

                    break;
            }


        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            // wifi 链接状态  android.net.wifi.STATE_CHANGE
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null) {
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                    //wifi 连接中状态
                    String extraInfo = networkInfo.getExtraInfo().replaceAll("\"", "");
                    final NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                    log("SSID : " + extraInfo + " : " + detailedState);
                    if (NetworkInfo.DetailedState.CONNECTING.equals(detailedState)) {
                        //开始链接
                        if ("<unknown ssid>".equals(extraInfo)) {
                            //开始查找可用wifi
                            currentSsid = null;
                            if (easyWfifiConnectListener != null) {
                                easyWfifiConnectListener.onScanning();
                            }
                        } else {
                            //链接特定wifi
                            currentSsid = extraInfo;
                            if (easyWfifiConnectListener != null) {
                                easyWfifiConnectListener.onConnecting(extraInfo);
                            }

                        }
                    } else if (NetworkInfo.DetailedState.AUTHENTICATING.equals(detailedState)) {
                        //开始验证 特定wifi
                        currentSsid = extraInfo;
                        if (easyWfifiConnectListener != null) {
                            easyWfifiConnectListener.onAuthenticating(extraInfo);
                        }
                    } else if (NetworkInfo.DetailedState.OBTAINING_IPADDR.equals(detailedState)) {
                        //正在获取id地址
                        currentSsid = extraInfo;
                        if (easyWfifiConnectListener != null) {
                            easyWfifiConnectListener.onObtainingIpaddr(extraInfo);
                        }
                    }
                } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    //已经连接
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    String extraInfo = networkInfo.getExtraInfo().replaceAll("\"", "");
                    log("SSID : " + extraInfo);
                    currentSsid = extraInfo;
                    if (wifiInfo != null) {
                        //已连接的wifi 信息
                        if (easyWfifiConnectListener != null) {
                            easyWfifiConnectListener.onConnected(extraInfo);
                        }
                    }
                } else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    //断开WiFi 链接

                    if (easyWfifiConnectListener != null) {
                        easyWfifiConnectListener.onDisconnected(currentSsid);
                    }
                }

            }

        } else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            //密码验证
            int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 110);
            if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                if (easyWfifiConnectListener != null) {
                    easyWfifiConnectListener.onAuthentError(currentSsid);
                }
            }
        }
//        printIntent(intent);
    }

    private boolean stopScan;

    private void stopScan() {
        stopScan = true;
    }

    public void scan() {
        log("scan:" + easyUtils.isWifiOpen());
        if (easyUtils.isWifiOpen()) {
            if (easyScanWifiListener != null) {
                easyScanWifiListener.onBefore();
            }
            log("开始扫描WiFi");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(500);
                    easyUtils.startScanWifi();
                    SystemClock.sleep(200);
                    final List<ScanResult> scanWifiList = easyUtils.getScanWifiList();
                    List<ScanResult> removeAll = new ArrayList<>();
                    for (ScanResult result : scanWifiList) {
                        if (TextUtils.isEmpty(result.SSID)) {
                            removeAll.add(result);
                        }
                    }
                    scanWifiList.removeAll(removeAll);
                    if (context == null) {
                        return;
                    }
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    // 获取已保存wifi配置链表
                    List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();


                    final List<ScanResult> savedResults = new ArrayList<>();

                    if (configs != null)
                        for (WifiConfiguration configuration : configs) {
                            for (ScanResult result : scanWifiList) {
//                            log("config_ssid:"+configuration.SSID + " : "+result.SSID + " -> "+(configuration.SSID.replaceAll("\"","").equals(result.SSID)));
                                if (configuration.SSID.replaceAll("\"", "").equals(result.SSID)) {
                                    savedResults.add(result);
                                    break;
                                }
                            }
                        }

//                    log("saved_size:" + configs.size() + " : " + savedResults.size());


                    scanWifiList.removeAll(savedResults);


                    Collections.sort(savedResults, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult scanResult, ScanResult t1) {
                            return t1.level - scanResult.level;
                        }
                    });

                    ScanResult linkedResult = null;


//                    log("connected_ssid : " + easyUtils.getSSID()+" : "+easyUtils.isWifiConnected());

                    if (easyUtils.isWifiConnected()) {
                        for (ScanResult result : savedResults) {
//                            log("connected_ssid : " + easyUtils.getSSID() + " : "+result.SSID +"->"+easyUtils.getSSID().replaceAll("\"","").equals(result.SSID));
                            if (easyUtils.getSSID().replaceAll("\"", "").equals(result.SSID)) {
                                linkedResult = result;
                                break;
                            }
                        }
                        if (linkedResult != null) {
                            savedResults.remove(linkedResult);
                            savedResults.add(0, linkedResult);
                        }
                    }
                    Collections.sort(scanWifiList, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult scanResult, ScanResult t1) {
                            return t1.level - scanResult.level;
                        }
                    });


//                    for (ScanResult result : scanWifiList){
//                        log("scan :"+result.SSID);
//                    }
//
//                    for (ScanResult result : savedResults){
//                        log("saved :"+result.SSID);
//                    }
//
//                    log(linkedResult==null? " linked: null ":"linked:"+linkedResult.SSID);


                    final ScanResult finalLinkedResult = linkedResult;
//                    log("扫描结束");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            log("扫描结束2");
                            if (easyScanWifiListener != null) {
                                final EasyScanWifiListener finalEasyScanWifiListener = easyScanWifiListener;
                                try {
                                    easyScanWifiListener.onSuccess(savedResults, finalLinkedResult, scanWifiList);
                                } finally {
                                    finalEasyScanWifiListener.onAfter();
                                }
                            }
                        }
                    });

                }
            }).start();
        }
    }

    public EasyUtils getWifiUtil() {
        return easyUtils;
    }

    private String clearSSID(String ssid) {
        if (ssid != null) {
            return ssid.replaceAll("\"", "");
        }
        return ssid;
    }


    /**
     * 是否已保存
     *
     * @param ssid
     * @return
     */
    public WifiConfiguration isConfiged(String ssid) {
        return easyUtils.isExist(ssid);
    }

    private void connect(final ScanResult result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ssid = result.SSID;
                if (isConfiged(ssid) != null) {
                    easyUtils.connectWifi(result.SSID, null, easyUtils.getWifiCliperType(result));
                } else {
                    if (easyUtils.isNonePassword(result)) {
                        //无密码
                        easyUtils.connectWifi(result.SSID, null, easyUtils.getWifiCliperType(result));
                    } else {
                        //需要密码
                    }
                }
            }
        }).start();
    }

    public void removeNetwork(String ssid){
        WifiConfiguration configed = isConfiged(ssid);
        if (configed!=null){
            easyUtils.wifiManager.removeNetwork(configed.networkId);
        }
    }

    public void connect(final ScanResult result, final String pwd) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        String ssid = result.SSID;
        if (isConfiged(ssid) != null) {
            log("已保存，直接连接");
            boolean connect = easyUtils.connectWifi(result.SSID, null, easyUtils.getWifiCliperType(result));
            log("connect : " + connect);
        } else {
            if (easyUtils.isNonePassword(result)) {
                log("无密码，直接连接");
                //无密码
                easyUtils.connectWifi(result.SSID, null, easyUtils.getWifiCliperType(result));
            } else {
                log("密码，直接连接");
                //需要密码
                easyUtils.connectWifi(result.SSID, pwd, easyUtils.getWifiCliperType(result));
            }
        }
//            }
//        }).start();
    }

    public boolean isNonePassword(ScanResult result) {
        return easyUtils.isNonePassword(result);
    }


    private void printIntent(Intent intent) {
        if (intent != null || intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            Map<String, Object> map = new HashMap<>();

            String action = intent.getAction();
            map.put("action", action);

            for (String key : extras.keySet()) {
                map.put(key, extras.get(key));
            }
            log(map.toString());
        } else {
            log("intent is null");
        }
    }

    public void log(String log) {
        Log.d(TAG, log);
    }
}
