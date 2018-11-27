package com.robot.brain.ai.tts;

/**
 * Created by lny on 2017/10/20.
 */

public interface SpeechSynthesizeEngineInitListener {
    void onSuccess();
    void onError(int code, String msg);
}
