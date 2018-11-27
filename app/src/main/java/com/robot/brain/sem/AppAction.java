package com.robot.brain.sem;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by lny on 2018/1/12.
 */

public class AppAction extends JSONObject {

    public static final String TYPE_INTERNAL ="internal";
    public static final String TYPE_EXTERNAL = "app";

    private boolean isLast;

    public AppAction(Map<String, Object> map) {
        super(map);
        isLast = false;
        put("is_last",isLast);
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
        put("is_last",last);
    }

    public String getAppType(){
        return getString("app_type");
    }
    public String getAppName(){
        return getString("app_name");
    }

    public String getConversationKey() {
        return getString("conversation_key");
    }

    public void setConversationKey(String conversationKey) {
        put("conversation_key",conversationKey);
    }

    public String getPackage(){
        if (containsKey("package")){
            return getString("package");
        }else {
            return null;
        }
    }

    public String getAction(){
        if (containsKey("action")){
            return getString("action");
        }else{
            return null;
        }
    }
}
