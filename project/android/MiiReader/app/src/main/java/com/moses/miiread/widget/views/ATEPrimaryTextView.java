package com.moses.miiread.widget.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import com.moses.miiread.utils.theme.ThemeStore;

/**
 * @author origin
 */
public class ATEPrimaryTextView extends AppCompatTextView {

    public ATEPrimaryTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ATEPrimaryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATEPrimaryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setTextColor(ThemeStore.textColorPrimary(context));
    }
}
