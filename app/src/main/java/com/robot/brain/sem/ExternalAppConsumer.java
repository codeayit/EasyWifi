package com.robot.brain.sem;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by lny on 2018/1/29.
 */

public class ExternalAppConsumer extends Consumer {

    public ExternalAppConsumer(AppAction appAction, Context context) {
        super(appAction, context);
    }

    @Override
    void excute() {
        if (getAppAction()!=null && !TextUtils.isEmpty(getAppAction().getString(key_app_name))){
            String result =  AppOpener.openExternalActivity(getContext(),getAppAction());
            if (!TextUtils.isEmpty(result)){
                t(result);
            }
        }
    }
}
