package com.robot.brain.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.ayit.klog.KLog;
import com.robot.baseapi.base.BaseService;
import com.robot.baseapi.net.NetWork;
import com.robot.baseapi.util.LogUtil;
import com.robot.brain.BrainConstant;
import com.robot.brain.ai.auth.AuthUtil;
import com.robot.brain.ai.auth.OnAuthListener;
import com.robot.brain.ai.recognization.AsrEngineUtil;
import com.robot.brain.ai.recognization.RecognizationEngineInitListener;
import com.robot.brain.ai.recognization.RecognizationEngineListener;
import com.robot.brain.ai.tts.SpeechSynthesizeEngineInitListener;
import com.robot.brain.ai.tts.SpeechSynthesizeEngineListener;
import com.robot.brain.ai.tts.TtsEngineUtil;
import com.robot.brain.net.API;
import com.robot.brain.sem.ActionConsumer;
import com.robot.brain.sem.AppAction;
import com.robot.brain.sem.Conversation;
import com.robot.brain.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class AiService extends BaseService implements RecognizationEngineListener, SpeechSynthesizeEngineListener {

    private static final int brain_state_uninited = 0;
    private static final int brain_state_initting = 1;
    private static final int brain_state_initfailed = 2;
    private static final int brain_state_initsuccess = 3;
    private int brain_state = brain_state_uninited;


    private static final int asr_state_ready = 0;
    private static final int asr_state_running = 1;
    private static final int asr_state_isbusy = 2;
    private int asr_state;

    private boolean isSem = true;

    private static final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    private BroadcastReceiver commandReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("AiService : onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d("AiService : onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        KLog.d("AiService : onCreate");
        brain_state = brain_state_uninited;

//        initAuth();
    }

    private void initAuth() {
        KLog.d("initAuth");
        brain_state = brain_state_initting;
        AuthUtil.init(getApplicationContext(), new OnAuthListener() {
            @Override
            public void onAuthSuccess() {
                KLog.d("授权成功");
                initAsr();
            }

            @Override
            public void onAuthFailed(int code, String msg) {
                KLog.d("授权失败:"+msg);
                brain_state = brain_state_initfailed;
            }
        });
    }

    private void initAsr() {
        AsrEngineUtil.init(getApplicationContext(), new RecognizationEngineInitListener() {
            @Override
            public void onSuccess() {
                AsrEngineUtil.getAsrInstance().setRecognizationEngineListener(AiService.this);
                asr_state = asr_state_ready;
                KLog.d("语音识别初始化成功");
                initTts();
            }

            @Override
            public void onError(int code, String msg) {
                KLog.d("语音识别初始化失败:"+msg);
                brain_state = brain_state_initfailed;
            }
        });
    }

    private void initTts() {
        TtsEngineUtil.init(getApplicationContext(), new SpeechSynthesizeEngineInitListener() {
            @Override
            public void onSuccess() {
                KLog.d("语音合成初始化成功");
                KLog.d("语音初始化完毕");
                brain_state = brain_state_initsuccess;
                AsrEngineUtil.getAsrInstance().startWakeup();
                TtsEngineUtil.getTtsEngine().setSpeechSynthesizeEngineListener(AiService.this);
                isRunning = true;
                watchHeadTouchThread = new WatchHeadTouchThread();
                watchHeadTouchThread.start();
            }

            @Override
            public void onError(int code, String msg) {
                KLog.d("语音合成初始化失败:"+msg);
                brain_state = brain_state_initfailed;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d("AiService : onDestroy");
        if (commandReceiver != null) {
            unregisterReceiver(commandReceiver);
        }
//        throw new RuntimeException("Ai service onDestroy");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        KLog.d("AiService : onTrimMemory");
//        throw new RuntimeException("Ai service onTrimMemory");
    }

    //---------------------------asr start --------------------------


    private void startConversation(String speak){
        KLog.d("点亮屏幕");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
        mWakeLock.acquire(10*1000);//这里唤醒锁，用这种方式要记得在适当的地方关闭锁
//        发送唤醒广播

        AsrEngineUtil.getAsrInstance().cancle();


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
                KLog.d("tts : onSpeechFinish");
                isSem = true;
                AsrEngineUtil.getAsrInstance().stopWakeup();
                AsrEngineUtil.getAsrInstance().startAsr();
//                CommandHelper.chatEmotion(getApplicationContext(),BrainConstant.normal);
            }

            @Override
            public void onError(String utteranceId, int code, String errorMsg) {
                KLog.d("语音合成 onError  "+code +" : "+errorMsg);
            }
        });
        TtsEngineUtil.getTtsEngine().startSpeak(true,speak);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AsrEngineUtil.getAsrInstance().startWakeup();
            }
        },500);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AsrEngineUtil.getAsrInstance().startWakeup();
