package com.robot.brain.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.robot.brain.R;
import com.robot.brain.ai.tts.SpeechSynthesizeEngineInitListener;
import com.robot.brain.ai.tts.TtsEngineUtil;


public class TtsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        TtsEngineUtil.init(this, new SpeechSynthesizeEngineInitListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"初始化成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(getApplicationContext(),"初始失败："+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startTts(View view){
        TtsEngineUtil.startSpeak("尊敬的各位同事尊敬的各位来宾女士们先生们朋友们大家晚上好");
    }

    public void stopTts(View view){
        TtsEngineUtil.getTtsEngine().stopSpeak();
    }


}
