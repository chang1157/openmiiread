//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.model.impl;

import java.util.List;

import com.moses.miiread.bean.*;
import io.reactivex.Observable;

public interface IStationBookModel {

    /**
     * 发现书籍
     */
    Observable<List<SearchBookBean>> findBook(String url, int page);

    /**
     * 搜索书籍
     */
    Observable<List<SearchBookBean>> searchBook(String content, int page);

    /**
     * 网络请求并解析书籍信息
     */
    Observable<BookShelfBean> getBookInfo(final BookShelfBean bookShelfBean);

    /**
     * 网络解析图书目录
     */
    Observable<List<ChapterListBean>> getChapterList(final BookShelfBean bookShelfBean);


    /**
     * 获取章节
     */
    Observable<BookContentBean> getBookContent(final BaseChapterBean chapterBean);


}