//            }
//        },500);
    }

    @Override
    public void onWakeup(){
        startConversation("在呢!在呢!");
    }

    @Override
    public void onEngineReadyForspeak() {


    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onCurrentSpeakVolume(float parcent) {

    }

    @Override
    public void onSpeakEnd() {

    }

    @Override
    public void onRecognizationTemp(int code, String tempResult) {

    }

    @Override
    public void onRecognizationSuccess(int code, String result) {
        //判断是否处理语义
        if (isSem){
            if (TextUtils.isEmpty(result)){
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
                        AsrEngineUtil.getAsrInstance().stopWakeup();
                        AsrEngineUtil.getAsrInstance().startAsr();
//                        CommandHelper.chatEmotion(getApplicationContext(),BrainConstant.normal);
                    }

                    @Override
                    public void onError(String utteranceId, int code, String errorMsg) {

                    }
                });
                TtsEngineUtil.getTtsEngine().startSpeak(true,BrainConstant.chat_unclear);
                AsrEngineUtil.getAsrInstance().startWakeup();
//                CommandHelper.chatRobot(getApplicationContext(),BrainConstant.chat_unclear,BrainConstant.speaking);
            }else{
//                CommandHelper.chatHuman(getApplicationContext(),result,BrainConstant.normal);
                sem(result);
            }
        }
    }

    @Override
    public void onRecognizationError(int code, String error) {
        if (isSem){
            if (code==-2){
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
                    }

                    @Override
                    public void onError(String utteranceId, int code, String errorMsg) {

                    }
                });
                TtsEngineUtil.getTtsEngine().startSpeak(true,BrainConstant.chat_end);
                AsrEngineUtil.getAsrInstance().startWakeup();
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
                        AsrEngineUtil.getAsrInstance().stopWakeup();
                        AsrEngineUtil.getAsrInstance().startAsr();
//                        CommandHelper.chatEmotion(getApplicationContext(),BrainConstant.normal);
                    }

                    @Override
                    public void onError(String utteranceId, int code, String errorMsg) {

                    }
                });
                TtsEngineUtil.getTtsEngine().startSpeak(true,BrainConstant.chat_unclear);
                AsrEngineUtil.getAsrInstance().startWakeup();
//                CommandHelper.chatRobot(getApplicationContext(),BrainConstant.chat_unclear,BrainConstant.speaking);
            }
        }

    }

    @Override
    public void onRecognizationFinish() {

    }

    @Override
    public void onEngineExit() {

    }
    //---------------------------asr end --------------------------


    //-------------------------tts start-----------------------------

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
    }

    @Override
    public void onError(String utteranceId, int code, String errorMsg) {
    }
    //-------------------------tts end-----------------------------


    public void sem(String asrResult){
        if (!TextUtils.isEmpty(asrResult)){
            NetWork.cancel(getApplication());
//            NetWork.getInstance(getApplicationContext())
//                    .post()
//                    .url(API.url_sem)
//                    .addParam(BrainConstant.p_q,asrResult)
//                    .build()
//                    .execute(new NetWorkStringCallBack() {
//                        @Override
//                        public void onBefore() {
//                            super.onBefore();
//                            CommandHelper.semBefore(getApplicationContext());
//                        }
//
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//                            CommandHelper.semFail(getApplicationContext(),e.getMessage());
//                        }
//
//                        @Override
//                        public void onResponse(JsonParser parser) {
//                            CommandHelper.semSuccess(getApplicationContext(),parser.toJsonString());
//                            JSONArray appActions = parser.parseAppActions();
//                            boolean isKeep = parser.parseKeepPlay().equals("N")?false:true;
//                            if (appActions!=null && !appActions.isEmpty()){
//                                List<AppAction> temp = new ArrayList<AppAction>();
//                                for (int i=0;i<appActions.size();i++){
//                                    temp.add(new AppAction(appActions.getJSONObject(i)));
//                                }
//                                Conversation conversation = new Conversation(temp,isKeep);
//                                ActionConsumer.getInstance().addConversation(conversation);
//                                ActionConsumer.getInstance().excute();
//
//                            }
//                        }
//
//                        @Override
//                        public void onAfter() {
//                            super.onAfter();
//                            CommandHelper.semAfter(getApplicationContext());
//                            AsrEngineUtil.getAsrInstance().stopAsr();
//                            AsrEngineUtil.getAsrInstance().startWakeup();
//                        }
//                    });
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private boolean isRunning;
    private WatchHeadTouchThread watchHeadTouchThread = null;

    //监控头部触摸状态
    private class WatchHeadTouchThread extends Thread {
        private  boolean isTouching = false;
        @Override
        public void run() {
            try {
                while (isRunning) {
                    String command = FileUtil.readFile(BrainConstant.TOUCH_HEAD_COMMAND);
                    //小于100则认为是正在触摸，其它情况则是没有触摸
                    if (Integer.valueOf(command) < 100) {
                        if (!isTouching) {
                            isTouching = true;
                            KLog.d("摸头："+command);
                            startConversation("请说话!");
                        }
                    } else {
                        isTouching = false;
                    }
                    this.sleep(200);
                }
            } catch (InterruptedException e) {
            }
        }
    }
}