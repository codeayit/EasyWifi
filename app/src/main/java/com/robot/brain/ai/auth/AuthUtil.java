package com.robot.brain.ai.auth;

import android.content.Context;

import com.robot.brain.ai.AiConfiguration;


/**
 * Created by lny on 2018/1/8.
 */

public class AuthUtil {
    private static Auther auther;
    public static synchronized void init(Context context,OnAuthListener onAuthListener){
        try {
            auther = (Auther) Class.forName(AiConfiguration.authClazz).newInstance();
            auther.init(context,onAuthListener);
            auther.doAuth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Auther getInstance(){
        return auther;
    }




}
