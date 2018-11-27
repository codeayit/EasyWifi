package com.robot.brain.ai.recognization;

/**
 * Created by lny on 2017/9/30.
 */

public interface RecognizationEngineInitListener {
    void onSuccess();
    void onError(int code, String msg);
}
