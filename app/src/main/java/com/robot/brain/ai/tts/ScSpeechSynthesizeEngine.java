package com.robot.brain.ai.tts;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalTTSEngine;
import com.aispeech.export.listeners.AITTSListener;
import com.ayit.klog.KLog;
import com.robot.brain.ai.AiConfiguration;


/**
 * Created by lny on 2018/1/8.
 */

public class ScSpeechSynthesizeEngine implements SpeechSynthesizeEngine {

    private AILocalTTSEngine mEngine;
    private boolean isInited = false;
    private String currentText;

    private SpeechSynthesizeEngineListener speechSynthesizeEngineListener;

    @Override
    public void init(Context context, final SpeechSynthesizeEngineInitListener speechSynthesizeEngineInitListener) {
        mEngine = AILocalTTSEngine.createInstance();//创建实例
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mEngine.setResource(AiConfiguration.tts_res);
        mEngine.setDictDbName(AiConfiguration.tts_dict);
        mEngine.init(context, new AITTSListener() {
            @Override
            public void onInit(int status) {
                if (status == AIConstant.OPT_SUCCESS) {
                   if (speechSynthesizeEngineInitListener!=null){
                       speechSynthesizeEngineInitListener.onSuccess();
                       isInited = true;
//                       TtsEngineUtil.getTtsEngine().startSpeak(true,"语音初始化完毕");
                   }
                } else {
                    if (speechSynthesizeEngineInitListener!=null){
                        speechSynthesizeEngineInitListener.onError(-1,"语音合成引擎初始化失败");
                    }
                    KLog.d("语音合成引擎初始化失败");
                }
            }

            @Override
            public void onError(String utteranceId, AIError error) {
                if (speechSynthesizeEngineListener!=null){
                    speechSynthesizeEngineListener.onError(utteranceId,-1,error.getError());
                }
                KLog.d("语音合成引擎初始化失败："+error.getError());
                KLog.d("思必驰语音合成 : onError");
            }

            @Override
            public void onReady(String utteranceId) {
                if (speechSynthesizeEngineListener!=null){
                    speechSynthesizeEngineListener.onSynthesizeFinish(utteranceId);
                }
            }

            @Override
            public void onCompletion(String utteranceId) {
                if (speechSynthesizeEngineListener!=null){
                    speechSynthesizeEngineListener.onSpeechFinish(utteranceId);
                }

            }

            @Override
            public void onProgress(int currentTime, int totalTime, boolean isRefTextTTSFinished) {
                if (speechSynthesizeEngineListener!=null){
                    speechSynthesizeEngineListener.onSpeechProgressChanged("0",currentTime,totalTime);
                }
            }
        }, AiConfiguration.APPKEY, AiConfiguration.SECRETKEY);//初始化合成引擎
        mEngine.setSpeechRate(0.80f);//设置语速
        mEngine.setDeviceId(Util.getIMEI(context.getApplicationContext()));
        mEngine.setUseSSML(false);
//        mEngine.setSpeechVolume(300);
    }

    @Override
    public void startSpeak(boolean isRestart, String text) {
        if (!TextUtils.isEmpty(text)){
            if (isRestart){
                mEngine.stop();
            }
            mEngine.setSavePath(Environment.getExternalStorageDirectory() + "/tts/"
                    + System.currentTimeMillis() + ".wav");
//            mEngine.setSSMLTextName("ttsText.xml");
            mEngine.speak(text,"1024");
            currentText = text;
        }
    }



    @Override
    public void stopSpeak() {
        mEngine.stop();
//        if (speechSynthesizeEngineListener!=null){
//            speechSynthesizeEngineListener.onSpeechFinish("stop");
//        }
    }

    @Override
    public void release() {
        mEngine.destroy();
    }

    @Override
    public void setSpeechSynthesizeEngineListener(SpeechSynthesizeEngineListener speechSynthesizeEngineListener) {
        this.speechSynthesizeEngineListener = speechSynthesizeEngineListener;
    }

    @Override
    public String getCurrentSpeakText() {
        return currentText;
    }

    @Override
    public boolean isInited() {
        return isInited;
    }
}
