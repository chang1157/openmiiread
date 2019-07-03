package com.moses.miiread.presenter.contract;

import com.google.android.material.snackbar.Snackbar;
import com.kunfei.basemvplib.impl.IPresenter;
import com.kunfei.basemvplib.impl.IView;

public interface MainContract {

    interface View extends IView {

        void initImmersionBar();

        /**
         * 取消弹出框
         */
        void dismissHUD();

        /**
         * 恢复数据
         */
        void onRestore(String msg);

        void recreate();

        void toast(String msg);

        void toast(int strId);

        int getGroup();

        Snackbar getSnackBar(String msg, int length);

        void showSnackBar(String msg, int length);

        void fixDrawerBottom();

    }

    interface Presenter extends IPresenter {

        void backupData();

        void restoreData();

        void addBookUrl(String bookUrl);

        void clearBookshelf();

        void importBookSource(String url, boolean... snack);

    }

}
