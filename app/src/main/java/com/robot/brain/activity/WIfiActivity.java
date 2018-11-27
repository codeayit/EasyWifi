package com.robot.brain.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.ayit.klog.KLog;
import com.robot.baseapi.base.BaseActivity;
import com.robot.baseapi.other.SafeHandler;
import com.robot.baseapi.other.SafeHandlerMsgCallback;
import com.robot.brain.R;
import com.robot.brain.util.WifiUtils;
import com.suke.widget.SwitchButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WIfiActivity extends BaseActivity implements SafeHandlerMsgCallback {

    private static SafeHandler handler;
    @BindView(R.id.iv_wifi_refresh)
    ImageView ivWifiRefresh;
    @BindView(R.id.rv_wifis)
    RecyclerView rvWifis;
    @BindView(R.id.sb_wifi)
    SwitchButton sbWifi;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private List<ScanResult> list;
    private CommonAdapter<ScanResult> adapter;

    private final long TIME_REFRESH = 2 * 1000;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);
        init();
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
            KLog.json(JSON.toJSONString(map));
        } else {
            KLog.d("intent is null");
        }
    }


    @Override
    public void initData() {

        IntentFilter filter = new IntentFilter();

        //链接状态  链接类型  android.net.conn.CONNECTIVITY_CHANGE
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //wifi 打开状态  android.net.wifi.WIFI_STATE_CHANGED
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // wifi 链接状态  android.net.wifi.STATE_CHANGE
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // 链接wifi 密码认证 android.net.wifi.supplicant.STATE_CHANGE
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

//        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    //网络链接状态  网络链接类型  android.net.conn.CONNECTIVITY_CHANGE

                } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    //wifi 打开状态  android.net.wifi.WIFI_STATE_CHANGED
                    //上个wifi状态
                    int previous_wifi_state = intent.getIntExtra("previous_wifi_state", -1);
                    // 当前wifi状态
                    int wifi_state = intent.getIntExtra("wifi_state", -1);
                    switch (wifi_state) {
                        case WifiManager.WIFI_STATE_DISABLING:
                            //关闭中
                            break;
                        case WifiManager.WIFI_STATE_DISABLED:
                            //已关闭
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            //开启中
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            //已开启
                            break;
                    }


                } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    // wifi 链接状态  android.net.wifi.STATE_CHANGE
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null) {
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                            //wifi 连接中状态
                            String extraInfo = networkInfo.getExtraInfo().replaceAll("\"", "");
                            KLog.d("SSID : "+extraInfo);
                            final NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
                            if (NetworkInfo.DetailedState.CONNECTING.equals(detailedState)) {
                                //开始链接
                                if ("<unknown ssid>".equals(extraInfo)) {
                                    //开始查找可用wifi
                                } else {
                                    //链接特定wifi

                                }
                            } else if (NetworkInfo.DetailedState.AUTHENTICATING.equals(detailedState)) {
                                //开始验证 特定wifi

                            } else if (NetworkInfo.DetailedState.OBTAINING_IPADDR.equals(detailedState)) {
                                //正在获取id地址
                            }
                        }else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                            //已经连接
                            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            if (wifiInfo!=null){
                                //已连接的wifi 信息
                            }
                        }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)){
                            //断开WiFi 链接
                        }

                    }

                }else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
                    //密码验证
                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 110);
                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {

                    }

                }
                printIntent(intent);
            }
        }, filter);


        handler = new SafeHandler();
        handler.regist(this);
        list = new ArrayList<>();
        adapter = new CommonAdapter<ScanResult>(getContext(), R.layout.item_rv_wifi, list) {

            /**
             * @param ap
             * @return
             * @desc wifi引导页
             */
            private int getWifiSSidImageID(ScanResult ap) {
                int imageId = 0;
                int rssi = ap.level;
                if (rssi < 0 && rssi >= -50) {
                    imageId = R.drawable.icon_top_wifi_3;
                } else if (rssi < -50 && rssi >= -70) {
                    imageId = R.drawable.icon_top_wifi_2;
                } else if (rssi < -70 && rssi >= -200) {
                    imageId = R.drawable.icon_top_wifi_1;
                } else {
                    imageId = R.drawable.icon_top_wifi_1;
                    //wifi已断小于-200
                }
                return imageId;
            }

            @Override
            protected void convert(ViewHolder holder, ScanResult scanResult, int position) {

                TextView tvWifiName = holder.getView(R.id.tv_wifiname);

                tvWifiName.setText(scanResult.SSID);
                if (WifiUtils.isNonePassword(scanResult.capabilities)) {
                    //无密码
                    holder.getView(R.id.iv_wifilock).setVisibility(View.INVISIBLE);
                } else {
                    holder.getView(R.id.iv_wifilock).setVisibility(View.VISIBLE);
                }

                ((ImageView) holder.getView(R.id.iv_wifilevel)).setImageResource(getWifiSSidImageID(scanResult));

                if (WifiUtils.isWifiConnected() && WifiUtils.getBSSID().equals(scanResult.BSSID)) {
                    tvWifiName.setTextColor(getContext().getResources().getColor(R.color.green));
                } else {
                    tvWifiName.setTextColor(getContext().getResources().getColor(R.color.white));
                }
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ScanResult result = list.get(position);
                KLog.d(":" + result.SSID + ":");
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        rvWifis.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvWifis.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWifis.setAdapter(adapter);

        sbWifi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                handler.removeCallbacksAndMessages();
                KLog.d("isChecked:" + isChecked);
                if (isChecked) {
                    WifiUtils.openWifi();
                    getWifiList();
                } else {
//                    ivWifiRefresh.setVisibility(View.GONE);
                    WifiUtils.closeWifi();
                    list.clear();
                    adapter.notifyDataSetChanged();
                    handler.removeCallbacksAndMessages();

                }
            }
        });
        sbWifi.setChecked(WifiUtils.isWifiOpen());


    }

    private void getWifiList() {
        KLog.d("getWifiList");
        handler.removeCallbacksAndMessages();
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_wifi_refresh);
        ivWifiRefresh.setAnimation(rotate);
        ivWifiRefresh.startAnimation(rotate);
        Observable.just(1)
                .map(new Function<Integer, List<ScanResult>>() {

                    @Override
                    public List<ScanResult> apply(Integer integer) throws Exception {
                        WifiUtils.startScanWifi();
//                        SystemClock.sleep(2 * 1000);
                        List<ScanResult> wifiLists = WifiUtils.getScanWifiList();


                        Collections.sort(wifiLists, new Comparator<ScanResult>() {
                            @Override
                            public int compare(ScanResult scanResult, ScanResult t1) {
                                return t1.level - scanResult.level;
                            }
                        });

                        List<ScanResult> removeAll = new ArrayList<>();
                        for (ScanResult result : wifiLists) {
                            if (TextUtils.isEmpty(result.SSID)) {
                                removeAll.add(result);
                            }
                        }

                        wifiLists.removeAll(removeAll);


                        if (WifiUtils.isWifiConnected())
                            for (ScanResult result : wifiLists) {
                                if (result.BSSID.equals(WifiUtils.getBSSID())) {
                                    wifiLists.remove(result);
                                    wifiLists.set(0, result);

                                    break;
                                }
                            }

                        return wifiLists;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ScanResult>>() {

                    @Override
                    public void accept(List<ScanResult> value) throws Exception {
                        if (getContext() != null) {
                            ivWifiRefresh.clearAnimation();
                            if (WifiUtils.isWifiOpen()) {
//                                handler.sendEmptyMessageDelayed(1, TIME_REFRESH);
//                            ivWifiRefresh.setImageResource(R.drawable.icon_wifi_refresh_normal);
                                list.clear();
                                list.addAll(value);
                            } else {
                                handler.removeCallbacksAndMessages();
                                list.clear();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void initView() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KLog.d("申请权限结果 ： " + grantResults[0]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sbWifi.setChecked(WifiUtils.isWifiOpen());
        if (WifiUtils.isWifiOpen()) {
            getWifiList();
            ivWifiRefresh.setVisibility(View.VISIBLE);
        } else {
//            ivWifiRefresh.setVisibility(View.GONE);
            list.clear();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.unregist();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void handleMessage(Message message) {
        getWifiList();
    }

    @OnClick({R.id.iv_wifi_refresh, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifi_refresh:
                getWifiList();
                break;
            case R.id.btn_confirm:
                break;
        }
    }
}
