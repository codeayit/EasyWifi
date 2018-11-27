package com.robot.brain.ai.recognization;

import android.content.Context;

/**
 * Created by lny on 2017/9/29.
 */

public interface RecognizationEngine {

    void ini(Context context, RecognizationEngineInitListener recognizationEngineInitListener);
    void setRecognizationEngineListener(RecognizationEngineListener recognizationEngineListener);
    void startWakeup();
    void startAsr();
    void cancle();
    void stopAsr();
    void stopWakeup();
    void stopBoth();
    void release();
    boolean isInited();
}
