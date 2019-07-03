//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.model;

import android.annotation.SuppressLint;

import android.util.Log;
import com.hwangjr.rxbus.RxBus;

import java.util.ArrayList;
import java.util.List;

import com.moses.miiread.bean.*;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.help.BookshelfHelp;
import com.moses.miiread.model.content.DefaultModel;
import com.moses.miiread.model.impl.IStationBookModel;
import io.reactivex.Observable;

public class WebBookModel {

    public static WebBookModel getInstance() {
        return new WebBookModel();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 网络请求并解析书籍信息
     * return BookShelfBean
     */
    public Observable<BookShelfBean> getBookInfo(BookShelfBean bookShelfBean) {
        IStationBookModel bookModel = getBookSourceModel(bookShelfBean.getTag());
        if (bookModel != null) {
            return bookModel.getBookInfo(bookShelfBean);
        } else {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 网络解析图书目录
     * return BookShelfBean
     */
    public Observable<BookShelfBean> getChapterList(final BookShelfBean bookShelfBean) {
        IStationBookModel bookModel = getBookSourceModel(bookShelfBean.getTag());
        if (bookModel != null) {
            return bookModel.getChapterList(bookShelfBean)
                    .flatMap((chapterList) -> upChapterList(bookShelfBean, chapterList));
        } else {
            return Observable.error(new Throwable(bookShelfBean.getBookInfoBean().getName() + "没有书源"));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 章节缓存
     */
    public Observable<BookContentBean> getBookContent(BaseChapterBean chapterBean, String bookName) {
        IStationBookModel bookModel = getBookSourceModel(chapterBean.getTag());
        if (bookModel != null) {
            return bookModel.getBookContent(chapterBean)
                    .flatMap((bookContentBean -> saveContent(bookName, chapterBean, bookContentBean)));
        } else
            return Observable.create(e -> {
                e.onError(new Throwable("没有找到书源"));
                e.onComplete();
            });
    }

    /**
     * 其他站点集合搜索
     */
    public Observable<List<SearchBookBean>> searchOtherBook(String content, int page, String tag) {
        //获取所有书源类
        IStationBookModel bookModel = getBookSourceModel(tag);
        if (bookModel != null) {
            return bookModel.searchBook(content, page);
        } else {
            return Observable.create(e -> {
                e.onNext(new ArrayList<>());
                e.onComplete();
            });
        }
    }

    /**
     * 发现页
     */
    public Observable<List<SearchBookBean>> findBook(String url, int page, String tag) {
        IStationBookModel bookModel = getBookSourceModel(tag);
        if (bookModel != null) {
            return bookModel.findBook(url, page);
        } else {
            return Observable.create(e -> {
                e.onNext(new ArrayList<>());
                e.onComplete();
            });
        }
    }

    //获取book source class
    @SuppressLint("LogNotTimber")
    private IStationBookModel getBookSourceModel(String tag) {
        try {
            if (BookShelfBean.LOCAL_TAG.equals(tag))
                return null;
            return DefaultModel.getInstance(tag);
        } catch (Exception e) {
            Log.e("TAG", "tag = " + tag + ":: " + e.getMessage());
        }
        return null;
    }

    private Observable<BookShelfBean> upChapterList(BookShelfBean bookShelfBean, List<ChapterListBean> chapterList) {
        return Observable.create(e -> {
            for (int i = 0; i < chapterList.size(); i++) {
                ChapterListBean chapter = chapterList.get(i);
                chapter.setDurChapterIndex(i);
                chapter.setTag(bookShelfBean.getTag());
                chapter.setNoteUrl(bookShelfBean.getNoteUrl());
            }
            if (bookShelfBean.getChapterListSize() < chapterList.size()) {
                bookShelfBean.setHasUpdate(true);
                bookShelfBean.setFinalRefreshData(System.currentTimeMillis());
                bookShelfBean.getBookInfoBean().setFinalRefreshData(System.currentTimeMillis());
            }
            if (!chapterList.isEmpty()) {
                bookShelfBean.setChapterListSize(chapterList.size());
                bookShelfBean.setDurChapter(Math.min(bookShelfBean.getDurChapter(), bookShelfBean.getChapterListSize() - 1));
                bookShelfBean.getBookInfoBean().setChapterList(chapterList);
                bookShelfBean.upDurChapterName();
                bookShelfBean.upLastChapterName();
                BookshelfHelp.delChapterList(bookShelfBean.getNoteUrl());
            }
            e.onNext(bookShelfBean);
            e.onComplete();
        });
    }

    @SuppressLint("DefaultLocale")
    private Observable<BookContentBean> saveContent(String bookName, BaseChapterBean chapterBean, BookContentBean bookContentBean) {
        return Observable.create(e -> {
            bookContentBean.setNoteUrl(chapterBean.getNoteUrl());
            if (bookContentBean.getDurChapterContent() == null) {
                e.onError(new Throwable("下载章节出错"));
            } else if (BookshelfHelp.saveChapterInfo(bookName + "-" + chapterBean.getTag(), chapterBean.getDurChapterIndex(),
                    chapterBean.getDurChapterName(), bookContentBean.getDurChapterContent())) {
                RxBus.get().post(RxBusTag.CHAPTER_CHANGE, chapterBean);
                e.onNext(bookContentBean);
            } else {
                e.onError(new Throwable("保存章节出错"));
            }
            e.onComplete();
        });
    }
}
