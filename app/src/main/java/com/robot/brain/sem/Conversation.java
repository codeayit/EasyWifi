package com.robot.brain.sem;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by lny on 2018/1/12.
 */

public class Conversation extends LinkedList<AppAction> {
    private String converstaion_key;
    private boolean isKeep;

    public Conversation(@NonNull Collection<? extends AppAction> c, boolean isKeep) {
        super(c);
        this.isKeep = isKeep;
        this.converstaion_key = String.valueOf(System.currentTimeMillis());
    }

    public String getConverstaion_key() {
        return converstaion_key;
    }

    @Override
    public AppAction removeFirst() {
        AppAction appAction = super.removeFirst();
        if (appAction != null)
            appAction.setConversationKey(converstaion_key);
        return appAction;
    }

    public boolean isKeep() {
        return isKeep;
    }

    public void setKeep(boolean keep) {
        isKeep = keep;
    }
}
