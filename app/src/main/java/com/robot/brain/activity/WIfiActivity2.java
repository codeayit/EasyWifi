package com.robot.brain.activity;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.ayit.easywifi.EasyScanWifiListener;
import com.ayit.easywifi.EasyWfifiConnectListener;
import com.ayit.easywifi.EasyWifiManager;
import com.ayit.easywifi.EasyWifiStateListener;
import com.ayit.klog.KLog;
import com.robot.baseapi.base.BaseActivity;
import com.robot.brain.R;
import com.suke.widget.SwitchButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WIfiActivity2 extends BaseActivity implements SwitchButton.OnCheckedChangeListener {

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

    private EasyWifiManager easyWifiManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);
        init();
    }


    @Override
    public void initData() {
        list = new ArrayList<>();
        easyWifiManager = new EasyWifiManager(getContext());
        easyWifiManager.onCreate();

        easyWifiManager.setEasyWifiStateListener(new EasyWifiStateListener() {
            @Override
            public void onDisabling() {
                ivWifiRefresh.setVisibility(View.VISIBLE);
                startAnimation();
            }

            @Override
            public void onDisabled() {
                stopAnimation();
                ivWifiRefresh.setVisibility(View.GONE);
                sbWifi.setOnCheckedChangeListener(null);
                sbWifi.setChecked(false);
                sbWifi.setOnCheckedChangeListener(WIfiActivity2.this);
                list.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onEnableing() {
                ivWifiRefresh.setVisibility(View.VISIBLE);
                startAnimation();
            }

            @Override
            public void onEnabled() {
                sbWifi.setOnCheckedChangeListener(null);
                sbWifi.setChecked(true);
                sbWifi.setOnCheckedChangeListener(WIfiActivity2.this);
                KLog.d("onEnabled : "+easyWifiManager.getWifiUtil().isWifiOpen());
                easyWifiManager.scan();
                stopAnimation();
            }
        });


        easyWifiManager.setEasyWfifiConnectListener(new EasyWfifiConnectListener() {
            @Override
            public void onScanning() {
                KLog.d("onScanning");
                easyWifiManager.scan();
            }

            @Override
            public void onConnecting(String ssid,String bssid) {
                KLog.d("onConnecting : "+ssid+" : "+bssid);
                startAnimation();
            }

            @Override
            public void onAuthenticating(String ssid,String bssid) {
                KLog.d("onAuthenticating : "+ssid+" : "+bssid);
                startAnimation();
            }

            @Override
            public void onObtainingIpaddr(String ssid,String bssid) {
                KLog.d("onObtainingIpaddr : "+ssid+" : "+bssid);

            }

            @Override
            public void onConnected(String ssid,String bssid) {
                KLog.d("onConnected : "+ssid+" : "+bssid);
                stopAnimation();
                easyWifiManager.scan();
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDisconnecting() {
                KLog.d("onDisconnecting");
            }

            @Override
            public void onDisconnected(String ssid,String bssid) {
                KLog.d("onDisconnected : "+ssid +" : "+bssid);
//                easyWifiManager.scan();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAuthentError(String ssid,String bssid) {
                KLog.d("onAuthentError : "+ssid);
                stopAnimation();
            }
        });




        easyWifiManager.setEasyScanWifiListener(new EasyScanWifiListener() {

            @Override
            public void onBefore() {
                startAnimation();
            }

            @Override
            public void onSuccess(List<ScanResult> savedResults, ScanResult linkedResult, List<ScanResult> otherResults) {
                list.clear();
                KLog.d("saved_size : "+savedResults.size());
                if (linkedResult != null){
                    KLog.d("linked : "+linkedResult.SSID);
                }else{
                    KLog.d("linked : null");
                }

                list.addAll(savedResults);
                list.addAll(otherResults);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAfter() {
                stopAnimation();
            }
        });


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
                if (easyWifiManager.getWifiUtil().isNonePassword(scanResult)) {
                    //无密码
                    holder.getView(R.id.iv_wifilock).setVisibility(View.INVISIBLE);
                } else {
                    holder.getView(R.id.iv_wifilock).setVisibility(View.VISIBLE);
                }

                ((ImageView) holder.getView(R.id.iv_wifilevel)).setImageResource(getWifiSSidImageID(scanResult));

                if (easyWifiManager.getWifiUtil().isWifiConnected() && easyWifiManager.getWifiUtil().getBSSID().equals(scanResult.BSSID)) {
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
                easyWifiManager.connect(result,"1234567890");

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        rvWifis.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvWifis.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWifis.setAdapter(adapter);

        sbWifi.setOnCheckedChangeListener(this);

    }


    private void startAnimation(){
        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_wifi_refresh);
        ivWifiRefresh.setAnimation(rotate);
        ivWifiRefresh.startAnimation(rotate);
    }

    private void stopAnimation(){
        if (getContext()!=null){
            ivWifiRefresh.clearAnimation();
        }
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
        KLog.d("wifi_open:"+easyWifiManager.getWifiUtil().isWifiOpen());
        KLog.d("wifi_open2:"+easyWifiManager.getWifiUtil().isWifiOpen());

        sbWifi.setChecked(easyWifiManager.getWifiUtil().isWifiOpen());
        if (easyWifiManager.getWifiUtil().isWifiOpen()){
            easyWifiManager.scan();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       easyWifiManager.onDestroy();
    }


    @OnClick({R.id.iv_wifi_refresh, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_wifi_refresh:
                easyWifiManager.scan();
                break;
            case R.id.btn_confirm:
                break;
        }
    }



    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked){
            easyWifiManager.getWifiUtil().openWifi();
        }else{
            easyWifiManager.getWifiUtil().closeWifi();

        }
    }
}
