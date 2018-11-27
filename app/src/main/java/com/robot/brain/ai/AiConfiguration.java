package com.robot.brain.ai;

/**
 * Created by lny on 2018/1/8.
 */

public interface AiConfiguration {


    //auth
    String authClazz = "com.robot.brain.ai.auth.ScAuther";


//    public static final String APPKEY = "14709940938595d7";// 测试激活码用的appkey
//    public static final String SECRETKEY = "80c88d6ee0b391a71fd8ebb63103d7eb";//添加您的SECRETKEY"

    public static final String APPKEY = "15371865824584bd";// 测试激活码用的appkey
    public static final String SECRETKEY = "1415d146fbf715bef9128321c7a27eae";//添加您的SECRETKEY"

    //tts
    String ttsClazz = "com.robot.brain.ai.tts.ScSpeechSynthesizeEngine";



    //asr
    String asrClazz = "com.robot.brain.ai.recognization.ScRecognizationEngine";


    public static String vad_res = "aicar_vad_v.10.bin";
    public static String ebnfc_res = "ebnfc.aicar.1.2.0.bin";
    public static String ebnfr_res = "ebnfr.aicar.1.2.0.bin";
    public static String local_asr_net_bin = "asr.net.bin";
    public static String tts_res = "qianranc_common_param_ce_local.v2.023.bin";
    public static String tts_res1 = "qianran.v2.4.14.bin";
    public static String tts_dict = "aitts_sent_dict_v3.26.db";
    public static String aec_res = "AEC_ch2-2-ch1_1ref_common_20180510_v0.9.4.bin";
    public static String wakeup_res = "wakeup_aifar_comm_20180104.bin";
    public static String nr_res = "NR_ch1-2-ch1_com_20171117_v1.0.0.bin";



}
