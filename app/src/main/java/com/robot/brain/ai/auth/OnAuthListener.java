package com.robot.brain.ai.auth;

/**
 * Created by lny on 2018/1/8.
 */

public interface OnAuthListener {
    void onAuthSuccess();
    void onAuthFailed(int code, String msg);
}
