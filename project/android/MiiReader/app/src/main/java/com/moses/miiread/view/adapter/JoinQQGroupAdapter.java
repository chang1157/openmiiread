package com.moses.miiread.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moses.miiread.R;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

public class JoinQQGroupAdapter extends RecyclerView.Adapter<JoinQQGroupAdapter.VHolder> {
    private String[] items;
    private String[] names;
    private String[] extraActionTxt;
    private OnItemClickListenerTwo onItemClickListenerTwo;
    private Context context;

    public JoinQQGroupAdapter(Context context, String[] items, String[] names, OnItemClickListenerTwo onItemClickListenerTwo, String... extraActionTxt) {
        this.items = items;
        this.names = names;
        this.extraActionTxt = extraActionTxt;
        this.onItemClickListenerTwo = onItemClickListenerTwo;
        this.context = context;
    }

    class VHolder extends RecyclerView.ViewHolder {
        TextView title, extraTxt;

        VHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            try {
                this.extraTxt = itemView.findViewById(R.id.extraTxt);
            } catch (Exception ignore) {

            }
        }
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_join_qq_grp, parent, false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder vHolder, int position) {
        vHolder.title.setText(names[position]);
        if (vHolder.extraTxt != null) {
            if (extraActionTxt != null && extraActionTxt.length > 0) {
                vHolder.extraTxt.setVisibility(View.VISIBLE);
                try {
                    vHolder.extraTxt.setText(extraActionTxt[position]);
                } catch (Exception e) {
                    vHolder.extraTxt.setText(extraActionTxt[extraActionTxt.length - 1]);
                }
            } else
                vHolder.extraTxt.setVisibility(View.GONE);
        }
        if (vHolder.extraTxt != null) {
            vHolder.extraTxt.setOnClickListener(v -> onItemClickListenerTwo.onClick(v, position));
            vHolder.extraTxt.setOnLongClickListener(v -> {
                onItemClickListenerTwo.onLongClick(v, position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
