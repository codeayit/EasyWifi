package com.robot.brain.ai.auth;

import android.content.Context;

import com.aispeech.export.listeners.AIAuthListener;
import com.aispeech.speech.AIAuthEngine;
import com.ayit.klog.KLog;
import com.robot.brain.ai.AiConfiguration;

import java.io.FileNotFoundException;


/**
 * Created by lny on 2018/1/8.
 */

public class ScAuther implements Auther {

    private AIAuthEngine authEngine;
    private OnAuthListener onAuthListener;

    @Override
    public void init(Context context, final OnAuthListener onAuthListener) {
        this.onAuthListener = onAuthListener;
        authEngine = AIAuthEngine.getInstance(context.getApplicationContext());
//        mEngine.setResStoragePath("/system/vender/aispeech");//设置自定义路径，请将相关文件预先放到该目录下
        try {
            authEngine.init(AiConfiguration.APPKEY, AiConfiguration.SECRETKEY, "");
            authEngine.setOnAuthListener(new AIAuthListener() {
                @Override
                public void onAuthSuccess() {
                    if (onAuthListener != null) {
                        onAuthListener.onAuthSuccess();
                    }
                    KLog.d("授权引擎初始化成功");
                }

                @Override
                public void onAuthFailed(String s) {
                    if (onAuthListener != null) {
                        onAuthListener.onAuthFailed(-1, s);
                    }
                    KLog.d("授权引擎初始化失败："+s);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doAuth() {
        if (authEngine == null) {
            throw new RuntimeException("Auther must init first or Auther init filed");
        }
        if (authEngine.isAuthed()){
            if (this.onAuthListener!=null){
                onAuthListener.onAuthSuccess();
            }
        }else{
            authEngine.doAuth();
        }

    }

    @Override
    public boolean isAuthed() {
        if (authEngine == null) {
            throw new RuntimeException("Auther must init first or Auther init filed");
        }
        return authEngine.isAuthed();
    }

}
