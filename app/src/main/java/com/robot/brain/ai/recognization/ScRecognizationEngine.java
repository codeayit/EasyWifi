package com.robot.brain.ai.recognization;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.common.AIConstant;
import com.aispeech.common.Util;
import com.aispeech.export.engines.AILocalGrammarEngine;
import com.aispeech.export.engines.AILocalWakeupDnnEngine;
import com.aispeech.export.engines.AIMixASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.aispeech.export.listeners.AILocalGrammarListener;
import com.aispeech.export.listeners.AILocalWakeupDnnListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ayit.klog.KLog;
import com.robot.baseapi.util.NetworkUtil;
import com.robot.brain.ai.AiConfiguration;
import com.robot.brain.ai.GrammarHelper;


/**
 * Created by lny on 2018/1/8.
 */

public class ScRecognizationEngine implements RecognizationEngine {

    private AILocalWakeupDnnEngine mWakeupEngine;
    private AILocalGrammarEngine mGrammarEngine;
    private AIMixASREngine mAsrEngine;
    private RecognizationEngineListener recognizationEngineListener;
    private boolean isInited = false;

    private boolean isAsrEngineRunning = false;
    private boolean isWakeupEngineRunning = false;

//    BeepPlayer startBeep;

    private long startTime, endTime;

    @Override
    public void ini(final Context context, final RecognizationEngineInitListener recognizationEngineInitListener) {
        startTime = System.currentTimeMillis();
        initWakeupDnnEngine(context, recognizationEngineInitListener);
    }

