package com.moses.miiread.presenter.contract;

import com.kunfei.basemvplib.impl.IPresenter;
import com.kunfei.basemvplib.impl.IView;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.List;

public interface FindBookContractMoses {
    interface Presenter extends IPresenter {

        void initData();

    }

    interface View<G, C> extends IView {

        void upStyle();

        void updateUI(List<RecyclerViewData<G, C>> group);

    }
}
