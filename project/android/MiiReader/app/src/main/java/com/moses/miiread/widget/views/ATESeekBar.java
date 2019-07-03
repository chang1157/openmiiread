package com.moses.miiread.widget.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;
import com.moses.miiread.R;
import com.moses.miiread.utils.theme.ATH;
import com.moses.miiread.utils.theme.ThemeStore;

/**
 * @author origin
 */
public class ATESeekBar extends AppCompatSeekBar {

    public ATESeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public ATESeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATESeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ATH.setTint(this, getResources().getColor(R.color.colorControlActivated));
    }
}