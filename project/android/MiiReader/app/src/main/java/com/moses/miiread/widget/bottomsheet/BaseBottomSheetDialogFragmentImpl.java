package com.moses.miiread.widget.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moses.miiread.R;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBottomSheetDialogFragmentImpl extends BottomSheetDialogFragment {

    public abstract View getContentView();

    public abstract void setContentView(View view);

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        if (getContentView() != null)
            dialog.setContentView(getContentView());
        return dialog;
    }
}