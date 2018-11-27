package com.robot.brain.ai.tts;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lny on 2017/10/20.
 */

public interface SpeechSynthesizeEngine {
    Map<String,Object> configs = new HashMap<>();
    void init(Context context, SpeechSynthesizeEngineInitListener speechSynthesizeEngineInitListener);
    void startSpeak(boolean isRestart, String text);
    void stopSpeak();
    void release();
    void setSpeechSynthesizeEngineListener(SpeechSynthesizeEngineListener speechSynthesizeEngineListener);
    String getCurrentSpeakText();

    boolean isInited();

}
