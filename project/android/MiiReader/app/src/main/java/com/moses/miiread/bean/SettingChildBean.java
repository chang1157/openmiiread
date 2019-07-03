package com.moses.miiread.bean;

public class SettingChildBean {
    private String title;
    private String desc;
    private String mark;
    private boolean checkable;
    private boolean checked;
    private boolean rightArrow = false;

    private String prefKey;

    public SettingChildBean(String title, String desc, String mark, boolean checkable, boolean checked, String prefKey) {
        this.title = title;
        this.desc = desc;
        this.mark = mark;
        this.checkable = checkable;
        this.checked = checked;
        this.prefKey = prefKey;
    }

    public SettingChildBean(String title, String desc, String mark, boolean checkable, boolean checked, boolean rightArrow, String prefKey) {
        this.title = title;
        this.desc = desc;
        this.mark = mark;
        this.checkable = checkable;
        this.checked = checked;
        this.rightArrow = rightArrow;
        this.prefKey = prefKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public boolean isRightArrow() {
        return rightArrow;
    }

    public void setRightArrow(boolean rightArrow) {
        this.rightArrow = rightArrow;
    }
}
