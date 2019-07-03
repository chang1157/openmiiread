package com.moses.miiread.widget.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import com.moses.miiread.utils.theme.ATH;
import com.moses.miiread.utils.theme.ThemeStore;

/**
 * @author origin
 */
public class ATESwitch extends Switch {

    public ATESwitch(Context context) {
        super(context);
        init(context, null);
    }

    public ATESwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATESwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ATH.setTint(this, ThemeStore.accentColor(context));
    }

    @Override
    public boolean isShown() {
        return getParent() != null && getVisibility() == View.VISIBLE;
    }
}
