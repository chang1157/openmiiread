package com.moses.miiread.presenter.contract;

import android.app.Activity;

import com.kunfei.basemvplib.impl.IPresenter;
import com.kunfei.basemvplib.impl.IView;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.BookmarkBean;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.presenter.ReadBookPresenter;
import com.moses.miiread.service.ReadAloudService;

public interface ReadBookContract {
    interface View extends IView {

        String getNoteUrl();

        Boolean getAdd();

        void setAdd(Boolean isAdd);

        void changeSourceFinish(BookShelfBean book);

        void startLoadingBook();

        void upMenu();

        void openBookFromOther();

        void showBookmark(BookmarkBean bookmarkBean);

        void skipToChapter(int chapterIndex, int pageIndex);

        void onMediaButton();

        void upAloudState(ReadAloudService.Status state);

        void upAloudTimer(String timer);

        void readAloudStart(int start);

        void readAloudLength(int readAloudLength);

        void refresh(boolean recreate);

        void upBar();

        void keepScreenOnChange(int keepScreenOn);

        void readRecreate();

        void refreshPage();

        void finish();

        void updateReadStyleDlgTone();

        void bgChange();

        void refreshOriginTxt();

    }

    interface Presenter extends IPresenter {

        int getOpen_from();

        BookShelfBean getBookShelf();

        void saveProgress();

        void addToShelf(final ReadBookPresenter.OnAddListener Listener);

        void removeFromShelf();

        void initData(Activity activity);

        void openBookFromOther(Activity activity);

        void addDownload(int start, int end);

        void changeBookSource(SearchBookBean searchBookBean);

        void saveBookmark(BookmarkBean bookmarkBean);

        void delBookmark(BookmarkBean bookmarkBean);

        void disableDurBookSource();

        BookSourceBean getBookSource();
    }
}
