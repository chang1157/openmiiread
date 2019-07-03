package com.moses.miiread.widget.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;
import com.moses.miiread.utils.ScreenUtils;
import com.moses.miiread.utils.Selector;
import com.moses.miiread.utils.theme.ThemeStore;

/**
 * @author origin
 */
public class ATERadioNoButton extends AppCompatRadioButton {

    public ATERadioNoButton(Context context) {
        super(context);
        init(context, null);
    }

    public ATERadioNoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATERadioNoButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackground(Selector.shapeBuild()
                .setCornerRadius(ScreenUtils.dpToPx(3))
                .setStrokeWidth(ScreenUtils.dpToPx(1))
                .setCheckedBgColor(ThemeStore.accentColor(context))
                .setCheckedStrokeColor(ThemeStore.accentColor(context))
                .setDefaultStrokeColor(Color.WHITE)
                .create());
    }
}
