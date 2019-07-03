package com.moses.miiread.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseFragment;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.presenter.BookDetailPresenter;
import com.moses.miiread.presenter.ChoiceBookPresenter;
import com.moses.miiread.presenter.contract.ChoiceBookContract;
import com.moses.miiread.view.activity.BookDetailActivity;
import com.moses.miiread.view.activity.SearchBookActivity;
import com.moses.miiread.view.adapter.ChoiceBookAdapter;
import com.moses.miiread.widget.recycler.refresh.OnLoadMoreListener;
import com.moses.miiread.widget.recycler.refresh.RefreshRecyclerView;

import java.util.List;
import java.util.Objects;

public class ChoiceBookPagerFragment extends MBaseFragment<ChoiceBookPresenter> implements ChoiceBookContract.View {

    private Unbinder unbinder;

    private String url;
    private String title;
    private String tag;

    @BindView(R.id.rfRv_search_books)
    RefreshRecyclerView rfRvSearchBooks;

    private ChoiceBookAdapter searchBookAdapter;
    private View viewRefreshError;

    public ChoiceBookPagerFragment(String url, String title, String tag) {
        this.url = url;
        this.title = title;
        this.tag = tag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url");
            title = savedInstanceState.getString("title");
            tag = savedInstanceState.getString("tag");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        searchBookAdapter = new ChoiceBookAdapter(getActivity());
    }

    @Override
    public int createLayoutId() {
        return R.layout.fragment_choice_book_pager;
    }

    @Override
    protected ChoiceBookPresenter initInjector() {
        Intent intent = new Intent();
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("tag", tag);
        return new ChoiceBookPresenter(intent);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        rfRvSearchBooks.setRefreshRecyclerViewAdapter(searchBookAdapter, new LinearLayoutManager(getActivity()));

        viewRefreshError = LayoutInflater.from(getActivity()).inflate(R.layout.view_searchbook_refresh_error, null);
        viewRefreshError.findViewById(R.id.tv_refresh_again).setOnClickListener(v -> {
            searchBookAdapter.replaceAll(null);
            //刷新失败 ，重试
            mPresenter.initPage();
            mPresenter.toSearchBooks(null);
            startRefreshAnim();
        });
        rfRvSearchBooks.setNoDataAndrRefreshErrorView(LayoutInflater.from(getActivity()).inflate(R.layout.view_searchbook_no_data, null),
                viewRefreshError);
    }

    @Override
    protected void bindEvent() {
        super.bindEvent();
        searchBookAdapter.setItemClickListener(new ChoiceBookAdapter.OnItemClickListener() {
            @Override
            public void clickAddShelf(View clickView, int position, SearchBookBean searchBookBean) {
                SearchBookActivity.startByKey(getContext(), searchBookBean.getName());
            }

            @Override
            public void clickItem(View animView, int position, SearchBookBean searchBookBean) {
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra("openFrom", BookDetailPresenter.FROM_SEARCH);
                intent.putExtra("data", searchBookBean);
                startActivityByAnim(intent, 0, 0);
            }
        });

        rfRvSearchBooks.setBaseRefreshListener(() -> {
            mPresenter.initPage();
            mPresenter.toSearchBooks(null);
            startRefreshAnim();
        });
        rfRvSearchBooks.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void startLoadMore() {
                mPresenter.toSearchBooks(null);
            }

            @Override
            public void loadMoreErrorTryAgain() {
                mPresenter.toSearchBooks(null);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void refreshSearchBook(List<SearchBookBean> books) {
        searchBookAdapter.replaceAll(books);
    }

    @Override
    public void refreshFinish(Boolean isAll) {
        rfRvSearchBooks.finishRefresh(isAll, true);
    }

    @Override
    public void loadMoreFinish(Boolean isAll) {
        rfRvSearchBooks.finishLoadMore(isAll, true);
    }

    @Override
    public void loadMoreSearchBook(final List<SearchBookBean> books) {
        if (books.size() <= 0) {
            loadMoreFinish(true);
            return;
        }
        for (SearchBookBean searchBook : searchBookAdapter.getSearchBooks()) {
            if (Objects.equals(books.get(0).getName(), searchBook.getName()) && Objects.equals(books.get(0).getAuthor(), searchBook.getAuthor())) {
                loadMoreFinish(true);
                return;
            }
        }
        searchBookAdapter.addAll(books);
        loadMoreFinish(false);
    }

    @Override
    public void searchBookError(String msg) {
        if (mPresenter.getPage() > 1) {
            rfRvSearchBooks.loadMoreError();
            if (msg != null) {
                toast(msg);
            }
        } else {
            //刷新失败
            rfRvSearchBooks.refreshError();
            if (msg != null) {
                ((TextView) viewRefreshError.findViewById(R.id.tv_error_msg)).setText(msg);
            } else {
                ((TextView) viewRefreshError.findViewById(R.id.tv_error_msg)).setText(R.string.get_data_error);
            }
        }

    }

    @Override
    public void addBookShelfFailed(String massage) {
        toast(massage);
    }

    @Override
    public void startRefreshAnim() {
        rfRvSearchBooks.startRefresh();
    }

    @Override
    protected void firstRequest() {
        super.firstRequest();
    }
}