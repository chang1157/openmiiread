package com.moses.miiread.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.moses.miiread.BitIntentDataManager;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseFragment;
import com.moses.miiread.bean.BookShelfBean;
import com.moses.miiread.help.ItemTouchCallback;
import com.moses.miiread.presenter.BookDetailPresenter;
import com.moses.miiread.presenter.BookListPresenter;
import com.moses.miiread.presenter.ReadBookPresenter;
import com.moses.miiread.presenter.contract.BookListContract;
import com.moses.miiread.utils.NetworkUtil;
import com.moses.miiread.utils.ScreenUtils;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.activity.BookDetailActivity;
import com.moses.miiread.view.activity.ReadBookActivity;
import com.moses.miiread.view.adapter.BookShelfAdapter;
import com.moses.miiread.view.adapter.BookShelfGridAdapter;
import com.moses.miiread.view.adapter.BookShelfListAdapter;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

import java.util.List;
import java.util.Objects;

public class BookListFragment extends MBaseFragment<BookListContract.Presenter> implements BookListContract.View {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.local_book_rv_content)
    RecyclerView rvBookshelf;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.rl_empty_view)
    RelativeLayout rlEmptyView;
    @BindView(R.id.empty_img)
    ImageView emptyImg;

    private Unbinder unbinder;
    private String bookPx;
    private boolean resumed = false;
    private boolean isRecreate;
    private int group;

    private BookShelfAdapter bookShelfAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int createLayoutId() {
        return R.layout.fragment_book_list;
    }

    @Override
    protected BookListContract.Presenter initInjector() {
        return new BookListPresenter();
    }

    @Override
    protected void initData() {
        CallBackValue callBackValue = (CallBackValue) getActivity();
        bookPx = preferences.getString(getString(R.string.pk_book_px), "0");
        isRecreate = callBackValue != null && callBackValue.isRecreate();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (rvBookshelf.getAdapter() instanceof BookShelfGridAdapter) {
            setFixedGridAdapter(false, newConfig);
        }
    }

    private void setFixedGridAdapter(boolean newInstance, Configuration newConfig) {
        ViewTreeObserver vto = rvBookshelf.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getContext() == null)
                    return;
                rvBookshelf.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] wh = ScreenUtils.getScreenSize(getContext());
                int bigC = 7;
                int smallC = 3;
                int rowCount = newInstance
                        ? (wh[0] < wh[1] ? smallC : bigC)
                        : (newConfig != null && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? bigC : smallC);
                if (bookShelfAdapter != null && bookShelfAdapter instanceof BookShelfGridAdapter) {
                    BookShelfGridAdapter adapter = (BookShelfGridAdapter) bookShelfAdapter;
                    if (rowCount == 3 && !adapter.getIsVertical()) {
                        adapter.setIsVertical(true);
                        adapter.notifyDataSetChanged();
                    } else if (rowCount == 7 && adapter.getIsVertical()) {
                        adapter.setIsVertical(false);
                        adapter.notifyDataSetChanged();
                    }
                }
                rvBookshelf.setLayoutManager(new GridLayoutManager(getContext(), rowCount));
            }
        });
        rvBookshelf.requestLayout();
        if (newInstance) {
            bookShelfAdapter = new BookShelfGridAdapter(getActivity());
            rvBookshelf.setAdapter((RecyclerView.Adapter) bookShelfAdapter);
            bookShelfAdapter.setItemClickListener(getAdapterListener());
            if (itemTouchCallback != null)
                itemTouchCallback.setOnItemTouchCallbackListener(bookShelfAdapter.getItemTouchCallbackListener());
        }
    }

    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        if (preferences.getBoolean("bookshelfIsList", true)) {
            rvBookshelf.setLayoutManager(new LinearLayoutManager(getContext()));
            bookShelfAdapter = new BookShelfListAdapter(getActivity());
            rvBookshelf.setAdapter((RecyclerView.Adapter) bookShelfAdapter);
        } else {
            setFixedGridAdapter(true, null);
        }
        refreshLayout.setColorSchemeColors(ThemeStore.accentColor(MApplication.getInstance()));
    }

    @Override
    protected void firstRequest() {
        group = preferences.getInt("bookshelfGroup", 0);
        if (preferences.getBoolean(getString(R.string.pk_auto_refresh), false)
                && !isRecreate && NetworkUtil.isNetWorkAvailable() && group != 2) {
            mPresenter.queryBookShelf(true, group);
        } else {
            mPresenter.queryBookShelf(false, group);
        }
    }

    @Override
    protected void bindEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            mPresenter.queryBookShelf(NetworkUtil.isNetWorkAvailable(), group);
            if (!NetworkUtil.isNetWorkAvailable()) {
                Toast.makeText(getContext(), R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
            }
            refreshLayout.setRefreshing(false);
        });
        itemTouchCallback = new ItemTouchCallback();
        itemTouchCallback.setSwipeRefreshLayout(refreshLayout);
        if (bookPx.equals("2")) {
            itemTouchCallback.setDragEnable(true);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        } else {
            itemTouchCallback.setDragEnable(false);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        }
        bookShelfAdapter.setItemClickListener(getAdapterListener());
        itemTouchCallback.setOnItemTouchCallbackListener(bookShelfAdapter.getItemTouchCallbackListener());

        //update shelf on fragment fist created
        if (!resumed && NetworkUtil.isNetWorkAvailable()) {
            mPresenter.queryBookShelf(NetworkUtil.isNetWorkAvailable(), group);
        }
    }

    private OnItemClickListenerTwo getAdapterListener() {
        return new OnItemClickListenerTwo() {
            @Override
            public void onClick(View view, int index) {
                BookShelfBean bookShelfBean = bookShelfAdapter.getBooks().get(index);
                if (view.getId() == R.id.item_menu) {
                    openBookDetailMenuBottomSheet(index);
                } else {
                    Intent intent = new Intent(getActivity(), ReadBookActivity.class);
                    intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
                    String key = String.valueOf(System.currentTimeMillis());
                    intent.putExtra("data_key", key);
                    try {
                        BitIntentDataManager.getInstance().putData(key, bookShelfBean.clone());
                    } catch (CloneNotSupportedException e) {
                        BitIntentDataManager.getInstance().putData(key, bookShelfBean);
                        e.printStackTrace();
                    }
                    startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onLongClick(View view, int index) {
                openBookDetailMenuBottomSheet(index);
            }

            private void openBookDetailMenuBottomSheet(int index) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("openFrom", BookDetailPresenter.FROM_BOOKSHELF);
                String key = String.valueOf(System.currentTimeMillis());
                intent.putExtra("data_key", key);
                BitIntentDataManager.getInstance().putData(key, bookShelfAdapter.getBooks().get(index));
                curDataKey = key;
                startActivityByAnim(intent, 0, 0);
            }
        };
    }

    private String curDataKey = null;
    private ItemTouchCallback itemTouchCallback;

    @Override
    public void onResume() {
        super.onResume();
        if (resumed) {
            resumed = false;
            stopBookShelfRefreshAnim();
        }
        BitIntentDataManager.getInstance().cleanData(curDataKey);
    }

    @Override
    public void onPause() {
        resumed = true;
        super.onPause();
    }

    public void toggleAdapterMode(boolean isList) {
        List<BookShelfBean> newDataS = bookShelfAdapter.getBooks();
        if (!isList && bookShelfAdapter instanceof BookShelfListAdapter) {
            setFixedGridAdapter(true, null);
        } else if (isList && bookShelfAdapter instanceof BookShelfGridAdapter) {
            rvBookshelf.setLayoutManager(new LinearLayoutManager(getContext()));
            bookShelfAdapter = new BookShelfListAdapter(getActivity());
            rvBookshelf.setAdapter((RecyclerView.Adapter) bookShelfAdapter);
            bookShelfAdapter.setItemClickListener(getAdapterListener());
            if (itemTouchCallback != null)
                itemTouchCallback.setOnItemTouchCallbackListener(bookShelfAdapter.getItemTouchCallbackListener());
        }
        bookShelfAdapter.setBooks(newDataS);
    }

    private void stopBookShelfRefreshAnim() {
        if (bookShelfAdapter.getBooks() != null && bookShelfAdapter.getBooks().size() > 0) {
            for (BookShelfBean bookShelfBean : bookShelfAdapter.getBooks()) {
                if (bookShelfBean.isLoading()) {
                    bookShelfBean.setLoading(false);
                    refreshBook(bookShelfBean.getNoteUrl());
                }
            }
        }
    }

    @Override
    public void refreshBookShelf(List<BookShelfBean> bookShelfBeanList) {
        bookShelfAdapter.replaceAll(bookShelfBeanList, bookPx);
        if (rlEmptyView == null) return;
        if (bookShelfBeanList.size() > 0) {
            rlEmptyView.setVisibility(View.GONE);
        } else {
            emptyImg.setVisibility(View.VISIBLE);
            tvEmpty.setText(R.string.bookshelf_empty);
            rlEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshBook(String noteUrl) {
        bookShelfAdapter.refreshBook(noteUrl);
    }

    @Override
    public void updateGroup(Integer group) {
        this.group = group;
    }

    @Override
    public void refreshError(String error) {

    }

    @Override
    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface CallBackValue {
        boolean isRecreate();

        int getGroup();
    }

}
