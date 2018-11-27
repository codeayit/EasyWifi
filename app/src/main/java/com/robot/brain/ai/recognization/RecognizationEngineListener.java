package com.robot.brain.ai.recognization;

/**
 * Created by lny on 2017/9/30.
 */

public interface RecognizationEngineListener {

    /**
     * 唤醒
     */
    void onWakeup();

    /**
     * 语音识别引擎准备好了
     */
    void onEngineReadyForspeak();

    /**
     * 开始录音
     */
    void onSpeakBegin();

    /**
     * 当前讲话的音量
     * @param parcent   百分比
     */
    void onCurrentSpeakVolume(float parcent);

    /**
     * 讲话结束
     */
    void onSpeakEnd();

    /**
     * 临时识别结果
     * @param tempResult
     */
    void onRecognizationTemp(int code, String tempResult);


    /**
     *
     * @param code  0 云端  1 本地
     * @param result
     */
    void onRecognizationSuccess(int code, String result);

    /**
     * 识别失败
     * @param code
     * @param error
     */
    void onRecognizationError(int code, String error);

    /**
     * 识别结束
     */
    void onRecognizationFinish();

    /**
     * 引擎推出
     */
    void onEngineExit();
}
