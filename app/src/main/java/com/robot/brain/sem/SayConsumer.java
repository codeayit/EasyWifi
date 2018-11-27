package com.robot.brain.sem;

import android.content.Context;
import android.text.TextUtils;

import com.robot.brain.BrainConstant;
import com.robot.brain.ai.recognization.AsrEngineUtil;
import com.robot.brain.ai.tts.SpeechSynthesizeEngineListener;
import com.robot.brain.ai.tts.TtsEngineUtil;

/**
 * Created by lny on 2018/1/12.
 */

public class SayConsumer extends Consumer {
    public SayConsumer(AppAction appAction, Context context) {
        super(appAction, context);
    }

//    {
//        "app_name":"say",
//            "apk_id":"com.jianos.say",
//            "user_app_id":123,
//            "action":"say",
//            "value":"马上为你播放《游戏问答转圈圈》"
//    }


    @Override
    void excute() {
        if (getAppAction() != null && getAppAction().getString(key_app_name).equals(app_name_say)) {
//            final String value = getAppAction().getString("value");
            String value = "中国天气网讯 今明天（13-14日），华北、黄淮等地持续回暖，同时，大气扩散逐渐转差，将有轻至中度霾。而在南方，西南地区阴雨雪天气持续，特别是在云南西部等地有中到大雨，局地暴雨，需要警惕地质灾害；同时，降雨向东蔓延扩展，江南、华南大部将逐步被雨水笼罩。";
            if (!TextUtils.isEmpty(value)){

//                CommandHelper.chatRobot(getContext(),value,1);
                TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(new SpeechSynthesizeEngineListener() {
                    @Override
                    public void onSynthesizeStart(String utteranceId) {

                    }

                    @Override
                    public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {

                    }

                    @Override
                    public void onSynthesizeFinish(String utteranceId) {

                    }

                    @Override
                    public void onSpeechProgressChanged(String utteranceId, int progress, int totalProgress) {

                    }

                    @Override
                    public void onSpeechFinish(String utteranceId) {
//                        CommandHelper.sendNextAppAction(getContext());
                        if (getAppAction().isLast()){
                            AsrEngineUtil.getAsrInstance().stopWakeup();
                            AsrEngineUtil.getAsrInstance().startAsr();
                        }
//                        EmotionHelper.chatEmotion(getContext(),0);
                        TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(null);
                    }

                    @Override
                    public void onError(String utteranceId, int code, String errorMsg) {
                        TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(null);
//                        CommandHelper.chatEmotion(getContext(),0);
//                        EmotionHelper.chatEmotion(getContext(),BrainConstant.normal);
                    }
                });
                AsrEngineUtil.getAsrInstance().startWakeup();
                TtsEngineUtil.getTtsEngine().startSpeak(true, value);
//                EmotionHelper.chatRobot(getContext(),BrainConstant.speaking,value,false);

            }else{
                TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(new SpeechSynthesizeEngineListener() {
                    @Override
                    public void onSynthesizeStart(String utteranceId) {

                    }

                    @Override
                    public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {

                    }

                    @Override
                    public void onSynthesizeFinish(String utteranceId) {

                    }

                    @Override
                    public void onSpeechProgressChanged(String utteranceId, int progress, int totalProgress) {

                    }

                    @Override
                    public void onSpeechFinish(String utteranceId) {
//                        CommandHelper.sendNextAppAction(getContext());
                        if (getAppAction().isLast()){
                            AsrEngineUtil.getAsrInstance().stopWakeup();
                            AsrEngineUtil.getAsrInstance().startAsr();
                        }
//                        EmotionHelper.chatEmotion(getContext(),BrainConstant.normal);
                        TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(null);
                    }

                    @Override
                    public void onError(String utteranceId, int code, String errorMsg) {
                        TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(null);
//                        EmotionHelper.chatEmotion(getContext(),BrainConstant.normal);
                    }
                });
                AsrEngineUtil.getAsrInstance().startWakeup();
                TtsEngineUtil.getTtsEngine().startSpeak(true,BrainConstant.chat_unclear);
//                CommandHelper.chatRobot(getContext(),BrainConstant.chat_unclear,1);
//                EmotionHelper.chatRobot(getContext(),BrainConstant.speaking,BrainConstant.chat_unclear,false);

            }
        }
    }


}
