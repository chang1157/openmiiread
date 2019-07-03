package com.moses.miiread.widget.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import com.moses.miiread.utils.Selector;
import com.moses.miiread.utils.theme.ThemeStore;

public class ATETextInputLayout extends TextInputLayout {
    public ATETextInputLayout(Context context) {
        super(context);
        init(context);
    }

    public ATETextInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ATETextInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setHintTextColor(Selector.colorBuild().setDefaultColor(ThemeStore.accentColor(context)).create());
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
    }
}
