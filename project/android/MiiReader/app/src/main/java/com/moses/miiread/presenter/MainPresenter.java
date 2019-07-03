//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.android.material.snackbar.Snackbar;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kunfei.basemvplib.BasePresenterImpl;
import com.kunfei.basemvplib.impl.IView;

import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import com.moses.miiread.R;
import com.moses.miiread.base.observer.SimpleObserver;
import com.moses.miiread.bean.BookInfoBean;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.dao.BookInfoBeanDao;
import com.moses.miiread.dao.DbHelper;
import com.moses.miiread.help.BookshelfHelp;
import com.moses.miiread.help.DataBackup;
import com.moses.miiread.help.DataRestore;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.model.WebBookModel;
import com.moses.miiread.presenter.contract.MainContract;
import com.moses.miiread.utils.RxUtils;
import com.moses.miiread.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void backupData() {
        DataBackup.getInstance().run();
    }

    @Override
    public void restoreData() {
        mView.onRestore(mView.getContext().getString(R.string.on_restore));
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            if (DataRestore.getInstance().run()) {
                e.onNext(true);
            } else {
                e.onNext(false);
            }
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        mView.dismissHUD();
                        if (value) {
                            //更新书架并刷新
                            mView.toast(R.string.restore_success);
                            mView.recreate();
                            ReadBookControl.getInstance().updateReaderSettings();
                        } else {
                            mView.toast(R.string.restore_fail);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissHUD();
                        mView.toast(R.string.restore_fail);
                    }
                });
    }

    @Override
    public void addBookUrl(String bookUrls) {
        bookUrls = bookUrls.trim();
        if (TextUtils.isEmpty(bookUrls)) return;

        String[] urls = bookUrls.split("\\n");

        Observable.fromArray(urls)
                .flatMap(this::addBookUrlO)
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SimpleObserver<BookShelfBean>() {
                    @Override
                    public void onNext(BookShelfBean bookShelfBean) {
                        if (bookShelfBean != null) {
                            getBook(bookShelfBean);
                        } else {
                            mView.toast("已在书架中");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast("网址格式不对");
                    }
                });
    }

    private Observable<BookShelfBean> addBookUrlO(String bookUrl) {
        return Observable.create(e -> {
            if (StringUtils.isTrimEmpty(bookUrl)) {
                e.onComplete();
                return;
            }
            URL url = new URL(bookUrl);
            BookInfoBean temp = DbHelper.getDaoSession().getBookInfoBeanDao().queryBuilder()
                    .where(BookInfoBeanDao.Properties.NoteUrl.eq(bookUrl)).limit(1).build().unique();
            if (temp != null) {
                e.onNext(null);
            } else {
                BookShelfBean bookShelfBean = new BookShelfBean();
                bookShelfBean.setTag(String.format("%s://%s", url.getProtocol(), url.getHost()));
                bookShelfBean.setNoteUrl(url.toString());
                bookShelfBean.setDurChapter(0);
                bookShelfBean.setGroup(mView.getGroup() % 4);
                bookShelfBean.setDurChapterPage(0);
                bookShelfBean.setFinalDate(System.currentTimeMillis());
                e.onNext(bookShelfBean);
            }
            e.onComplete();
        });
    }

    @Override
    public void clearBookshelf() {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            BookshelfHelp.clearBookshelf();
            e.onNext(true);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        RxBus.get().post(RxBusTag.REFRESH_BOOK_LIST, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast(e.getMessage());
                    }
                });
    }

    private void getBook(BookShelfBean bookShelfBean) {
        WebBookModel.getInstance()
                .getBookInfo(bookShelfBean)
                .flatMap(bookShelfBean1 -> WebBookModel.getInstance().getChapterList(bookShelfBean1))
                .flatMap(this::saveBookToShelfO)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<BookShelfBean>() {
                    @Override
                    public void onNext(BookShelfBean value) {
                        if (value.getBookInfoBean().getChapterUrl() == null) {
                            mView.toast("添加书籍失败");
                        } else {
                            //成功   //发送RxBus
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelfBean);
                            mView.toast("添加书籍成功");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast("添加书籍失败" + e.getMessage());
                    }
                });
    }

    @Override
    public void importBookSource(String url, boolean... snack) {
        if (snack[0])
            mView.showSnackBar("正在更新书源", Snackbar.LENGTH_INDEFINITE);
        Observable<List<BookSourceBean>> observable = BookSourceManager.importSource(url);
        if (observable != null) {
            observable.subscribe(getImportObserver(snack));
        } else {
            if (snack[0])
                mView.showSnackBar("格式不对", Snackbar.LENGTH_SHORT);
        }
    }

    private SimpleObserver<List<BookSourceBean>> getImportObserver(boolean... snack) {
        return new SimpleObserver<List<BookSourceBean>>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onNext(List<BookSourceBean> bookSourceBeans) {
                if (snack[0]) {
                    if (bookSourceBeans.size() > 0) {
                        int deleteC = 0;
                        for (BookSourceBean sourceBean : bookSourceBeans) {
                            if (sourceBean.containsGroup("删除"))
                                deleteC++;
                        }
                        mView.showSnackBar(String.format("更新了%d个书源", bookSourceBeans.size() - deleteC), Snackbar.LENGTH_SHORT);
                    } else {
                        mView.showSnackBar("格式不对", Snackbar.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (snack[0])
                    mView.showSnackBar(e.getMessage(), Snackbar.LENGTH_SHORT);
            }
        };
    }

    /**
     * 保存数据
     */
    private Observable<BookShelfBean> saveBookToShelfO(BookShelfBean bookShelfBean) {
        return Observable.create(e -> {
            BookshelfHelp.saveBookToShelf(bookShelfBean);
            e.onNext(bookShelfBean);
            e.onComplete();
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.IMMERSION_CHANGE)})
    public void initImmersionBar(Boolean immersion) {
        mView.initImmersionBar();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.RECREATE)})
    public void updatePx(Boolean px) {
        mView.recreate();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.AUTO_BACKUP)})
    public void autoBackup(Boolean backup) {
        DataBackup.getInstance().autoSave();
    }
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.RESTORE_BOOK_SOURCE_FROM_SERVER)})
    public void restoreBookSourceFromServer(Boolean restore) {
        importBookSource(ConfigMng.DEFAULT_SOURCE_URL, true);
    }
}