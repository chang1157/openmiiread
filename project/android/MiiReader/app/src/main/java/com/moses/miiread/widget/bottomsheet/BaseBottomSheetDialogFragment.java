package com.moses.miiread.widget.bottomsheet;

import android.content.DialogInterface;
import android.view.View;
import androidx.annotation.NonNull;

public class BaseBottomSheetDialogFragment extends BaseBottomSheetDialogFragmentImpl {

    private View contentV;

    public BaseBottomSheetDialogFragment(){

    }

    @Override
    public void setContentView(View view) {
        this.contentV = view;
    }

    @Override
    public View getContentView() {
        return contentV;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null)
            dismissListener.onDismiss();
    }

    public void setDismissListener(BottomSheetDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public BottomSheetDismissListener getDismissListener() {
        return dismissListener;
    }

    private BottomSheetDismissListener dismissListener;
    public interface BottomSheetDismissListener{
        void onDismiss();
    }
}
