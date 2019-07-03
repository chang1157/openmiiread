package com.moses.miiread.model.analyzeRule;

import android.content.SharedPreferences;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.CookieBean;
import com.moses.miiread.dao.DbHelper;

import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by origin on 2018/3/2.
 * 解析Headers
 */

public class AnalyzeHeaders {
    private static SharedPreferences preferences = MApplication.getConfigPreferences();

    public static Map<String, String> getMap(BookSourceBean bookSourceBean) {
        Map<String, String> headerMap = new HashMap<>();
        if (bookSourceBean != null && !isEmpty(bookSourceBean.getHttpUserAgent())) {
            headerMap.put("User-Agent", bookSourceBean.getHttpUserAgent());
        } else {
            headerMap.put("User-Agent", getDefaultUserAgent());
        }
        if (bookSourceBean != null) {
            CookieBean cookie = DbHelper.getDaoSession().getCookieBeanDao().load(bookSourceBean.getBookSourceUrl());
            if (cookie != null) {
                headerMap.put("Cookie", cookie.getCookie());
            }
        }
        return headerMap;
    }

    public static String getUserAgent(String userAgent) {
        if (isEmpty(userAgent)) {
            return getDefaultUserAgent();
        } else {
            return userAgent;
        }
    }

    private static String getDefaultUserAgent() {
        return preferences.getString(MApplication.getInstance().getString(R.string.pk_user_agent),
                MApplication.getInstance().getString(R.string.pv_user_agent));
    }
}
