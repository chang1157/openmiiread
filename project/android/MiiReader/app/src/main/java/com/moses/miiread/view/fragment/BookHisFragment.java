package com.moses.miiread.view.fragment;

import com.kunfei.basemvplib.impl.IPresenter;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseFragment;

public class BookHisFragment extends MBaseFragment {
    @Override
    public int createLayoutId() {
        return R.layout.fragment_book_his;
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }
}
