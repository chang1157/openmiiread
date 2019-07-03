package com.moses.miiread.utils;

import android.annotation.SuppressLint;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;

public class BottomNavigationViewHelper {
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("RestrictedApi")
    public static void disableShiftingMode(BottomNavigationView view) {
        try {
            BottomNavigationMenuView mMenuView = (BottomNavigationMenuView) view.getChildAt(0);
            Field mShiftingModeField = BottomNavigationMenuView.class.getDeclaredField("mShiftingMode");
            mShiftingModeField.setAccessible(true);
            mShiftingModeField.set(mMenuView, false);
            for (int i = 0; i < mMenuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) mMenuView.getChildAt(i);
                itemView.setShifting(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("RestrictedApi")
    public static void disableItemScale(BottomNavigationView view) {
        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            Field mLargeLabelField = BottomNavigationItemView.class.getDeclaredField("mLargeLabel");
            Field mSmallLabelField = BottomNavigationItemView.class.getDeclaredField("mSmallLabel");
            Field mShiftAmountField = BottomNavigationItemView.class.getDeclaredField("mShiftAmount");
            Field mScaleUpFactorField = BottomNavigationItemView.class.getDeclaredField("mScaleUpFactor");
            Field mScaleDownFactorField = BottomNavigationItemView.class.getDeclaredField("mScaleDownFactor");
            mSmallLabelField.setAccessible(true);
            mLargeLabelField.setAccessible(true);
            mShiftAmountField.setAccessible(true);
            mScaleUpFactorField.setAccessible(true);
            mScaleDownFactorField.setAccessible(true);
            final float fontScale = view.getResources().getDisplayMetrics().scaledDensity;
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                TextView lagerObj = (TextView) mLargeLabelField.get(itemView);
                TextView smallObj = (TextView) mSmallLabelField.get(itemView);
                lagerObj.setTextSize(smallObj.getTextSize() / fontScale + 0.5f);
                mShiftAmountField.set(itemView, 0);
                mScaleUpFactorField.set(itemView, 1f);
                mScaleDownFactorField.set(itemView, 1f);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }
    }
}