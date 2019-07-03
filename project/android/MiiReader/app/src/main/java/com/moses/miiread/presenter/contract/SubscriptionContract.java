package com.moses.miiread.presenter.contract;

import com.kunfei.basemvplib.impl.IPresenter;
import com.kunfei.basemvplib.impl.IView;

public interface SubscriptionContract {

    interface View extends IView {

        void initImmersionBar();

        void dismissHUD();

        void toast(String msg);

        void toast(int strId);

    }

    interface Presenter extends IPresenter {

        /**
         * 获取商品列表
         */
        void doGetSubscritions();

        /**
         * 获取用户订阅信息
         */
        void doGetSubscritionsByUser(String uid);

        /**
         * 获取商品列表结果
         */
        void onGetSubscritionsResult(boolean ok);

        /**
         * 获取用户订阅信息结果
         */
        void onGetSubscrtitionsByUser(boolean ok);

        /**
         * 获取订阅页说明信息
         */
        void doGetSubcritionsDesc();

        /**
         * 获取说明信息结果
         */
        void onGetSubscrtionsDesc(boolean ok);

        /**
         * 订阅
         */
        void submmitOrder(String userId, int orderType);

        /**
         * 订阅结果
         */
        void onSubmmitOrder(boolean ok);
    }
}
