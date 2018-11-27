package com.robot.brain;

import android.content.Intent;

import com.aispeech.common.AIConstant;
import com.robot.baseapi.base.BaseApplication;
import com.robot.brain.ai.AiConfiguration;
import com.robot.brain.service.AiService;
import com.robot.brain.service.WsService;

/**
 * Created by lny on 2018/1/10.
 */

public class BrainApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        iniAi();
        iniNetWork();
        startService(new Intent(this, AiService.class));
        startService(new Intent(this, WsService.class));
    }

    private void iniNetWork() {

    }

    private void iniAi() {
        AIConstant.openLog();
        AIConstant.setNewEchoEnable(true);// 打开AEC
        AIConstant.setEchoCfgFile(AiConfiguration.aec_res);// 设置AEC的配置文件
//        AIConstant.setRecChannel(2);// 默认为1,即左通道为rec录音音频,右通道为play参考音频（播放音频）若设置为2,
//        通道会互换，即左通道为play参考音频（播放音频）,右通道为rec录音音频
    }


}
