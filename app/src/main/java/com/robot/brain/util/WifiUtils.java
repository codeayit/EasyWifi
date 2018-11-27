package com.robot.brain.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;


import com.robot.brain.BrainApplication;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by gujunkang on 16/9/8.
 */
public class WifiUtils {
    private static Context context = BrainApplication.getInstance();
    public static WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);


    public static enum WifiCliperType {
        WIFICLIPER_WEP, WIFICLIP_WPA, WIFICLIPER_NOPASS, WIFICLIPER_INVALID
    }


    public static List<ScanResult> getScanWifiList() {
        return wifiManager.getScanResults();
    }

    /**
     * @return
     * @desc 判断机器人的wifi是否已连接
     */
    public static boolean isWifiConnected() {
        return getNetworkState(ConnectivityManager.TYPE_WIFI) == NetworkInfo.State.CONNECTED;

    }


    /**
     * @param networkType
     * @return
     * @desc 根据网络类型获取网络的连接状态
     */
    public static NetworkInfo.State getNetworkState(int networkType) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        NetworkInfo networkInfo = cm.getNetworkInfo(networkType);
        return networkInfo == null ? null : networkInfo.getState();
    }

    /**
     * @return
     * @desc 根据当前的Rssi获取当前的网络信号强弱
     */
    public static int getWifiLevel() {
        int wifiState = 0;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (isNetworkConnected() && null != wifiInfo) {
            // wifi 信号等级，1~MyConstants.HIGH_QUALITY_WIFI_LEVEL
            wifiState = wifiManager.calculateSignalLevel(wifiInfo.getRssi(), 3) + 1;
        }else{
            wifiState = -1;
        }
        return wifiState;
    }

    public static boolean isWifiEnable() {
        return wifiManager.isWifiEnabled();
    }

    public static boolean isWifiOpen(){
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
//        if (wifiManager.isWifiEnabled() && ipAddress != 0) {
//            return true;
//        } else {
//            return false;
//        }

        return wifiManager.isWifiEnabled();

//        ConnectivityManager connectivityManager = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        if (activeNetInfo != null
//                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            return true;
//        }
//        return false;

    }

    public static boolean isWifiApEnabled() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);

        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean openWifi() {
        if (!isWifiEnable()) {
            boolean enabled = wifiManager.setWifiEnabled(true);
            return enabled;
        }
        return false;
    }

    public static void closeWifi(){
        if(isWifiEnable()){
            wifiManager.setWifiEnabled(false);
        }
    }

    public static boolean connentWifi(String ssid, String pwd, WifiCliperType type) {
        if (!isWifiEnable()) {
            return false;
        }
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.currentThread();
                Thread.sleep(500);
            } catch (InterruptedException e) {

            }
        }

        WifiConfiguration wifiConfig = CreateWifiInfo(ssid, pwd, type);
        if (wifiConfig == null) {
            return false;
        }
        int netId;
        WifiConfiguration tempConig = isExist(ssid);
        if (tempConig != null) {
            netId = wifiManager.updateNetwork(tempConig);
//            wifiManager.removeNetwork(tempConig.networkId);
        }else{
            netId = wifiManager.addNetwork(wifiConfig);
        }
//        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
//        WifiInfo  wifiInfo = getConnectionInfo();
//        if(null != wifiInfo){
//            disconnectWifi(wifiInfo.getNetworkId());
//        }
        boolean bRet = wifiManager.enableNetwork(netId, true);
//        wifiManager.reconnect();
        return bRet;
    }

    /**
     * 获取当前正在连接的WIFI信息
     * @return 当前正在连接的WIFI信息
     */
    public static WifiInfo getConnectionInfo() {
        try {
            return wifiManager.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCliperType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = isExist(SSID);

        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        } else {
        }

        if (type == WifiCliperType.WIFICLIPER_NOPASS) // WIFICIPHER_NOPASS
        {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (type == WifiCliperType.WIFICLIPER_WEP) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCliperType.WIFICLIP_WPA) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public static WifiConfiguration isExist(String SSID) {
        if(null != SSID) {
            List<WifiConfiguration> mList = wifiManager.getConfiguredNetworks();
            if(null != mList && !mList.isEmpty()) {
                for (WifiConfiguration configuration : mList) {
                    if (null != configuration.SSID && configuration.SSID.equals("\"" + SSID + "\""))
                        return configuration;
                }
            }
        }
        return null;
    }

    public static String getBSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    public static String getSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static String getLocalIpAddress() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return initToIp(wifiInfo.getIpAddress());
    }

    public static String getServeIpAddress() {
        DhcpInfo info = wifiManager.getDhcpInfo();
        return initToIp(info.gateway);
    }

    public static boolean bHasSavedWifiConfiguration(){
        boolean bHadSavedConfiguration = false;
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if(null != configurations && !configurations.isEmpty()){
            bHadSavedConfiguration = true;
        }
        return bHadSavedConfiguration;
    }

    /**
     * 断开WIFI
     * @param netId netId
     * @return 是否断开
     */
    public static boolean disconnectWifi(int netId) {
        boolean isDisable = wifiManager.disableNetwork(netId);
        boolean isDisconnect = wifiManager.disconnect();
        return isDisable && isDisconnect;
    }

    /**
     * @return {@code true or false}
     * @desc check netWork every ten seconds
     */
    public static boolean hasNetwork() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null !=  cm && cm.getActiveNetworkInfo() != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                return info.isConnected();
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null !=  cm && cm.getActiveNetworkInfo() != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {
                return info.isConnected();
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    public static void startScanWifi() {
        wifiManager.startScan();
    }

    private static String initToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }


    /**
     * 是否开放
     * @param capabilities
     * @return
     */
    public static boolean isNonePassword(String capabilities){
        String desc = "";
        String descOri = capabilities;
        if(descOri.toUpperCase().contains("WPA-PSK")) {
            desc = "WPA";
        }if(descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA2";
        }if (descOri.toUpperCase().contains("WPA-PSK") && descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA/WPA2";
        }
        return TextUtils.isEmpty(desc);

    }
}
