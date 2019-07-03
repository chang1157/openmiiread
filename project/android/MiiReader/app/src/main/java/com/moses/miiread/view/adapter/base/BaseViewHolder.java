package com.moses.miiread.view.adapter.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by origin on 17-5-17.
 */

public class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public IViewHolder<T> holder;

    public BaseViewHolder(View itemView, IViewHolder<T> holder) {
        super(itemView);
        this.holder = holder;
        holder.initView();
    }
}
