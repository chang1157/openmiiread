package com.moses.miiread.utils;

/**
 * @author origin 2019/5/10
 * pager + fragment 夜间模式下可能重复recreate
 */
public class FragmentFixUtil {
    public static String getFragmentTagInViewPager(int pagerId, int index) {
        return "android:switcher:" + pagerId + ":" + index;
    }
}