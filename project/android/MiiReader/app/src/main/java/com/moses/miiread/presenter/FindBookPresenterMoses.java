//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.presenter;

import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kunfei.basemvplib.BasePresenterImpl;
import com.kunfei.basemvplib.impl.IView;
import com.moses.miiread.MApplication;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.FindKindBean;
import com.moses.miiread.bean.FindKindGroupBean;
import com.moses.miiread.constant.RxBusTag;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.presenter.contract.FindBookContractMoses;
import com.moses.miiread.utils.RxUtils;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.List;

public class FindBookPresenterMoses extends BasePresenterImpl<FindBookContractMoses.View> implements FindBookContractMoses.Presenter {
    private Disposable disposable;

    @SuppressWarnings("unchecked")
    @Override
    public void initData() {
        if (disposable != null) return;
        Single.create((SingleOnSubscribe<List<RecyclerViewData>>) e -> {
            List<RecyclerViewData> group = new ArrayList<>();
            boolean showAllFind = MApplication.getConfigPreferences().getBoolean("showAllFind", true);
            List<BookSourceBean> sourceBeans = new ArrayList<>(
                    showAllFind ? BookSourceManager.getAllBookSourceBySerialNumber()
                            : BookSourceManager.getSelectedBookSourceBySerialNumber());
            for (BookSourceBean sourceBean : sourceBeans) {
                if ((ConfigMng.PORN_SOURCE_ENABLE || !sourceBean.containsGroup("辣文")) && !sourceBean.containsGroup("删除") && !sourceBean.containsGroup("失效")) {
                    if (ConfigMng.useAsMarketSourceUrlForEditedMode.contains(sourceBean.getBookSourceUrl())) {
                        try {
                            if (!TextUtils.isEmpty(sourceBean.getRuleFindUrl())) {
                                String[] kindA = sourceBean.getRuleFindUrl().split("(&&|\n)+");
                                List<FindKindBean> children = new ArrayList<>();
                                for (int i = 0; i < kindA.length; i++) {
                                    if (kindA.length > 1) {
                                        //如果只有一个类型，第一个类型作为书库展示； 如果多余1个类型， 则使用第二个类型
                                        if (i == 0)
                                            continue;
                                        if (i > 1)
                                            break;
                                    }
                                    String kindB = kindA[i];
                                    if (kindB.trim().isEmpty()) continue;
                                    String[] kind = kindB.split("::");
                                    FindKindBean findKindBean = new FindKindBean();
                                    findKindBean.setGroup(sourceBean.getBookSourceName());
                                    findKindBean.setTag(sourceBean.getBookSourceUrl());
                                    findKindBean.setKindName(kind[0]);
                                    findKindBean.setKindUrl(kind[1]);
                                    children.add(findKindBean);
                                    break;
                                }
                                FindKindGroupBean groupBean = new FindKindGroupBean();
                                groupBean.setGroupName(sourceBean.getBookSourceName());
                                groupBean.setGroupTag(sourceBean.getBookSourceUrl());
                                group.add(new RecyclerViewData(groupBean, children, true));
                            }
                        } catch (Exception exception) {
                            sourceBean.addGroup("发现规则语法错误");
                            BookSourceManager.addBookSource(sourceBean);
                        }
                    }
                }
            }
            e.onSuccess(group);
        })
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<List<RecyclerViewData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<RecyclerViewData> recyclerViewData) {
                        mView.updateUI(recyclerViewData);
                        disposable.dispose();
                        disposable = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        disposable.dispose();
                        disposable = null;
                    }
                });
    }

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.UPDATE_BOOK_SOURCE)})
    public void hadAddOrRemoveBook(Object object) {
        initData();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.UP_FIND_STYLE)})
    public void upFindStyle(Object object) {
        mView.upStyle();
        initData();
    }
}