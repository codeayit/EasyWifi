package com.ayit.easywifi;

import android.net.wifi.ScanResult;

import java.util.List;

public interface EasyScanWifiListener {
    void onBefore();
    void onSuccess(List<ScanResult> savedResults,ScanResult linkedResult,List<ScanResult> otherResults);
    void onAfter();
}
