package com.moses.miiread.service.tts;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import com.moses.miiread.service.ReadAloudService;

import java.util.List;
import java.util.Objects;

public abstract class TtsProviderFactory {

    public abstract void say(int nowSpeak, List<String> contentList);

    public abstract void init(Context context);

    public abstract TextToSpeech getTts();

    public abstract void setTts(TextToSpeech tts);

    private static TtsProviderFactory sInstance;

    public abstract void bindReadAloudService(ReadAloudService service);

    public static TtsProviderFactory getInstance() {
        if (sInstance == null) {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion < Build.VERSION_CODES.DONUT) {
                return null;
            }
            try {
                String className = "TtsProviderImpl";
                Class<? extends TtsProviderFactory> clazz =
                        Class.forName(Objects.requireNonNull(TtsProviderFactory.class.getPackage()).getName() + "." + className)
                                .asSubclass(TtsProviderFactory.class);
                sInstance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return sInstance;
    }
}