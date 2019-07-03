package com.moses.miiread.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kunfei.basemvplib.BaseActivity;
import com.moses.miiread.BitIntentDataManager;
import com.moses.miiread.R;
import com.moses.miiread.bean.BookSourceBean;
import com.moses.miiread.bean.FindKindBean;
import com.moses.miiread.bean.FindKindGroupBean;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.logic.ConfigMng;
import com.moses.miiread.model.BookSourceManager;
import com.moses.miiread.model.WebBookModel;
import com.moses.miiread.presenter.BookDetailPresenter;
import com.moses.miiread.utils.StringUtils;
import com.moses.miiread.view.activity.BookDetailActivity;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class FindAdapterMoses extends RecyclerView.Adapter<FindAdapterMoses.MyViewHolder> {
    private List<RecyclerViewData<FindKindGroupBean, FindKindBean>> datas = new ArrayList<>();
    private Context context;
    private OnItemClickListenerTwo onItemClickListener;

    public FindAdapterMoses(OnItemClickListenerTwo onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDatas(List<RecyclerViewData<FindKindGroupBean, FindKindBean>> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_find_right_grp, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int pos) {
        FindKindGroupBean groupBean = datas.get(pos).getGroupData();
        myViewHolder.sourceName.setText(StringUtils.fixSourceDisplayName(groupBean.getGroupName()));
        @SuppressWarnings("unchecked") final FindChildAdapter<SearchBookBean>[] marketAdapter = new FindChildAdapter[1];
        marketAdapter[0] = new FindChildAdapter<>((Activity) context, new OnItemClickListenerTwo() {
            @Override
            public void onClick(View view, int index) {
                if (index != marketAdapter[0].getItemCount() - 1) {
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra("openFrom", BookDetailPresenter.FROM_SEARCH);
                    intent.putExtra("data", marketAdapter[0].getDatas().get(index));
                    ((BaseActivity) context).startActivityByAnim(intent, 0, 0);
                } else myViewHolder.cv.performClick();
            }

            @Override
            public void onLongClick(View view, int index) {

            }
        });
        myViewHolder.childListV.setAdapter(marketAdapter[0]);
        for (Object object : datas.get(pos).getChildList()) {
            FindKindBean kindBean = (FindKindBean) object;
            String url = kindBean.getKindUrl();
            String tag = kindBean.getTag();
            Object dataObj = BitIntentDataManager.getInstance().getData("cachedMarketBook::" + tag);
            boolean searchWeb = false;
            if (dataObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<SearchBookBean> datas = (List<SearchBookBean>) dataObj;
                if (datas.size() > 0)
                    marketAdapter[0].setDatas(datas);
                else searchWeb = true;

            } else
                searchWeb = true;
            if (searchWeb) {
                int page = 1;
                searchBook(marketAdapter[0], url, page, tag);
            }
        }
        myViewHolder.cv.setOnClickListener(v -> {
            BookSourceBean sourceBean = BookSourceManager.getBookSourceByUrl(groupBean.getGroupTag());
            if (sourceBean != null && onItemClickListener != null) {
                onItemClickListener.onClick(v, pos);
            }
        });
        if (pos == getItemCount() - 1)
            myViewHolder.divider.setVisibility(View.GONE);
        else
            myViewHolder.divider.setVisibility(View.VISIBLE);
    }

    private void searchBook(final FindChildAdapter<SearchBookBean> marketAdapter, final String url, final int page, final String tag) {
        WebBookModel.getInstance().findBook(url, page, tag)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SearchBookBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<SearchBookBean> value) {
                        marketAdapter.setPageCount(marketAdapter.getPageCount() + 1);
                        marketAdapter.addDatas(value);
                        BitIntentDataManager.getInstance().putData("cachedMarketBook::" + tag, marketAdapter.getDatas());//
                        //当书库子列表小于指定大小时才进行加载更多的操作
                        if (marketAdapter.getItemCount() < ConfigMng.MAX_BOOK_MARKET_LIST_ITEM_COUNT && marketAdapter.getPageCount() < ConfigMng.MAX_BOOK_MARKET_LIST_PAGE_COUNT)
                            searchBook(marketAdapter, url, page + 1, tag);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public List<RecyclerViewData<FindKindGroupBean, FindKindBean>> getDatas() {
        return datas;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        View cv;
        TextView sourceName;
        RecyclerView childListV;
        View divider;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.ll_content);
            sourceName = itemView.findViewById(R.id.tv_source_name);
            childListV = itemView.findViewById(R.id.child_listv);
            LinearLayoutManager lm = new LinearLayoutManager(itemView.getContext());
            lm.setOrientation(RecyclerView.HORIZONTAL);
            childListV.setLayoutManager(lm);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}

