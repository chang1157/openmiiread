package com.moses.miiread.widget.recycler.expandable.bean;

import java.util.List;

/**
 * author：origin
 * describe:
 * date: 2017/5/22
 * G 为group数据对象
 * C 为child数据对象
 */
@SuppressWarnings("unchecked")
public class RecyclerViewData<G, C> {

    private GroupItem groupItem;

    /**
     * @param isExpand   初始化展示数据时，该组数据是否展开
     */
    public RecyclerViewData(G groupData, List<C> childDatas, boolean isExpand) {
        this.groupItem = new GroupItem(groupData, childDatas, isExpand);
    }

    public RecyclerViewData(G groupData, List<C> childDatas) {
        this.groupItem = new GroupItem(groupData, childDatas, false);
    }

    public GroupItem getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(GroupItem groupItem) {
        this.groupItem = groupItem;
    }

    public G getGroupData() {
        return (G) groupItem.getGroupData();
    }

    public List<C> getChildList() {
        return groupItem.getChildDatas();
    }

    public void removeChild(int position) {
        if (null == groupItem || !groupItem.hasChilds()) {
            return;
        }
        groupItem.getChildDatas().remove(position);
    }

    public C getChild(int childPosition) {
        return (C) groupItem.getChildDatas().get(childPosition);
    }

}
