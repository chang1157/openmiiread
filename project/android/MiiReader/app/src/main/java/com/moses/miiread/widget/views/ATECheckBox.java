package com.moses.miiread.widget.views;

import android.content.Context;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatCheckBox;
import com.moses.miiread.utils.theme.ATH;
import com.moses.miiread.utils.theme.ThemeStore;

/**
 * @author origin
 */
public class ATECheckBox extends AppCompatCheckBox {

    public ATECheckBox(Context context) {
        super(context);
        init(context, null);
    }

    public ATECheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATECheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ATH.setTint(this, ThemeStore.accentColor(context));
    }
}
