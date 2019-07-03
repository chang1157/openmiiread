package com.moses.miiread.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.base.MBaseFragment;
import com.moses.miiread.bean.FindKindBean;
import com.moses.miiread.bean.FindKindGroupBean;
import com.moses.miiread.presenter.FindBookPresenterMoses;
import com.moses.miiread.presenter.contract.FindBookContractMoses;
import com.moses.miiread.utils.theme.ThemeStore;
import com.moses.miiread.view.activity.ChoiceCatoActivity;
import com.moses.miiread.view.adapter.FindAdapterMoses;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;
import com.moses.miiread.widget.itemdecoration.DividerItemDecoration;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.List;
import java.util.Objects;

public class FindBookFragmentMoses extends MBaseFragment<FindBookContractMoses.Presenter> implements FindBookContractMoses.View<FindKindGroupBean, FindKindBean>, OnItemClickListenerTwo {
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.rl_empty_view)
    RelativeLayout rlEmptyView;
    @BindView(R.id.rv_find_right)
    RecyclerView rvFindRight;

    private Unbinder unbinder;
    private FindAdapterMoses findAdapterMoses;
    private LinearLayoutManager rightLayoutManager;

    @Override
    public int createLayoutId() {
        return R.layout.fragment_book_find_moses;
    }

    @Override
    protected FindBookContractMoses.Presenter initInjector() {
        return new FindBookPresenterMoses();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        refreshLayout.setColorSchemeColors(ThemeStore.accentColor(MApplication.getInstance()));
        refreshLayout.setOnRefreshListener(() -> {
            mPresenter.initData();
            refreshLayout.setRefreshing(false);
        });
        rightLayoutManager = new LinearLayoutManager(getContext());
        initRecyclerView();
    }

    /**
     * 首次逻辑操作
     */
    @Override
    protected void firstRequest() {
        super.firstRequest();
        mPresenter.initData();
    }

    @Override
    public void upStyle() {
        initRecyclerView();
    }

    @Override
    public synchronized void updateUI(List<RecyclerViewData<FindKindGroupBean, FindKindBean>> group) {
        if (rlEmptyView == null) return;
        if (group.size() == 0) {
            tvEmpty.setText(R.string.no_find);
            rlEmptyView.setVisibility(View.VISIBLE);
        } else {
            rlEmptyView.setVisibility(View.GONE);
        }
        findAdapterMoses.setDatas(group);
        rlEmptyView.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        findAdapterMoses = new FindAdapterMoses(this);
        rvFindRight.setLayoutManager(rightLayoutManager);
        rvFindRight.setAdapter(findAdapterMoses);
        rvFindRight.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onLongClick(View view, int index) {
//        {//长按：置顶/删除/等
//            if (getActivity() == null) return;
//            FindKindGroupBean groupBean;
//            groupBean = (FindKindGroupBean) findAdapterMoses.getDatas().get(index).getGroupData();
//            BookSourceBean sourceBean = BookSourceManager.getBookSourceByUrl(groupBean.getGroupTag());
//            if (sourceBean == null) {
//                return;
//            }
//            PopupMenu popupMenu = new PopupMenu(getContext(), view);
//            popupMenu.getMenu().add(R.string.edit);
//            popupMenu.getMenu().add(R.string.to_top);
//            popupMenu.getMenu().add(R.string.delete);
//            popupMenu.setOnMenuItemClickListener(item -> {
//                if (item.getTitle().equals(getString(R.string.edit))) {
//                    SourceEditActivity.startThis(getActivity(), sourceBean);
//                } else if (item.getTitle().equals(getString(R.string.to_top))) {
//                    BookSourceManager.toTop(sourceBean);
//                } else if (item.getTitle().equals(getString(R.string.delete))) {
//                    BookSourceManager.removeBookSource(sourceBean);
//                }
//                return true;
//            });
//            popupMenu.show();
//        }
    }

    @Override
    public void onClick(View view, int index) {
        FindKindGroupBean kindGroupBean = findAdapterMoses.getDatas().get(index).getGroupData();
        Intent intent = new Intent(getActivity(), ChoiceCatoActivity.class);
        intent.putExtra("sourceUrl", kindGroupBean.getGroupTag());
        intent.putExtra("sourceName", kindGroupBean.getGroupName());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
