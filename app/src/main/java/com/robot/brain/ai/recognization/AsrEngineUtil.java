package com.robot.brain.ai.recognization;

import android.content.Context;

import com.robot.brain.ai.AiConfiguration;


/**
 * Created by lny on 2018/1/8.
 */

public class AsrEngineUtil {
    private static RecognizationEngine asr;

    public static synchronized void init(Context context,RecognizationEngineInitListener recognizationEngineInitListener){
        try {
            asr = (RecognizationEngine)Class.forName(AiConfiguration.asrClazz).newInstance();
            asr.ini(context,recognizationEngineInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RecognizationEngine getAsrInstance(){
        return  asr;
    }

}
