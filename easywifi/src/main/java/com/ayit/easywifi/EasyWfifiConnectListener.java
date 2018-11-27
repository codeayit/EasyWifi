package com.ayit.easywifi;

public interface EasyWfifiConnectListener {
    /**
     * 搜索已保存wifi
     */
    void onScanning();

    /**
     * 搜索已保存可连接wifi，开始链接
     * @param ssid
     */
    void onConnecting(String ssid);

    /**
     * 密码验证
     * @param ssid
     */
    void onAuthenticating(String ssid);

    /**
     * 获取id地址
     * @param ssid
     */
    void onObtainingIpaddr(String ssid);

    /**
     * 链接成功
     * @param ssid
     */
    void onConnected(String ssid);

    /**
     * 断开链接中，不一定回调
     */
    void onDisconnecting();

    /**
     * 断开链接
     */
    void onDisconnected(String ssid);

    /**
     * 密码错误
     * @param ssid
     */
    void onAuthentError(String ssid);
}
