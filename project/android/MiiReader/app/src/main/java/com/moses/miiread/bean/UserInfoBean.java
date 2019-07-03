package com.moses.miiread.bean;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class UserInfoBean implements Parcelable,Cloneable {
    @Id
    private String uid;
    private String nickname;
    private String sex;
    private Long birth;
    private String phoneno;
    private String email;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Long getBirth() {
        return birth;
    }

    public void setBirth(Long birth) {
        this.birth = birth;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return " ,uid="+uid+"\n ,nickname="+nickname +"\n ,sex="+ sex +"\n ,birth="+birth +"\n ,phoneno="+phoneno +"\n, email="+ email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.nickname);
        dest.writeString(this.sex);
        dest.writeValue(this.birth);
        dest.writeString(this.phoneno);
        dest.writeString(this.email);
    }

    public UserInfoBean() {
    }

    protected UserInfoBean(Parcel in) {
        this.uid = in.readString();
        this.nickname = in.readString();
        this.sex = in.readString();
        this.birth = (Long) in.readValue(Long.class.getClassLoader());
        this.phoneno = in.readString();
        this.email = in.readString();
    }

    @Generated(hash = 1934565625)
    public UserInfoBean(String uid, String nickname, String sex, Long birth, String phoneno,
            String email) {
        this.uid = uid;
        this.nickname = nickname;
        this.sex = sex;
        this.birth = birth;
        this.phoneno = phoneno;
        this.email = email;
    }

    public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>() {
        @Override
        public UserInfoBean createFromParcel(Parcel source) {
            return new UserInfoBean(source);
        }

        @Override
        public UserInfoBean[] newArray(int size) {
            return new UserInfoBean[size];
        }
    };

    @Override
    public Object clone() throws CloneNotSupportedException {
        UserInfoBean userInfoBean = (UserInfoBean) super.clone();
        userInfoBean.uid = uid;
        userInfoBean.nickname = nickname;
        userInfoBean.sex = sex;
        userInfoBean.birth = birth;
        userInfoBean.phoneno = phoneno;
        userInfoBean.email = email;
        return userInfoBean;
    }
}
