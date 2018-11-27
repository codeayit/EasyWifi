package com.robot.brain.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ayit.klog.KLog;
import com.robot.brain.R;
import com.robot.brain.ai.recognization.AsrEngineUtil;
import com.robot.brain.ai.recognization.RecognizationEngineInitListener;
import com.robot.brain.ai.recognization.RecognizationEngineListener;



public class AsrActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        AsrEngineUtil.init(this, new RecognizationEngineInitListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getBaseContext(), "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(getBaseContext(), "初始化失败", Toast.LENGTH_SHORT).show();
                KLog.d("error : " + msg);
            }
        });
        AsrEngineUtil.getAsrInstance().setRecognizationEngineListener(new RecognizationEngineListener() {
            @Override
            public void onWakeup() {

            }

            @Override
            public void onEngineReadyForspeak() {
                KLog.d("onEngineReadyForspeak : ");
            }

            @Override
            public void onSpeakBegin() {
                KLog.d("onSpeakBegin : ");
            }

            @Override
            public void onCurrentSpeakVolume(float parcent) {
                KLog.d("onCurrentSpeakVolume : " + parcent);
            }

            @Override
            public void onSpeakEnd() {
                KLog.d("onSpeakEnd : ");
            }

            @Override
            public void onRecognizationTemp(int code, String tempResult) {
                KLog.d("onRecognizationTemp : " + tempResult);
            }

            @Override
            public void onRecognizationSuccess(int code, String result) {
                KLog.d("onRecognizationSuccess : " + result);
                AsrEngineUtil.getAsrInstance().startWakeup();

            }

            @Override
            public void onRecognizationError(int code, String error) {
                KLog.d("onRecognizationError : " + error);
                AsrEngineUtil.getAsrInstance().startWakeup();
            }

            @Override
            public void onRecognizationFinish() {
                KLog.d("onRecognizationFinish : ");
            }

            @Override
            public void onEngineExit() {
                KLog.d("onEngineExit : ");
            }
        });
    }

    public void startAsr(View view) {
        AsrEngineUtil.getAsrInstance().startAsr();
    }

    public void stopAsr(View view) {
        AsrEngineUtil.getAsrInstance().stopAsr();
    }

    public void cancleAsr(View view) {
        AsrEngineUtil.getAsrInstance().cancle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AsrEngineUtil.getAsrInstance().release();
    }
}
