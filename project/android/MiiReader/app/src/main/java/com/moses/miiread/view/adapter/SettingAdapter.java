package com.moses.miiread.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.moses.miiread.R;
import com.moses.miiread.bean.SettingChildBean;
import com.moses.miiread.bean.SettingGrpBean;
import com.moses.miiread.widget.recycler.expandable.BaseExpandAbleViewHolder;
import com.moses.miiread.widget.recycler.expandable.BaseExpandableRecyclerAdapter;
import com.moses.miiread.widget.recycler.expandable.bean.RecyclerViewData;

import java.util.List;

public class SettingAdapter extends BaseExpandableRecyclerAdapter<SettingGrpBean, SettingChildBean, SettingAdapter.VHolder> {

    private Context context;
    private List<RecyclerViewData<SettingGrpBean, SettingChildBean>> datas;
    private LayoutInflater inflater;

    public SettingAdapter(Context ctx, List<RecyclerViewData<SettingGrpBean, SettingChildBean>> datas) {
        super(ctx, datas);
        this.context = ctx;
        this.datas = datas;
        this.inflater = LayoutInflater.from(ctx);
    }

    public void setDatas(List<RecyclerViewData<SettingGrpBean, SettingChildBean>> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(ViewGroup parent) {
        return inflater.inflate(R.layout.item_setting_grp, parent, false);
    }

    @Override
    public View getChildView(ViewGroup parent) {
        return inflater.inflate(R.layout.item_setting_child, parent, false);
    }

    @Override
    public VHolder createRealViewHolder(Context ctx, View view, int viewType) {
        return new VHolder(ctx, view, viewType);
    }

    @Override
    public void onBindGroupHolder(VHolder holder, int groupPos, int position, SettingGrpBean groupData) {
        RecyclerViewData<SettingGrpBean, SettingChildBean> grp = datas.get(groupPos);
        holder.grpTitle.setText(grp.getGroupData().getName());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindChildHolder(VHolder holder, int groupPos, int childPos, int position, SettingChildBean childData) {
        SettingChildBean cld = datas.get(groupPos).getChild(childPos);
        holder.childTitle.setText(cld.getTitle());
        if (!TextUtils.isEmpty(cld.getDesc())) {
            holder.childDesc.setText(cld.getDesc());
            holder.childDesc.setVisibility(View.VISIBLE);
        } else
            holder.childDesc.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(cld.getMark())) {
            holder.childMark.setText(cld.getMark());
            holder.childMark.setVisibility(View.VISIBLE);
        } else
            holder.childMark.setVisibility(View.GONE);
        if (cld.isCheckable()) {
            holder.check.setChecked(cld.isChecked());
            holder.check.setVisibility(View.VISIBLE);
        } else
            holder.check.setVisibility(View.GONE);
        holder.arrow.setVisibility(cld.isRightArrow() ? View.VISIBLE : View.GONE);
    }

    class VHolder extends BaseExpandAbleViewHolder {

        private TextView grpTitle;
        private TextView childTitle, childDesc, childMark;
        private CheckBox check;
        private ImageView arrow;

        public VHolder(Context ctx, View itemView, int viewType) {
            super(ctx, itemView, viewType);
            if (viewType == VIEW_TYPE_PARENT)
                grpTitle = itemView.findViewById(R.id.text1);
            else {
                childTitle = itemView.findViewById(R.id.title);
                childDesc = itemView.findViewById(R.id.desc);
                childMark = itemView.findViewById(R.id.mark);
                check = itemView.findViewById(R.id.check);
                arrow = itemView.findViewById(R.id.cpv_arrow_right);
            }
        }

        @Override
        public int getChildViewResId() {
            return R.id.ll_content;
        }

        @Override
        public int getGroupViewResId() {
            return R.id.ll_content;
        }
    }
}
