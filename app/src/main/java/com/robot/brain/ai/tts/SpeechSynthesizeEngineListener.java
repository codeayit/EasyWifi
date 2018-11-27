package com.robot.brain.ai.tts;


/**
 * Created by lny on 2017/10/20.
 */

public interface SpeechSynthesizeEngineListener {
    /**
     * 本次合成过程开始时，SDK的回调
     * @param utteranceId
     */
    void onSynthesizeStart(String utteranceId);

    /**
     * 合成数据过程中的回调接口，返回合成数据和进度，分多次回调。
     * @param utteranceId
     * @param audioData 合成的部分数据，可以就这部分数据自行播放或者顺序保存到文件。如果保存到文件的话，是一个pcm可以播放的音频文件。 音频数据是16K采样率，16bits编码，单声道。
     * @param progress 大致进度。从0 到 “合成文本的字符数”。
     */
    void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress);

    /**
     * SDK开始控制播放器播放合成的声音。如果使用speak方法会有此回调，使用synthesize没有。
     * @param utteranceId
     */
    void onSynthesizeFinish(String utteranceId);

    /**
     * 播放数据过程中的回调接口，分多次回调。
     如果使用speak方法会有此回调，使用synthesize没有。
     * @param utteranceId
     * @param progress 大致进度。从0 到 “合成文本的字符数”。
     */
    void onSpeechProgressChanged(String utteranceId, int progress, int totalProgress);

    void onSpeechFinish(String utteranceId);

    void  onError(String utteranceId, int code, String errorMsg);
}
