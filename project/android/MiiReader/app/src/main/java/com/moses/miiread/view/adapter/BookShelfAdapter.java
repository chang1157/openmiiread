package com.moses.miiread.view.adapter;

import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.help.ItemTouchCallback;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

import java.util.List;

public interface BookShelfAdapter {
    ItemTouchCallback.OnItemTouchCallbackListener getItemTouchCallbackListener();

    List<BookShelfBean> getBooks();

    void setBooks(List<BookShelfBean> books);

    void replaceAll(List<BookShelfBean> newDataS, String bookshelfPx);

    void refreshBook(String noteUrl);

    void setItemClickListener(OnItemClickListenerTwo itemClickListener);
}
