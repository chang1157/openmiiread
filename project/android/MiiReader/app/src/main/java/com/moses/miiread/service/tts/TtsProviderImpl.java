package com.moses.miiread.service.tts;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.service.ReadAloudService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private ReadAloudService service;

    private Boolean langOk = false;

    public void init(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }

    @Override
    public void bindReadAloudService(ReadAloudService service) {
        this.service = service;
        if (tts != null)
            tts.setOnUtteranceProgressListener(new ReadAloudService.ttsUtteranceListener(service));
    }

    public Boolean getLangOk() {
        return langOk;
    }

    public void setLangOk(Boolean langOk) {
        this.langOk = langOk;
    }

    @Override
    public TextToSpeech getTts() {
        return tts;
    }

    @Override
    public void setTts(TextToSpeech tts) {
        this.tts = tts;
    }

    @Override
    public void say(int nowSpeak, List<String> contentList) {
        if (service == null)
            return;
        //
        if (tts != null) {
            if (!langOk) {
                Toast.makeText(service, service.getString(R.string.tts_fix), Toast.LENGTH_SHORT).show();
                //先停止朗读服务方便用户设置好后的重试
                ReadAloudService.stop(service);
                //跳转到文字转语音设置界面
                toTTSSetting();
            }
        } else {
            Toast.makeText(service, service.getString(R.string.tts_init_failed), Toast.LENGTH_SHORT).show();
            ReadAloudService.stop(service);
        }

        //
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "content");
        for (int i = nowSpeak; i < contentList.size(); i++) {
            if (i == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(contentList.get(i), TextToSpeech.QUEUE_FLUSH, null, "content");
                } else {
                    tts.speak(contentList.get(i), TextToSpeech.QUEUE_FLUSH, map);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(contentList.get(i), TextToSpeech.QUEUE_ADD, null, "content");
                } else {
                    tts.speak(contentList.get(i), TextToSpeech.QUEUE_ADD, map);
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.CHINA);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
                langOk = true;
        }
    }

    private void toTTSSetting() {
        //跳转到文字转语音设置界面
        try {
            Intent intent = new Intent();
            intent.setAction("com.android.settings.TTS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MApplication.getInstance().startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}