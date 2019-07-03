package com.moses.miiread.widget.views;

import android.content.Context;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatTextView;
import com.moses.miiread.R;
import com.moses.miiread.utils.ScreenUtils;
import com.moses.miiread.utils.Selector;
import com.moses.miiread.utils.theme.ThemeStore;

public class ATEAccentStrokeTextView extends AppCompatTextView {
    public ATEAccentStrokeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ATEAccentStrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATEAccentStrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackground(Selector.shapeBuild()
                .setCornerRadius(ScreenUtils.dpToPx(3))
                .setStrokeWidth(ScreenUtils.dpToPx(1))
                .setDisabledStrokeColor(context.getResources().getColor(R.color.md_grey_500))
                .setDefaultStrokeColor(getResources().getColor(R.color.colorControlActivated))
                .setPressedBgColor(context.getResources().getColor(R.color.transparent30))
                .create());
        setTextColor(Selector.colorBuild()
                .setDefaultColor(getResources().getColor(R.color.colorControlActivated))
                .setDisabledColor(context.getResources().getColor(R.color.md_grey_500))
                .create());
    }
}
