package com.moses.miiread.help;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.observer.SimpleObserver;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.ReplaceRuleBean;
import com.moses.miiread.bean.SearchHistoryBean;
import com.moses.miiread.dao.DbHelper;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.model.ReplaceRuleManager;
import com.moses.miiread.utils.FileUtils;
import com.moses.miiread.utils.PrefUtil;
import com.moses.miiread.utils.XmlUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by origin on 2018/1/30.
 * 数据恢复
 */

public class DataRestore {

    public static DataRestore getInstance() {
        return new DataRestore();
    }

    public Boolean run() throws Exception {
        String dirPath = FileUtils.getSdCardPath() + "/" + MApplication.getInstance().getString(R.string.local_folder_name);
        restoreConfig(dirPath);
        restoreBookSource(dirPath);
        restoreBookShelf(dirPath);
        restoreSearchHistory(dirPath);
        restoreReplaceRule(dirPath);
        return true;
    }

    public void restoreBookSourceOnly(){
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            String dirPath = FileUtils.getSdCardPath() + "/" + MApplication.getInstance().getString(R.string.local_folder_name);
            restoreBookSource(dirPath);
            e.onNext(true);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void restoreConfig(String dirPath) {
        Map<String, ?> entries = null;
        try (FileInputStream ins = new FileInputStream(dirPath + "/config.xml")) {
            entries = XmlUtils.readMapXml(ins);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entries == null || entries.isEmpty()) return;
        SharedPreferences.Editor editor = MApplication.getConfigPreferences().edit();
        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            Object v = entry.getValue();
            String key = entry.getKey();
            String type = v.getClass().getSimpleName();

            if ("Integer".equals(type)) {
                editor.putInt(key, (Integer) v);
            } else if ("Boolean".equals(type)) {
                editor.putBoolean(key, (Boolean) v);
            } else if ("String".equals(type)) {
                editor.putString(key, (String) v);
            } else if ("Float".equals(type)) {
                editor.putFloat(key, (Float) v);
            } else if ("Long".equals(type)) {
                editor.putLong(key, (Long) v);
            }
        }
        editor.putInt("versionCode", MApplication.getVersionCode());
        editor.apply();
        MApplication.getInstance().upThemeStore();
    }

    private void restoreBookShelf(String file) throws Exception {
        String json = DocumentHelper.readString("myBookShelf.json", file);
        if (json != null) {
            List<BookShelfBean> bookShelfList = new Gson().fromJson(json, new TypeToken<List<BookShelfBean>>() {
            }.getType());
            for (BookShelfBean bookshelf : bookShelfList) {
                if (bookshelf.getNoteUrl() != null) {
                    DbHelper.getDaoSession().getBookShelfBeanDao().insertOrReplace(bookshelf);
                }
                if (bookshelf.getBookInfoBean().getNoteUrl() != null) {
                    DbHelper.getDaoSession().getBookInfoBeanDao().insertOrReplace(bookshelf.getBookInfoBean());
                }
            }
        }
    }

    private void restoreBookSource(String file) throws Exception {
        boolean editSourceEnable = PrefUtil.getInstance().getBoolean("isEditSourceEnable", false);
        if(!editSourceEnable)
            return;
        String json = DocumentHelper.readString("myBookSource.json", file);
        if (json != null) {
            List<BookSourceBean> bookSourceBeans = new Gson().fromJson(json, new TypeToken<List<BookSourceBean>>() {
            }.getType());
            BookSourceManager.addBookSource(bookSourceBeans);
        }
    }

    private void restoreSearchHistory(String file) throws Exception {
        String json = DocumentHelper.readString("myBookSearchHistory.json", file);
        if (json != null) {
            List<SearchHistoryBean> searchHistoryBeans = new Gson().fromJson(json, new TypeToken<List<SearchHistoryBean>>() {
            }.getType());
            if (searchHistoryBeans != null && searchHistoryBeans.size() > 0) {
                DbHelper.getDaoSession().getSearchHistoryBeanDao().insertOrReplaceInTx(searchHistoryBeans);
            }
        }
    }

    private void restoreReplaceRule(String file) throws Exception {
        String json = DocumentHelper.readString("myBookReplaceRule.json", file);
        if (json != null) {
            List<ReplaceRuleBean> replaceRuleBeans = new Gson().fromJson(json, new TypeToken<List<ReplaceRuleBean>>() {
            }.getType());
            ReplaceRuleManager.addDataS(replaceRuleBeans);
        }
    }
}