    private static final int MSG_START_ASR = 1;
    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_START_ASR:
//                    mAsrEngine.start();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     *  step 2
     * @param context
     * @param recognizationEngineInitListener
     */
    private void initWakeupDnnEngine(final Context context, final RecognizationEngineInitListener recognizationEngineInitListener) {
        KLog.d("initWakeupDnnEngine");
//        startBeep = new BeepPlayer(context, R.raw.open);
//        startBeep.setOnCompletionTask(startBeep.new OnCompletionTask(mHandler, MSG_START_ASR));


        mWakeupEngine = AILocalWakeupDnnEngine.createInstance(); //创建实例
//        mEngine.setResStoragePath("/sdcard/aispeech/");//设置自定义目录放置资源，如果要设置，请预先把相关资源放在该目录下
        mWakeupEngine.setResBin(AiConfiguration.wakeup_res); //非自定义唤醒资源可以不用设置words和thresh，资源已经自带唤醒词
//        mEngine.setEchoWavePath("/sdcard/speech"); //保存aec音频到/sdcard/speech/目录,请确保该目录存在
        mWakeupEngine.init(context, new AILocalWakeupDnnListener() {
            @Override
            public void onInit(int status) {
                if (status == AIConstant.OPT_SUCCESS) {
//                    resultText.append("初始化成功!");
                    KLog.d("唤醒初始化成功");
                    initGrammarEngine(context, recognizationEngineInitListener);
//                    mWakeupEngine.start();
                } else {
//                    resultText.setText("初始化失败!code:" + status);
                    if (recognizationEngineInitListener != null)
                        recognizationEngineInitListener.onError(status, "唤醒初始化失败");
                }
            }

            @Override
            public void onError(AIError aiError) {
                KLog.d("唤醒初始化失败  ：" + aiError.getError());
                if (recognizationEngineInitListener != null)
                    recognizationEngineInitListener.onError(aiError.getErrId(), aiError.getError());
            }

            @Override
            public void onWakeup(String recordId, double confidence, String wakeupWord) {
                KLog.d("唤醒次："+wakeupWord);
                isWakeupEngineRunning = false;
                if (recognizationEngineListener != null) {
                    recognizationEngineListener.onWakeup();
                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startBeep.playBeep();
//                    }
//                }).start();
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onRecorderReleased() {

            }

            @Override
            public void onReadyForSpeech() {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onWakeupEngineStopped() {

            }
        }, AiConfiguration.APPKEY, AiConfiguration.SECRETKEY);
        mWakeupEngine.setStopOnWakeupSuccess(true);//设置当检测到唤醒词后自动停止唤醒引擎
//        mEngine.setWords(new String[] {"ni hao xiao le","ni hao xiao mo","xiao mo"});
        mWakeupEngine.setWords(new String[] {"xiao mo","sheng yin da yi dian"});
        mWakeupEngine.setThreshold(new float[] {0.25f,0.25f});
        //开启音频上传功能
//        if (getExternalCacheDir() != null) {
//            ///storage/sdcard0/Android/data/com.aispeech.sample/cache
//            mWakeupEngine.setTmpDir(getExternalCacheDir().getAbsolutePath()); //SDK 会自动生成唤醒音频，生成时，要保证此文件夹存在
//            mWakeupEngine.setUploadEnable(true);
//            mWakeupEngine.setUploadInterval(10000);
//        }
        mWakeupEngine.setNetWorkState("WIFI");
    }

    /**
     *  step 1
     * @param context
     * @param recognizationEngineInitListener
     */
    private void initGrammarEngine(final Context context, final RecognizationEngineInitListener recognizationEngineInitListener) {


        mGrammarEngine = AILocalGrammarEngine.createInstance();
//      mGrammarEngine.setResStoragePath("/system/vender/aispeech");//设置自定义路径，请将相关文件预先放到该目录下
        mGrammarEngine.setResFileName(AiConfiguration.ebnfc_res);
        mGrammarEngine.init(context, new AILocalGrammarListener() {
            @Override
            public void onInit(int status) {
                if (status != 0) {
                    if (recognizationEngineInitListener != null)
                        recognizationEngineInitListener.onError(-1, "本地语法编译引擎初始化失败");
                    KLog.d("本地语法编译引擎初始化失败");
                } else {
                    mGrammarEngine.setDeviceId(Util.getIMEI(context));
                    // 生成ebnf语法
                    GrammarHelper gh = new GrammarHelper(context.getApplicationContext());
                    String contactString = gh.getConatcts();
                    contactString = "";
                    String appString = gh.getApps();
                    // 如果手机通讯录没有联系人
                    if (TextUtils.isEmpty(contactString)) {
                        contactString = "无联系人";
                    }
                    String ebnf = gh.importAssets(contactString, "", "asr.xbnf");
                    // 设置ebnf语法
                    mGrammarEngine.setEbnf(ebnf);
                    // 启动语法编译引擎，更新资源
                    mGrammarEngine.update();
                    KLog.d("本地语法编译引擎初始化成功");
//                    initWakeupDnnEngine(context,recognizationEngineInitListener);
                }


            }

            @Override
            public void onError(AIError aiError) {
                if (recognizationEngineInitListener != null) {
                    recognizationEngineInitListener.onError(-1, aiError.getError());
                    KLog.d("本地语法编译引擎初始化成功");
                }
            }

            @Override
            public void onUpdateCompleted(String recordId, String path) {
                KLog.d("资源生成/更新成功\npath=" + path + "\n重新加载识别引擎...");
                initAsrEngine(context, recognizationEngineInitListener);
            }
        }, AiConfiguration.APPKEY, AiConfiguration.SECRETKEY);

    }

    /**
     *  step 3
     * @param context
     * @param recognizationEngineInitListener
     */
    private void initAsrEngine(final Context context, final RecognizationEngineInitListener recognizationEngineInitListener) {
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
        }
        mAsrEngine = AIMixASREngine.createInstance();
        mAsrEngine.setResBin(AiConfiguration.ebnfr_res);
        mAsrEngine.setNetBin(AILocalGrammarEngine.OUTPUT_NAME, true);
        mAsrEngine.setVadResource(AiConfiguration.vad_res);
        mAsrEngine.setServer("ws://s.api.aispeech.com:1028,ws://s.api.aispeech.com:80"); //正式产品环境
        mAsrEngine.setUseXbnfRec(true);
//        mAsrEngine.setRes("aihome");
        mAsrEngine.setRes("airobot");
        mAsrEngine.setUseForceout(false);
//        mAsrEngine.setUsePinyin(true);
        mAsrEngine.setAthThreshold(0.6f);
        mAsrEngine.setIsRelyOnLocalConf(true);
        mAsrEngine.setIsPreferCloud(false);
//        mAsrEngine.setLocalBetterDomains(new String[]{"open_resource_manager", "about_us", "return_home", "reset_factory", "open_gallery", "setup_brightness", "setup_time", "open_radio", "open_network", "bind_app", "open_message", "stop_play", "voice_louder", "voice_lower", "take_photo",
//                "open_remind", "close_remind", "open_bluetooth", "open_ktv", "battery_query", "current_version",
//                "setting_alarm", "open_alarm", "open_wake", "close_wake", "open_setting", "finish_chat", "open_my",
//                "open_treasure", "open_download", "open_call_list", "open_store", "open_call", "query_version", "dizzy", "like", "naughty",
//                "angry", "sad", "happy", "SPEECH_STOP_CAMERA", "open_colorful_lamp", "close_colorful_lamp", "ok", "voice_max", "voice_min",
//                "open_protect_eyes", "close_protect_eyes", "open_moxin_jiaoyu"});
        mAsrEngine.setLocalBetterDomains(new String[] { "phone", "wechat"});
        //新
        mAsrEngine.setCloudNotGoodAtDomains(new String[]{"phonecall","weixin"});
        mAsrEngine.putCloudLocalDomainMap("weixin", "wechat");
        mAsrEngine.putCloudLocalDomainMap("phonecall", "phone");

        mAsrEngine.setWaitCloudTimeout(5 * 1000);
        mAsrEngine.setPauseTime(200);
        mAsrEngine.setNoSpeechTimeOut(6 * 1000);
        mAsrEngine.setUseConf(true);
        mAsrEngine.setCloudVadEnable(false);
        if (context.getExternalCacheDir() != null) {
            //设置上传音频使能
            mAsrEngine.setUploadEnable(true);
            //设置上传的音频保存在本地的目录
            mAsrEngine.setTmpDir(context.getExternalCacheDir().getAbsolutePath());
        }
//        mAsrEngine.setMergeRule(new IMergeRule() {
//            @Override
//            public AIResult mergeResult(AIResult localResult, AIResult cloudResult) {
//                AIResult result = null;
//                if (cloudResult == null) {
//                    // 为结果增加标记,以标示来源于云端还是本地
//                    JSONObject localJsonObject = JSON.parseObject(localResult.getResultObject()
//                            .toString());
//                    localJsonObject.put("src", "native");
//                    localResult.setResultObject(localJsonObject);
//                    result = localResult;
//                } else {
//                    JSONObject cloudJsonObject = JSON.parseObject(cloudResult.getResultObject()
//                            .toString());
//                    cloudJsonObject.put("src", "cloud");
//                    cloudResult.setResultObject(cloudJsonObject);
//                    result = cloudResult;
//                }
//                return result;
//            }
//        });
        mAsrEngine.init(context, new AIASRListener() {
            @Override
            public void onInit(int status) {
                if (status == 0) {
                    isInited = true;
                    if (NetworkUtil.isWifiConnected(context)) {
                        if (mAsrEngine != null) {
                            mAsrEngine.setNetWorkState("WIFI");
                        }
                    }
                    if (recognizationEngineInitListener != null) {
                        recognizationEngineInitListener.onSuccess();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (true) {
//                                    SystemClock.sleep(10 * 1000);
//                                    KLog.d("asrengine : "+isAsrEngineRunning);
//                                    KLog.d("wakeupengine : "+isWakeupEngineRunning);
//                                }
//                            }
//                        }).start();
                    }

                } else {
                    if (recognizationEngineListener != null) {
                        recognizationEngineInitListener.onError(-1, "语音识别引擎初始化失败");
                    }
                }
                endTime = System.currentTimeMillis();
            }

            @Override
            public void onError(AIError aiError) {
                if (recognizationEngineListener != null) {
                    if (aiError.getErrId() == 70904) {
                        recognizationEngineListener.onRecognizationError(-2, aiError.getError());
                    } else {
                        recognizationEngineListener.onRecognizationError(-1, aiError.getError());
                    }
                }
                isAsrEngineRunning = false;
            }

            @Override
            public void onResults(AIResult aiResult) {
                isAsrEngineRunning = false;
                KLog.d(aiResult.getResultObject().toString());
                if (recognizationEngineListener != null) {
                    if (aiResult.isLast()) {
                        JSONObject result = JSON.parseObject(aiResult.getResultObject().toString());
                        if (result.containsKey("src")) {
                            if (result.getString("src").equals("native")) {
                                recognizationEngineListener.onRecognizationSuccess(1, result.getJSONObject("result").getString("rec").replaceAll(" ", ""));
                            } else if (result.getString("src").equals("cloud")) {
                                recognizationEngineListener.onRecognizationSuccess(0, result.getJSONObject("result").getString("input"));
                            }
                        } else {
                            recognizationEngineListener.onRecognizationSuccess(0, result.getJSONObject("result").getString("input"));
                        }

                    } else {
                        JSONObject result = JSON.parseObject(aiResult.getResultObject().toString());
                        if (result.containsKey("src")) {
                            if (result.getString("src").equals("native")) {
                                recognizationEngineListener.onRecognizationTemp(1, result.getJSONObject("result").getString("rec").replaceAll(" ", ""));
                            } else if (result.getString("src").equals("cloud")) {
                                recognizationEngineListener.onRecognizationTemp(0, result.getJSONObject("result").getString("input"));
                            }
                        } else {
                            recognizationEngineListener.onRecognizationTemp(0, result.getJSONObject("result").getString("input"));
                        }
                    }
                }
            }

            @Override
            public void onRmsChanged(float percent) {
                if (recognizationEngineListener != null) {
                    recognizationEngineListener.onCurrentSpeakVolume(percent);
                }
            }

            @Override
            public void onReadyForSpeech() {
                if (recognizationEngineListener != null) {
                    recognizationEngineListener.onEngineReadyForspeak();
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                if (recognizationEngineListener != null) {
                    recognizationEngineListener.onSpeakBegin();
                }
            }

            @Override
            public void onEndOfSpeech() {
                if (recognizationEngineListener != null) {
                    recognizationEngineListener.onSpeakEnd();
                }
            }

            @Override
            public void onRecorderReleased() {
                KLog.d("onRecorderReleased");
            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onNotOneShot() {
                KLog.d("onNotOneShot");
            }
        }, AiConfiguration.APPKEY, AiConfiguration.SECRETKEY);
        mAsrEngine.setUseCloud(true);//该方法必须在init之后

//        mAsrEngine.setUserId("AISPEECH"); //填公司名字

//        mAsrEngine.setCoreType("cn.sds"); //cn.sds为云端对话服务，cn.dlg.ita为云端语义服务，默认为云端语义,想要访问对话服务时，才设置为cn.sds，否则不用设置
    }

    @Override
    public void setRecognizationEngineListener(RecognizationEngineListener recognizationEngineListener) {
        this.recognizationEngineListener = recognizationEngineListener;
    }

    @Override
    public void startWakeup() {
        if (mAsrEngine.isBusy()) {
            mAsrEngine.stopRecording();
        } else {
            mAsrEngine.cancel();
        }
        mWakeupEngine.start();
        isWakeupEngineRunning = true;
    }

    @Override
    public void startAsr() {
        if (mAsrEngine.isBusy()) {
            mAsrEngine.cancel();
        }
        mAsrEngine.start();
        isAsrEngineRunning = true;
    }

    @Override
    public void cancle() {
//        mGrammarEngine.cancel();
        if (mAsrEngine.isBusy()) {
            mAsrEngine.cancel();
        } else {
            mWakeupEngine.stop();
        }
        isAsrEngineRunning = false;
        isWakeupEngineRunning = false;
    }

    @Override
    public void stopAsr() {
        if (mAsrEngine.isBusy()) {
            mAsrEngine.stopRecording();
        } else {
            mAsrEngine.cancel();
        }
        isAsrEngineRunning = false;
    }

    @Override
    public void stopWakeup() {
        if (mAsrEngine.isBusy()) {
            mAsrEngine.cancel();
        }
        mWakeupEngine.stop();
        isWakeupEngineRunning = false;
        KLog.d("stopWakeup");
    }

    @Override
    public void stopBoth() {
        stopAsr();
        stopWakeup();
    }


    @Override
    public void release() {
        if (mGrammarEngine != null) {
            mGrammarEngine.destroy();
            mGrammarEngine = null;
        }
        if (mAsrEngine != null) {
            mAsrEngine.destroy();
            mAsrEngine = null;
        }
        if (recognizationEngineListener != null) {
            recognizationEngineListener.onEngineExit();
        }

        if (mWakeupEngine != null) {
            mWakeupEngine.destroy();
            mWakeupEngine = null;
        }
    }

    @Override
    public boolean isInited() {
        return isInited;
    }
}
