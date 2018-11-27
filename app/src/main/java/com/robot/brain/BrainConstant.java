package com.robot.brain;

import com.robot.baseapi.base.BaseConstant;

/**
 * Created by lny on 2018/1/11.
 */

public interface BrainConstant extends BaseConstant {
    String action = "com.robot.brain";
    String action_consumer = "com.robot.brain.consumer";
    String action_awake = "com.robot.brain.awake";



    final int asr_start = 100;
    final int asr_stop = 101;
    final int asr_success = 102;
    final int asr_fail = 103;
    final String key_asr_result = "asr_result";
    final int wakeup_start = 104;
    final int wakeup_stop = 105;
    final int wakeup_success = 106;
    final int wakeup_fail = 107;
    final String key_wakeup_result = "wakeup_result";


    final int tts_start = 200;
    final int tts_stop = 201;
    final int tts_success = 202;
    final int tts_fail = 203;
    final String key_tts_text = "tts_text";
    final String key_tts_isrestart = "tts_isrestart";

    int init_success = 300;
    int init_failed= 301;

    final int sem_success = 400;
    final int sem_fail = 401;
    final int sem_before = 402;
    final int sem_after = 403;
    final String key_sem_result = "sem_result";
    final String key_is_sem = "is_sem";


    int init  = 302;
    String key_command = "command";
    String key_braininit_result = "braininit_result";
    String key_appkey = "appkey";
    String key_msg = "msg";





    String p_q="q";
    String p_device ="device";
    String p_sign="sign";


//    String chat_start = "您找我有什么事?";
    String chat_start = "您找我有什么事？";
    String chat_end = "您好像没有说话哦，我休息一会!";
    String chat_unclear  ="没有听清您说什么，请再说一次吧!";


    /**
     * 表情常量
     */
    //正常的
    int normal = 0;
    //讲话
    int speaking = 1;
    //喜欢的
    int like = 2;
    //淡定的，平静的
    int calm = 3;
    //生气的
    int angry = 4;
    //困惑的
    int confused = 5;
    //藐视
    int despite = 6;
    //失望的
    int dissappointed = 7;
    //兴奋的激动的
    int excited = 8;
    //欢乐的，高兴的
    int joy = 9;
    //消极的
    int nagetive = 10;
    //积极的
    int positive = 11;
    // 顽皮的，淘气的
    int naughty = 12;
    // 自豪的；得意的
    int proud = 13;
    // 惊奇，诧异
    int surprise = 14;
    //可疑的
    int suspect = 15;
    //担心的
    int worry = 16;
    //难过的；悲哀的
    int  sad = 17;
    // 晕眩的
    int dizzy = 18;



    // 头部感应    文件路径
    String TOUCH_HEAD_COMMAND = "/sys/class/power_supply/battery/touch_adc_val";
}
