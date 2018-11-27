package com.robot.brain.ai.auth;

import android.content.Context;

/**
 * Created by lny on 2018/1/8.
 */

public interface Auther {
    void init(Context context, OnAuthListener onAuthListener);
    void doAuth();
    boolean isAuthed();

}
