package com.moses.miiread.logic;

import com.moses.miiread.BuildConfig;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;

import java.util.ArrayList;
import java.util.List;

public class ConfigMng {

    public static final String DEFAULT_SOURCE_URL = (BuildConfig.DEBUG ? OSS_DOMAIN_DBG : OSS_DOMAIN_RLS) + "." + MApplication.getInstance().getString(R.string.resource_url);

    public static final boolean PORN_SOURCE_ENABLE = false;

    public static final boolean JUMP_TO_SOURCE_ENABLE = true;

    public static final boolean USE_MOSES_FIND_FRG = true;

    public static final int MAX_BOOK_MARKET_LIST_ITEM_COUNT = 15;
    public static final int MAX_BOOK_MARKET_LIST_PAGE_COUNT = 3;
    public static final int DEFAULT_THREAD_NUM = 20;

    public static final float NAVIGAITON_WIDTH_RATE_TO_CONENT_vertical = 0.78f;
    public static final float NAVIGAITON_WIDTH_RATE_TO_CONENT_horizontal = 0.5f;

    public static final boolean UPDATE_ENABLE = true;

    //用于非自更新源的书城管理
    public static List<String> useAsMarketSourceUrlForEditedMode = new ArrayList<>();

    static {
        //默认书城
        useAsMarketSourceUrlForEditedMode.add("https://m.zhaishuyuan.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.youdict.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.kygso.com");
        useAsMarketSourceUrlForEditedMode.add("https://m.677a.cn");
        useAsMarketSourceUrlForEditedMode.add("http://www.mbg8.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.pig87.com");
        useAsMarketSourceUrlForEditedMode.add("https://m.xiashu.cc");
        useAsMarketSourceUrlForEditedMode.add("http://zuopinj.com");
        useAsMarketSourceUrlForEditedMode.add("http://easou.com");
        useAsMarketSourceUrlForEditedMode.add("http://api.qingoo.cn:9090");
        useAsMarketSourceUrlForEditedMode.add("https://meiriyiwen.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.linovel.net");
        useAsMarketSourceUrlForEditedMode.add("http://api.17k.com");
        useAsMarketSourceUrlForEditedMode.add("http://api10e46r.zhuishushenqi.com");
        useAsMarketSourceUrlForEditedMode.add("https://fox2008.cn");
        useAsMarketSourceUrlForEditedMode.add("http://www.8kana.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.kenxingw.com");
        useAsMarketSourceUrlForEditedMode.add("https://api.zhuishushenqi.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.jjxs.la");
        useAsMarketSourceUrlForEditedMode.add("https://m.mianhuatang.co");
        useAsMarketSourceUrlForEditedMode.add("https://www.qidian.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.zhuishushenqi.com");
        useAsMarketSourceUrlForEditedMode.add("https://b.faloo.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.jsmw266.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.qijizuopin.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.shenpinwu.com");
        useAsMarketSourceUrlForEditedMode.add("http://m.itangyuan.com");
        useAsMarketSourceUrlForEditedMode.add("https://www.yuesehan.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.yousuu.com");
        useAsMarketSourceUrlForEditedMode.add("http://www.zongheng.com");
    }
}