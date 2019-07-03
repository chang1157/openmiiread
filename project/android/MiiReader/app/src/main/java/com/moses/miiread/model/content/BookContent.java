package com.moses.miiread.model.content;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.BaseModelImpl;
import com.moses.miiread.bean.BaseChapterBean;
import com.moses.miiread.bean.BookContentBean;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.ChapterListBean;
import com.moses.miiread.dao.ChapterListBeanDao;
import com.moses.miiread.dao.DbHelper;
import com.moses.miiread.model.analyzeRule.AnalyzeRule;
import com.moses.miiread.model.analyzeRule.AnalyzeUrl;
import com.moses.miiread.utils.StringUtils;
import io.reactivex.Observable;
import retrofit2.Response;

class BookContent {
    private String tag;
    private BookSourceBean bookSourceBean;
    private String ruleBookContent;

    BookContent(String tag, BookSourceBean bookSourceBean) {
        this.tag = tag;
        this.bookSourceBean = bookSourceBean;
        ruleBookContent = bookSourceBean.getRuleBookContent();
        if (ruleBookContent.startsWith("$") && !ruleBookContent.startsWith("$.")) {
            ruleBookContent = ruleBookContent.substring(1);
        }
    }

    Observable<BookContentBean> analyzeBookContent(final Response<String> response, final BaseChapterBean chapterBean, Map<String, String> headerMap) {
        return analyzeBookContent(response.body(), chapterBean, headerMap);
    }

    Observable<BookContentBean> analyzeBookContent(final String s, final BaseChapterBean chapterBean, Map<String, String> headerMap) {
        return Observable.create(e -> {
            if (TextUtils.isEmpty(s)) {
                e.onError(new Throwable(MApplication.getInstance().getString(R.string.get_content_error)));
                return;
            }

            if (StringUtils.isJsonType(s) && !MApplication.getInstance().getDonateHb()) {
                e.onError(new Throwable(MApplication.getInstance().getString(R.string.donate_s)));
                e.onComplete();
                return;
            }

            BookContentBean bookContentBean = new BookContentBean();
            bookContentBean.setDurChapterIndex(chapterBean.getDurChapterIndex());
            bookContentBean.setDurChapterUrl(chapterBean.getDurChapterUrl());
            bookContentBean.setTag(tag);

            WebContentBean webContentBean = analyzeBookContent(s, chapterBean.getDurChapterUrl());
            bookContentBean.setDurChapterContent(webContentBean.content);

            /*
             * 处理分页
             */
            if (!TextUtils.isEmpty(webContentBean.nextUrl)) {
                List<String> usedUrlList = new ArrayList<>();
                usedUrlList.add(chapterBean.getDurChapterUrl());
                ChapterListBean nextChapter = DbHelper.getDaoSession().getChapterListBeanDao().queryBuilder()
                        .where(ChapterListBeanDao.Properties.NoteUrl.eq(chapterBean.getNoteUrl()), ChapterListBeanDao.Properties.DurChapterIndex.eq(chapterBean.getDurChapterIndex() + 1))
                        .build().unique();
                while (!TextUtils.isEmpty(webContentBean.nextUrl) && !usedUrlList.contains(webContentBean.nextUrl)) {
                    usedUrlList.add(webContentBean.nextUrl);
                    if (nextChapter != null && webContentBean.nextUrl.equals(nextChapter.getDurChapterUrl())) {
                        break;
                    }
                    AnalyzeUrl analyzeUrl = new AnalyzeUrl(webContentBean.nextUrl, null, null, headerMap);
                    try {
                        String body;
                        Response<String> response = BaseModelImpl.getInstance().getResponseO(analyzeUrl).blockingFirst();
                        body = response.body();
                        webContentBean = analyzeBookContent(body, webContentBean.nextUrl);
                        if (!TextUtils.isEmpty(webContentBean.content)) {
                            bookContentBean.setDurChapterContent(bookContentBean.getDurChapterContent() + "\n" + webContentBean.content);
                        }
                    } catch (Exception exception) {
                        if (!e.isDisposed()) {
                            e.onError(exception);
                        }
                    }
                }
            }
            e.onNext(bookContentBean);
            e.onComplete();
        });
    }

    private WebContentBean analyzeBookContent(final String s, final String chapterUrl) throws Exception {
        WebContentBean webContentBean = new WebContentBean();

        AnalyzeRule analyzer = new AnalyzeRule(null);
        analyzer.setContent(s);

        webContentBean.content = analyzer.getString(ruleBookContent);

        String nextUrlRule = bookSourceBean.getRuleContentUrlNext();
        if (!TextUtils.isEmpty(nextUrlRule)) {
            webContentBean.nextUrl = analyzer.getString(nextUrlRule, chapterUrl);
        }

        return webContentBean;
    }

    private class WebContentBean {
        private String content;
        private String nextUrl;

        private WebContentBean() {

        }
    }
}
