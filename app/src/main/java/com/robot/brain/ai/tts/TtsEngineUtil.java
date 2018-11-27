package com.robot.brain.ai.tts;

import android.content.Context;

import com.robot.brain.ai.AiConfiguration;


/**
 * Created by lny on 2017/10/20.
 */

public class TtsEngineUtil {

    private static SpeechSynthesizeEngine tts;


    private TtsEngineUtil() {
    }

    public synchronized static void init(Context context, SpeechSynthesizeEngineInitListener speechSynthesizeEngineInitListener) {
        if (tts == null) {
            try {
                tts = (SpeechSynthesizeEngine) Class.forName(AiConfiguration.ttsClazz).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tts.init(context, speechSynthesizeEngineInitListener);
        }
    }

    public static SpeechSynthesizeEngine getTtsEngine() {
        return tts;
    }

    /**
     * 暂停上句未完成任务
     * @param text
     */
    public static void startSpeak(String text){
       getTtsEngine().startSpeak(true,text);
    }


}
