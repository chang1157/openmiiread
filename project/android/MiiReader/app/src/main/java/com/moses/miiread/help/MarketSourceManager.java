package com.moses.miiread.help;

import android.app.Activity;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moses.miiread.BuildConfig;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.BaseModelImpl;
import com.moses.miiread.base.observer.SimpleObserver;
import com.moses.miiread.bean.UpdateInfoBean;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.analyzeRule.AnalyzeHeaders;
import com.moses.miiread.model.impl.IHttpGetApi;
import com.moses.miiread.utils.PrefUtil;
import com.moses.miiread.view.activity.UpdateActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class MarketSourceManager {
    private Activity activity;

    public static MarketSourceManager getInstance(Activity activity) {
        return new MarketSourceManager(activity);
    }

    private MarketSourceManager(Activity activity) {
        this.activity = activity;
    }

    private void checkMarketSources() {
        BaseModelImpl.getInstance().getRetrofitString((BuildConfig.DEBUG ? ConfigMng.OSS_DOMAIN_DBG : ConfigMng.OSS_DOMAIN_RLS) + "." + MApplication.getInstance().getString(R.string.latest_release_api_subfix))
                .create(IHttpGetApi.class)
                .getWebContent((BuildConfig.DEBUG ? ConfigMng.OSS_DOMAIN_DBG : ConfigMng.OSS_DOMAIN_RLS) + "." + MApplication.getInstance().getString(R.string.market_source_url), AnalyzeHeaders.getMap(null))
                .flatMap(response -> analyzeMarketSources(response.body()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<String>>() {
                    @Override
                    public void onNext(List<String> strings) {
                        if (strings.size() > 0) {
                            ConfigMng.useAsMarketSourceUrlForEditedMode.clear();
                            ConfigMng.useAsMarketSourceUrlForEditedMode.addAll(strings);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private Observable<List<String>> analyzeMarketSources(String jsonStr) {
        return Observable.create(emitter -> {
            try {
                List<String> marketSources = new ArrayList<>();
                JsonObject obj = new JsonParser().parse(jsonStr).getAsJsonObject();
                JsonArray array = obj.getAsJsonArray("data");
                for (int i = 0; i < array.size(); i++) {
                    String source = array.get(i).getAsString();
                    marketSources.add(source);
                }
                if (marketSources.size() > 0)
                    PrefUtil.getInstance().setString("useAsMarketSourceUrlForEditedMode", jsonStr);
                emitter.onNext(marketSources);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
                emitter.onComplete();
            }
        });
    }

    public void initMarketSources() {
        PrefUtil prefUtil = PrefUtil.getInstance();
        String jsonS = prefUtil.getString("useAsMarketSourceUrlForEditedMode", null);
        if (jsonS != null) {
            try {
                ConfigMng.useAsMarketSourceUrlForEditedMode.clear();
                JsonObject obj = new JsonParser().parse(jsonS).getAsJsonObject();
                JsonArray array = obj.getAsJsonArray("data");
                for (int i = 0; i < array.size(); i++) {
                    String source = array.get(i).getAsString();
                    ConfigMng.useAsMarketSourceUrlForEditedMode.add(source);
                }
            } catch (Exception ignore) {

            }
        }
        checkMarketSources();
    }
}