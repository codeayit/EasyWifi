package com.robot.brain.sem;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lny on 2018/1/12.
 */

public abstract class Consumer implements ConsumerInterface{

    private AppAction appAction;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AppAction getAppAction() {
        return appAction;
    }

    public void setAppAction(AppAction appAction) {
        this.appAction = appAction;
    }

    public Consumer(AppAction appAction, Context context) {
        this.appAction = appAction;
        this.context = context;
    }

    abstract void excute();

    public void t(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

}
