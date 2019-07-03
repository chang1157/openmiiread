package com.moses.miiread.widget.radio_btn;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RadioButton;
import androidx.appcompat.widget.AppCompatImageButton;
import com.moses.miiread.R;

public class ToneRadioBtn extends AppCompatImageButton {

    private boolean checked;

    public void setChecked(boolean checked) {
        synchronized (ToneRadioBtn.class.getName()) {
            this.checked = checked;
            updateSrc();
            updateParentRadioGrp();
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public interface OnCheckedChangeListener {
        void onCheckChanged(int viewId, boolean checked);
    }

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public ToneRadioBtn(Context context) {
        this(context, null);
    }

    public ToneRadioBtn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToneRadioBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        updateSrc();
        setOnClickListener(v -> {
            if (isChecked())
                return;
            setChecked(true);
            if (onCheckedChangeListener != null)
                onCheckedChangeListener.onCheckChanged(v.getId(), this.isChecked());
        });
    }

    private void updateSrc() {
        if (this.isChecked())
            setImageDrawable(getResources().getDrawable(R.drawable.ic_done_24dp));
        else
            setImageDrawable(new ColorDrawable());
    }

    private void updateParentRadioGrp() {
        synchronized (ToneRadioBtn.class.getName()) {
            ViewParent parent = getParent();
            if (isChecked() && parent instanceof ViewGroup) {
                ViewGroup grp = (ViewGroup) parent;
                for (int i = 0; i < grp.getChildCount(); i++) {
                    View v = grp.getChildAt(i);
                    if (v.getId() == getId())
                        continue;
                    if (v instanceof RadioButton && ((RadioButton) v).isChecked())
                        ((RadioButton) v).setChecked(false);
                    else if (v instanceof ToneRadioBtn && ((ToneRadioBtn) v).isChecked())
                        ((ToneRadioBtn) v).setChecked(false);
                }
            }
        }
    }
}