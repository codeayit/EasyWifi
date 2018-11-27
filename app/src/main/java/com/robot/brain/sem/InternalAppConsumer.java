package com.robot.brain.sem;

import android.content.Context;
import android.text.TextUtils;

import com.robot.brain.BrainApplication;

/**
 * Created by lny on 2018/1/29.
 */

public class InternalAppConsumer extends Consumer {

    public InternalAppConsumer(AppAction appAction, Context context) {
        super(appAction, context);
    }

    @Override
    void excute() {
        if (getAppAction()!=null && !TextUtils.isEmpty(getAppAction().getString(key_app_name))){
            AppAction appAction = getAppAction();
            switch (appAction.getAppName()) {
                case "say":
                    new SayConsumer(appAction, BrainApplication.getInstance()).excute();
                    break;
            }
        }
    }
}
