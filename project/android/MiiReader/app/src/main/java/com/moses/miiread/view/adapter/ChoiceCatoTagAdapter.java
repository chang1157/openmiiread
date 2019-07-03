package com.moses.miiread.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moses.miiread.R;
import com.moses.miiread.bean.FindKindBean;
import com.moses.miiread.view.adapter.base.OnItemClickListenerTwo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChoiceCatoTagAdapter extends RecyclerView.Adapter<ChoiceCatoTagAdapter.VHolder> {

    private List<FindKindBean> kindBeans;
    private Context context;
    private LayoutInflater layoutInflater;

    private AtomicInteger curSelectedIndex = new AtomicInteger(0);

    public ChoiceCatoTagAdapter(Context context, List<FindKindBean> kindBeans) {
        this.kindBeans = kindBeans;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setCurSelectedIndex(int posi){
        if(curSelectedIndex.get() != posi) {
            int prevSelectedIndex = curSelectedIndex.get();
            curSelectedIndex.set(posi);
            notifyItemChanged(prevSelectedIndex);
            notifyItemChanged(posi);
        }
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_choice_cato_tag, parent, false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        if (onItemClickListenerTwo != null) {
            holder.tag.setOnClickListener(v -> {
                onItemClickListenerTwo.onClick(holder.itemView, position);
               setCurSelectedIndex(position);
            });
        }
        holder.tag.setText(kindBeans.get(position).getKindName());
        if (position == curSelectedIndex.get()){
            holder.tag.setBackgroundResource(R.drawable.bg_dlg_button_solid);
            holder.tag.setTextColor(context.getResources().getColor(R.color.tv_text_button_nor));
        }
        else{
            holder.tag.setBackgroundResource(R.drawable.bg_dlg_button_stroke);
            holder.tag.setTextColor(context.getResources().getColor(R.color.tv_text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return kindBeans.size();
    }

    public static class VHolder extends RecyclerView.ViewHolder {

        TextView tag;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.text1);
        }
    }

    public ChoiceCatoTagAdapter setOnItemClickListenerTwo(OnItemClickListenerTwo onItemClickListenerTwo) {
        this.onItemClickListenerTwo = onItemClickListenerTwo;
        return this;
    }

    private OnItemClickListenerTwo onItemClickListenerTwo;
}
