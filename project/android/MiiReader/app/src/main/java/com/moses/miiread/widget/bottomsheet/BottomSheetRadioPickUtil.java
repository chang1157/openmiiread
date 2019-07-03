package com.moses.miiread.widget.bottomsheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import com.moses.miiread.R;

public class BottomSheetRadioPickUtil {

    public interface RadioIndexPickListener {
        void onIndexPick(int index);
    }

    public static void handleBottomSheetRadioPick(FragmentManager fm, Context context, int titleResId, String[] array, int selectedIndex, RadioIndexPickListener pickListener) {
        String title = context.getResources().getString(titleResId);
        handleBottomSheetRadioPick(fm, context, title, array, selectedIndex, pickListener);
    }

    public static void handleBottomSheetRadioPick(FragmentManager fm, Context context, int titleResId, int arrayResId, int selectedIndex, RadioIndexPickListener pickListener) {
        String title = context.getResources().getString(titleResId);
        String[] array = context.getResources().getStringArray(arrayResId);
        handleBottomSheetRadioPick(fm, context, title, array, selectedIndex, pickListener);
    }

    public static void handleBottomSheetRadioPick(FragmentManager fm, Context context, String title, int arrayResId, int selectedIndex, RadioIndexPickListener pickListener) {
        String[] array = context.getResources().getStringArray(arrayResId);
        handleBottomSheetRadioPick(fm, context, title, array, selectedIndex, pickListener);
    }

    public static void handleBottomSheetRadioPick(FragmentManager fm, Context context, String title, String[] array, int selectedIndex, RadioIndexPickListener pickListener) {
        if (array.length < 2 || array.length > 10) {
            try {
                throw new Exception("BottomSheetRadioPickUtil:: handleBottomSheetRadioPick() 默认只支持2 ~ 10个选项卡。如有其他需要，请修改源码");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        int layoutResId = -1024;
        switch (array.length) {
            case 2:
                layoutResId = R.layout.dialog_bottom_radio_pick_2;
                break;
            case 3:
                layoutResId = R.layout.dialog_bottom_radio_pick_3;
                break;
            case 4:
                layoutResId = R.layout.dialog_bottom_radio_pick_4;
                break;
            case 5:
                layoutResId = R.layout.dialog_bottom_radio_pick_5;
                break;
            case 6:
                layoutResId = R.layout.dialog_bottom_radio_pick_6;
                break;
            case 7:
                layoutResId = R.layout.dialog_bottom_radio_pick_7;
                break;
            case 8:
                layoutResId = R.layout.dialog_bottom_radio_pick_8;
                break;
            case 9:
                layoutResId = R.layout.dialog_bottom_radio_pick_9;
                break;
            case 10:
                layoutResId = R.layout.dialog_bottom_radio_pick_10;
                break;
            default:
                break;
        }
        View view = LayoutInflater.from(context).inflate(layoutResId, null, false);
        BaseBottomSheetDialogFragment fragment = new BaseBottomSheetDialogFragment();
        fragment.setContentView(view);
        RadioGroup rgp = view.findViewById(R.id.radio_group);
        assert rgp != null;
        switch (selectedIndex) {
            case 0:
                rgp.check(R.id.pick_0);
                break;
            case 1:
                rgp.check(R.id.pick_1);
                break;
            case 2:
                rgp.check(R.id.pick_2);
                break;
            case 3:
                rgp.check(R.id.pick_3);
                break;
            case 4:
                rgp.check(R.id.pick_4);
                break;
            case 5:
                rgp.check(R.id.pick_5);
                break;
            case 6:
                rgp.check(R.id.pick_6);
                break;
            case 7:
                rgp.check(R.id.pick_7);
                break;
            case 8:
                rgp.check(R.id.pick_8);
                break;
            case 9:
                rgp.check(R.id.pick_9);
                break;
            default:
                break;
        }
        TextView titleV = view.findViewById(R.id.title);
        if (titleV != null)
            titleV.setText(title);
        TextView pick_0 = rgp.findViewById(R.id.pick_0);
        TextView pick_1 = rgp.findViewById(R.id.pick_1);
        TextView pick_2 = rgp.findViewById(R.id.pick_2);
        TextView pick_3 = rgp.findViewById(R.id.pick_3);
        TextView pick_4 = rgp.findViewById(R.id.pick_4);
        TextView pick_5 = rgp.findViewById(R.id.pick_5);
        TextView pick_6 = rgp.findViewById(R.id.pick_6);
        TextView pick_7 = rgp.findViewById(R.id.pick_7);
        TextView pick_8 = rgp.findViewById(R.id.pick_8);
        TextView pick_9 = rgp.findViewById(R.id.pick_9);
        if (pick_0 != null)
            pick_0.setText(array[0]);
        if (pick_1 != null)
            pick_1.setText(array[1]);
        if (pick_2 != null)
            pick_2.setText(array[2]);
        if (pick_3 != null)
            pick_3.setText(array[3]);
        if (pick_4 != null)
            pick_4.setText(array[4]);
        if (pick_5 != null)
            pick_5.setText(array[5]);
        if (pick_6 != null)
            pick_6.setText(array[6]);
        if (pick_7 != null)
            pick_7.setText(array[7]);
        if (pick_8 != null)
            pick_8.setText(array[8]);
        if (pick_9 != null)
            pick_9.setText(array[9]);
        rgp.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.pick_0:
                    pickListener.onIndexPick(0);
                    break;
                case R.id.pick_1:
                    pickListener.onIndexPick(1);
                    break;
                case R.id.pick_2:
                    pickListener.onIndexPick(2);
                    break;
                case R.id.pick_3:
                    pickListener.onIndexPick(3);
                    break;
                case R.id.pick_4:
                    pickListener.onIndexPick(4);
                    break;
                case R.id.pick_5:
                    pickListener.onIndexPick(5);
                    break;
                case R.id.pick_6:
                    pickListener.onIndexPick(6);
                    break;
                case R.id.pick_7:
                    pickListener.onIndexPick(7);
                    break;
                case R.id.pick_8:
                    pickListener.onIndexPick(8);
                    break;
                case R.id.pick_9:
                    pickListener.onIndexPick(9);
                    break;
                default:
                    break;
            }
            fragment.dismiss();
        });
        fragment.show(fm, "tagId=" + layoutResId);
    }
}