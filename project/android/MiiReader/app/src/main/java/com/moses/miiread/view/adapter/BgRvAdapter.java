package com.moses.miiread.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moses.miiread.R;
import com.moses.miiread.help.ReadBookControl;

public class BgRvAdapter extends RecyclerView.Adapter<BgRvAdapter.VHolder> {
    private BgEle[] bgEles;
    private Context context;
    private OnBgActionItemClick onBgActionItemClick;
    private ReadBookControl readBookControl;

    public BgRvAdapter(Context context, @NonNull BgEle[] bgEles) {
        this.context = context;
        this.bgEles = bgEles;
        this.readBookControl = ReadBookControl.getInstance();
    }

    public BgRvAdapter setOnBgActionItemClick(OnBgActionItemClick onBgActionItemClick) {
        this.onBgActionItemClick = onBgActionItemClick;
        return this;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_bg_rv, parent, false);
        return new VHolder(v);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        BgEle bgEle = bgEles[position];
        if (position == 0)
            holder.lMargin.setVisibility(View.VISIBLE);
        else holder.lMargin.setVisibility(View.GONE);
        if (position != bgEles.length - 1)
            holder.rMargin.setVisibility(View.VISIBLE);
        else
            holder.rMargin.setVisibility(View.GONE);
        holder.bgTxt.setText(bgEle.title);
        holder.checkIcon.setImageTintList(ColorStateList.valueOf(bgEle.toneTxtClr));
        if (bgEle.bgAction != BgAction.setCustom) {
            holder.checkIcon.setImageResource(R.drawable.ic_check_black_24dp);
            try {
                holder.bgEg.setBackgroundResource((Integer) bgEle.ele);
            } catch (Exception e) {
                holder.bgEg.setBackgroundColor((Integer) bgEle.ele);
            }
        } else {
            if (readBookControl.getCustomToneSetted()) {
                holder.checkIcon.setImageTintList(ColorStateList.valueOf(readBookControl.getTextColor(3)));
                holder.bgEg.setBackground(readBookControl.getBgDrawable(3, context, 68, 42));
                holder.checkIcon.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                holder.checkIcon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.tone_text_white)));
                holder.bgEg.setBackgroundResource(R.drawable.bg_tone_bg_white);
                holder.checkIcon.setImageResource(R.drawable.ic_add);
            }
        }

        if (bgEle.isChecked || (bgEle.bgAction == BgAction.setCustom && !readBookControl.getCustomToneSetted()))
            holder.checkIcon.setVisibility(View.VISIBLE);
        else
            holder.checkIcon.setVisibility(View.GONE);

        holder.bgEg.setOnClickListener(v -> onBgActionItemClick.onItemClick(bgEle, position));
    }

    @Override
    public int getItemCount() {
        return bgEles.length;
    }

    public BgEle[] getBgEles() {
        return bgEles;
    }

    public static class VHolder extends RecyclerView.ViewHolder {

        TextView bgTxt;
        View lMargin, rMargin, ll_content;
        ImageView bgEg, checkIcon;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            bgEg = itemView.findViewById(R.id.bg_eg);
            bgTxt = itemView.findViewById(R.id.bg_txt);
            lMargin = itemView.findViewById(R.id.lmargin);
            rMargin = itemView.findViewById(R.id.rmargin);
            ll_content = itemView.findViewById(R.id.ll_content);
            checkIcon = itemView.findViewById(R.id.check_icon);
        }
    }

    public interface OnBgActionItemClick {
        void onItemClick(BgEle bgEle, int posi);
    }

    public static class BgEle {
        public BgAction bgAction;
        public String title;
        public Object ele;//null color img
        public int toneTxtClr;
        public boolean isChecked;

        public BgEle(BgAction bgAction, String title, Object ele, int toneTxtClr, boolean isChecked) {
            this.bgAction = bgAction;
            this.title = title;
            this.ele = ele;
            this.toneTxtClr = toneTxtClr;
            this.isChecked = isChecked;
        }
    }

    public enum BgAction {
        setBgClr0,
        setBgClr1,
        setBgClr2,
        setCustom
    }
}
