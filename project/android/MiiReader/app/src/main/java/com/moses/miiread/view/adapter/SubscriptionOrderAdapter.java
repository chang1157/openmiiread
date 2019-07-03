package com.moses.miiread.view.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.moses.miiread.R;
import com.moses.miiread.bean.SubscriptionOrderBean;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionOrderAdapter extends RecyclerView.Adapter<SubscriptionOrderAdapter.VHolder> {

    private Context context;
    private List<SubscriptionOrderBean> list;
    private AtomicInteger checkedIndex = new AtomicInteger(0);

    class VHolder extends RecyclerView.ViewHolder {

        TextView name, summary, desc, coinIcon, cost, mark;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.summary = itemView.findViewById(R.id.summary);
            this.coinIcon = itemView.findViewById(R.id.coin_icon);
            this.cost = itemView.findViewById(R.id.cost);
            // TODO: 2019/4/29
        }
    }

    public SubscriptionOrderAdapter(Context context, List<SubscriptionOrderBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<SubscriptionOrderBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<SubscriptionOrderBean> getList() {
        return list;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemV = LayoutInflater.from(context).inflate(R.layout.item_subscription_order, parent, false);
        return new VHolder(itemV);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        SubscriptionOrderBean orderBean = list.get(position);
        holder.name.setText(orderBean.typeName);
        holder.cost.setText(String.valueOf(((Float) orderBean.cost).intValue()));
        if (!TextUtils.isEmpty(orderBean.typeSummary))
            holder.summary.setText(orderBean.typeSummary);
        else if(orderBean.coins != 0 || orderBean.freeCoins != 0)
        {
            StringBuilder builder = new StringBuilder();
            ForegroundColorSpan cSp = null, fSp = null;
            if(orderBean.coins != 0)
            {
                builder.append(orderBean.coins).append(" ").append("金币");
                cSp = new ForegroundColorSpan(0xff3390fe);
            }
            if(orderBean.freeCoins != 0)
            {
                if(builder.toString().length() != 0)
                    builder.append( " + ");
                builder.append(orderBean.freeCoins).append(" ").append("免费币");
                fSp = new ForegroundColorSpan(0xff3390fe);
            }
            SpannableString ss = new SpannableString(builder.toString());
            if(cSp != null)
                ss.setSpan(cSp, builder.indexOf(String.valueOf(orderBean.coins)), builder.indexOf(String.valueOf(orderBean.coins)) + String.valueOf(orderBean.coins).length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(fSp != null)
                ss.setSpan(fSp, builder.indexOf(String.valueOf(orderBean.freeCoins)), builder.indexOf(String.valueOf(orderBean.freeCoins)) + String.valueOf(orderBean.freeCoins).length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.summary.setText(ss);
        }else
            holder.summary.setText("");
        ConstraintLayout constLayout = holder.itemView.findViewById(R.id.list_item);
        if (orderBean.isChecked()) {
            if (orderBean.isVip)
                constLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_subscription_vip_checked));
            else
                constLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_subscription_normal_checked));
        } else
            constLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_subscription_uncheck));
        if (orderBean.isVip) {
            holder.cost.setTextColor(0xffcdaa61);
            holder.coinIcon.setTextColor(0xffcdaa61);
            holder.summary.setTextColor(0xff3390fe);
        } else {
            holder.cost.setTextColor(0xff3390fe);
            holder.coinIcon.setTextColor(0xff3390fe);
        }
        constLayout.setOnClickListener(v -> {
            if (checkedIndex.get() == position)
                return;
            orderBean.setChecked(!orderBean.isChecked());
            if (orderBean.isChecked())
                checkedIndex.set(position);
            for (int i = 0; i < getItemCount(); i++) {
                if (i != checkedIndex.get())
                    list.get(i).setChecked(false);
            }
            notifyDataSetChanged();
        });
        // TODO: 2019/4/29
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
