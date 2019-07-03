package com.moses.miiread.view.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.moses.miiread.R;
import com.moses.miiread.bean.SearchBookBean;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

import java.util.ArrayList;
import java.util.List;

public class FindChildAdapter<T extends SearchBookBean> extends RecyclerView.Adapter<FindChildAdapter.VHolder> {

    private List<T> datas = new ArrayList<>();
    private Activity activity;
    private OnItemClickListenerTwo onItemClickListener;
    private int pageCount = 0;

    public void setDatas(List<T> datas) {
        if (datas == null || datas.size() == 0)
            return;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<T> datas) {
        if (datas == null || datas.size() == 0)
            return;
        boolean notify = false;
        for (T data : datas) {
            boolean contain = false;
            for (T t : this.datas) {
                if (t.getNoteUrl().compareTo(data.getNoteUrl()) == 0)
                    contain = true;
            }
            if (!contain) {
                this.datas.add(data);
                notify = true;
            }
        }
        if (notify)
            notifyItemChanged(this.datas.size() - 1);
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void clearDatas() {
        if (datas.size() > 0)
            notifyItemMoved(0, datas.size() - 1);
        this.datas.clear();
    }

    public FindChildAdapter(Activity activity, OnItemClickListenerTwo onItemClickListener) {
        this.activity = activity;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(activity).inflate(R.layout.item_bookmarket_grid, parent, false);
            return new BookHolder(v);
        } else {
            View v = LayoutInflater.from(activity).inflate(R.layout.item_bookmarket_more, parent, false);
            return new ForwardVHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == datas.size())
            return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            BookHolder bookHolder = (BookHolder) holder;
            T sourceBean = datas.get(position);
            if (!activity.isFinishing()) {
                if (TextUtils.isEmpty(sourceBean.getCoverUrl())) {
                    Glide.with(activity).load(sourceBean.getCoverUrl())
                            .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop().placeholder(R.drawable.img_cover_default))
                            .into(bookHolder.ivCover);
                } else if (sourceBean.getCoverUrl().startsWith("http")) {
                    Glide.with(activity).load(sourceBean.getCoverUrl())
                            .apply(new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop().placeholder(R.drawable.img_cover_default))
                            .into(bookHolder.ivCover);
                }
                bookHolder.tvName.setText(sourceBean.getName());
            }
            if(position == 0)
                ((BookHolder) holder).fstGap.setVisibility(View.VISIBLE);
            else
                ((BookHolder) holder).fstGap.setVisibility(View.GONE);
        }
        holder.cv.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onClick(holder.cv, position);
        });
    }

    @Override
    public int getItemCount() {
        if (datas.size() > 0)
            return datas.size() + 1;
        return 0;
    }

    public static class BookHolder extends VHolder {
        ImageView ivCover;
        TextView tvName;
        View fstGap;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvName = itemView.findViewById(R.id.tv_name);
            fstGap = itemView.findViewById(R.id.fst_gap);
        }
    }

    public static class ForwardVHolder extends VHolder {

        public ForwardVHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class VHolder extends RecyclerView.ViewHolder {

        View cv;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.ll_content);
        }
    }
}
