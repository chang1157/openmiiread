//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.view.popupwindow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.tabs.TabLayout;
import com.moses.miiread.R;
import com.moses.miiread.help.ReadBookControl;
import com.moses.miiread.utils.FileUtils;
import com.moses.miiread.utils.StringUtils;
import com.moses.miiread.utils.TimeUtils;
import com.moses.miiread.view.activity.ReadBookActivity;
import com.moses.miiread.view.activity.ReadStyleActivity;
import com.moses.miiread.view.adapter.BgRvAdapter;
import com.moses.miiread.view.adapter.FontRvAdapter;
import com.moses.miiread.widget.font.FontSelector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadInterfacePopMoses extends FrameLayout {
    private ReadBookActivity activity;
    private ReadBookControl readBookControl = ReadBookControl.getInstance();
    private OnChangeProListener changeProListener;

    @BindView(R.id.textlayout)
    View textLayout;
    @BindView(R.id.toneLayout)
    View toneLayout;
    @BindView(R.id.paddingLayout)
    View paddingLayout;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.night_start_btn)
    TextView nightStartBtn;
    @BindView(R.id.night_off_btn)
    TextView nightOffBtn;
    @BindView(R.id.custom_night_light_switch)
    SwitchCompat customNightLightSwitch;
    @BindView(R.id.fontListV)
    RecyclerView fontListV;
    @BindView(R.id.bg_listV)
    RecyclerView bgListV;

    @BindView(R.id.brightness_btn)
    CheckBox brightnessBtn;
    @BindView(R.id.brightness_seek)
    SeekBar brightnessSeek;

    @BindView(R.id.text_smaller)
    View textSmaller;
    @BindView(R.id.text_bigger)
    View textBigger;
    @BindView(R.id.textSizePer)
    TextView textSizePer;
    @BindView(R.id.style_bold)
    View textStyleBold;
    @BindView(R.id.style_italic)
    View textStyleItalic;
    @BindView(R.id.textBold)
    TextView textBold;

    @BindView(R.id.linespacing_smaller)
    View lineSpacingSmaller;
    @BindView(R.id.linespacing_bigger)
    View lineSpacingBigger;
    @BindView(R.id.linespacingPer)
    TextView lineSpacingPer;

    @BindView(R.id.top_padding_increase)
    View topPaddingIncrease;
    @BindView(R.id.top_padding_decrease)
    View topPaddingDecrease;
    @BindView(R.id.top_padding_txt)
    TextView topPaddingTxt;
    @BindView(R.id.right_padding_increase)
    View rightPaddingIncrease;
    @BindView(R.id.right_padding_decrease)
    View rightPaddingDecrease;
    @BindView(R.id.right_padding_txt)
    TextView rightPaddingTxt;
    @BindView(R.id.bottom_padding_increase)
    View bottomPaddingIncrease;
    @BindView(R.id.bottom_padding_decrease)
    View bottomPaddingDecrease;
    @BindView(R.id.bottom_padding_txt)
    TextView bottomPaddingTxt;
    @BindView(R.id.left_padding_increase)
    View leftPaddingIncrease;
    @BindView(R.id.left_padding_decrease)
    View leftPaddingDecrease;
    @BindView(R.id.left_padding_txt)
    TextView leftPaddingTxt;
    @BindView(R.id.padding_icon_inside)
    View paddingIconInside;

    public ReadInterfacePopMoses(Context context) {
        super(context);
        init(context);
    }

    public ReadInterfacePopMoses(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadInterfacePopMoses(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_read_interface_moses, null);
        addView(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(null);
    }

    public void setListener(ReadBookActivity readBookActivity, @NonNull OnChangeProListener changeProListener) {
        this.activity = readBookActivity;
        this.changeProListener = changeProListener;
        initData();
        bindEvent();
        initLight();
        initCustomNightLightSwitch();
        updateNightLightBtns();
        updateCustomLightIcon();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        updateBg(readBookControl.getTextDrawableIndex());
        updateTextSize();
        updateLineSpacing();
        updateFontV();
        updateBgV();
        updatePagePadding();
    }

    /**
     * 控件事件
     */
    @SuppressLint("SetTextI18n")
    private void bindEvent() {
        this.setOnClickListener(v -> {
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        textLayout.setVisibility(VISIBLE);
                        paddingLayout.setVisibility(GONE);
                        toneLayout.setVisibility(GONE);
                        break;
                    case 1:
                        textLayout.setVisibility(GONE);
                        paddingLayout.setVisibility(GONE);
                        toneLayout.setVisibility(VISIBLE);
                        int textDrawableIndex = ReadBookControl.getInstance().getTextDrawableIndex();
                        if (textDrawableIndex != -1)
                            bgListV.scrollToPosition(textDrawableIndex);
                        break;
                    case 2:
                        textLayout.setVisibility(GONE);
                        paddingLayout.setVisibility(VISIBLE);
                        toneLayout.setVisibility(GONE);
                        updatePaddingViewer();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();

        nightStartBtn.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                readBookControl.setCustomNightLightOnHour24(hourOfDay);
                readBookControl.setCustomNightLightOnMin(minute);
                updateNightLightBtns();
            }, readBookControl.getCustomNightLightOnHour24(),
                    readBookControl.getCustomNightLightOnMin(), true);
            dialog.show();
        });
        nightOffBtn.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                readBookControl.setCustomNightLightOffHour24(hourOfDay);
                readBookControl.setCustomNightLightOffMin(minute);
                updateNightLightBtns();
            }, readBookControl.getCustomNightLightOffHour24(),
                    readBookControl.getCustomNightLightOffMin(), true);
            dialog.show();
        });
        customNightLightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!activity.isNightTheme()) {
                    if (TimeUtils.isInNightTime()) {
                        changeProListener.turnOnNightAtSwitchOn();
                        readBookControl.setCustomNightLightOn(true);
                    } else {
                        readBookControl.setCustomNightLightOn(true);
                        updateNightLightBtns();
                    }
                } else {
                    if (!TimeUtils.isInNightTime()) {
                        changeProListener.turnOffNightAtSwitchOff();
                        readBookControl.setCustomNightLightOn(true);
                    } else {
                        readBookControl.setCustomNightLightOn(true);
                        updateNightLightBtns();
                    }
                }
            } else {
                readBookControl.setCustomNightLightOn(false);
                updateNightLightBtns();
            }
        });
        textSmaller.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getTextSizePercent();
            if (percent == ReadBookControl.SizePercent.p13)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setTextSizePercent(prevPercent);
                changeProListener.upTextSize();
                Float sizePercent = prevPercent.getPercent() * 100;
                textSizePer.setText(sizePercent.intValue() + "%");
            }
        });
        textBigger.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getTextSizePercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setTextSizePercent(nextPercent);
                changeProListener.upTextSize();
                Float sizePercent = nextPercent.getPercent() * 100;
                textSizePer.setText(sizePercent.intValue() + "%");
            }
        });
        lineSpacingSmaller.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getLineSpacingPercent();
            if (percent == ReadBookControl.SizePercent.p75)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setLineSpacingPercent(prevPercent);
                changeProListener.upTextSize();
                Float spacingPercent = prevPercent.getPercent() * 100;
                lineSpacingPer.setText(spacingPercent.intValue() + "%");
            }
        });
        lineSpacingBigger.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getLineSpacingPercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setLineSpacingPercent(nextPercent);
                changeProListener.upTextSize();
                Float spacingPercent = nextPercent.getPercent() * 100;
                lineSpacingPer.setText(spacingPercent.intValue() + "%");
            }
        });
        leftPaddingDecrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingLeftPercent();
            if (percent == ReadBookControl.SizePercent.p0)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingLeftPercent(prevPercent);
                changeProListener.upMargin();
                Float paddingPercent = prevPercent.getPercent() * 100;
                leftPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        leftPaddingIncrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingLeftPercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingLeftPercent(nextPercent);
                changeProListener.upMargin();
                Float paddingPercent = nextPercent.getPercent() * 100;
                leftPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        rightPaddingDecrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingRightPercent();
            if (percent == ReadBookControl.SizePercent.p0)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingRightPercent(prevPercent);
                changeProListener.upMargin();
                Float paddingPercent = prevPercent.getPercent() * 100;
                rightPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        rightPaddingIncrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingRightPercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingRightPercent(nextPercent);
                changeProListener.upMargin();
                Float paddingPercent = nextPercent.getPercent() * 100;
                rightPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        bottomPaddingDecrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingBottomPercent();
            if (percent == ReadBookControl.SizePercent.p0)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingBottomPercent(prevPercent);
                changeProListener.upMargin();
                Float paddingPercent = prevPercent.getPercent() * 100;
                bottomPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        bottomPaddingIncrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingBottomPercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingBottomPercent(nextPercent);
                changeProListener.upMargin();
                Float paddingPercent = nextPercent.getPercent() * 100;
                bottomPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        topPaddingDecrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingTopPercent();
            if (percent == ReadBookControl.SizePercent.p0)
                return;
            ReadBookControl.SizePercent prevPercent = percent.getSmaller();
            if (prevPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingTopPercent(prevPercent);
                changeProListener.upMargin();
                Float paddingPercent = prevPercent.getPercent() * 100;
                topPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        topPaddingIncrease.setOnClickListener(v -> {
            ReadBookControl.SizePercent percent = readBookControl.getPaddingTopPercent();
            ReadBookControl.SizePercent nextPercent = percent.getBigger();
            if (nextPercent != ReadBookControl.SizePercent.invalid) {
                readBookControl.setPaddingTopPercent(nextPercent);
                changeProListener.upMargin();
                Float paddingPercent = nextPercent.getPercent() * 100;
                topPaddingTxt.setText(paddingPercent.intValue() + "%");
                updatePaddingViewer();
            }
        });
        brightnessBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                brightnessBtn.setButtonDrawable(R.drawable.ic_brightness_auto_24dp);
                brightnessSeek.setEnabled(false);
                setScreenBrightness();
            } else {
                brightnessSeek.setEnabled(true);
                setScreenBrightness(readBookControl.getLight());
                updateCustomLightIcon();
            }
            readBookControl.setLightFollowSys(isChecked);
        });
        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!readBookControl.getLightFollowSys()) {
                    readBookControl.setLight(progress);
                    setScreenBrightness(progress);
                    updateCustomLightIcon();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textBold.setText(getContext().getResources().getString(readBookControl.getTextBold() ? R.string.text_bold_enable : R.string.text_bold_disable));
        textStyleBold.setOnClickListener(v -> {
            readBookControl.setTextBold(!readBookControl.getTextBold());
            changeProListener.refresh();
            textBold.setText(getContext().getResources().getString(readBookControl.getTextBold() ? R.string.text_bold_enable : R.string.text_bold_disable));
        });
        textStyleItalic.setOnClickListener(v -> {
            readBookControl.setTextItalic(!readBookControl.getTextItalic());
            changeProListener.refresh();
        });
    }

    //更新编剧设置的示意图
    private void updatePaddingViewer() {
        int leftPadding = readBookControl.getPaddingLeft();
        int rightPadding = readBookControl.getPaddingRight();
        int topPadding = readBookControl.getPaddingTop();
        int bottomPadding = readBookControl.getPaddingBottom();
        paddingIconInside.setPivotX(0);
        paddingIconInside.setPivotY(0);
        float w = paddingIconInside.getWidth(), h = paddingIconInside.getHeight();
        if (w == 0 || h == 0) {
            ViewTreeObserver vto = paddingIconInside.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    paddingIconInside.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float w = paddingIconInside.getMeasuredWidth(), h = paddingIconInside.getMeasuredHeight();
                    if (w == 0 || h == 0)
                        return;
                    float scaleX = (w - leftPadding - rightPadding) / w;
                    float scaleY = (h - topPadding - bottomPadding) / h;
                    paddingIconInside.setScaleX(scaleX);
                    paddingIconInside.setScaleY(scaleY);
                    paddingIconInside.setTranslationX(leftPadding);
                    paddingIconInside.setTranslationY(topPadding);
                }
            });
        } else {
            paddingIconInside.setTranslationX(leftPadding);
            paddingIconInside.setTranslationY(topPadding);
            float scaleX = (w - leftPadding - rightPadding) / w;
            float scaleY = (h - topPadding - bottomPadding) / h;
            paddingIconInside.setScaleX(scaleX);
            paddingIconInside.setScaleY(scaleY);
        }
    }

    //设置字体
    public void setReadFonts(String path) {
        readBookControl.setReadBookFont(path);
        changeProListener.refresh();
    }

    //清除字体
    private void clearFontPath() {
        readBookControl.setReadBookFont(null);
        changeProListener.refresh();
    }

    @SuppressLint("SetTextI18n")
    private void updateTextSize() {
        ReadBookControl.SizePercent percent = readBookControl.getTextSizePercent();
        Float sizePercent = percent.getPercent() * 100;
        textSizePer.setText(sizePercent.intValue() + "%");
    }

    @SuppressLint("SetTextI18n")
    private void updateLineSpacing() {
        ReadBookControl.SizePercent percent = readBookControl.getLineSpacingPercent();
        Float spacingPercent = percent.getPercent() * 100;
        lineSpacingPer.setText(spacingPercent.intValue() + "%");
    }

    @SuppressLint("SetTextI18n")
    private void updatePagePadding() {
        topPaddingTxt.setText(((Float) (readBookControl.getPaddingTopPercent().getPercent() * 100)).intValue() + "%");
        leftPaddingTxt.setText(((Float) (readBookControl.getPaddingLeftPercent().getPercent() * 100)).intValue() + "%");
        bottomPaddingTxt.setText(((Float) (readBookControl.getPaddingBottomPercent().getPercent() * 100)).intValue() + "%");
        rightPaddingTxt.setText(((Float) (readBookControl.getPaddingRightPercent().getPercent() * 100)).intValue() + "%");
    }

    private void updateBg(int index) {
        readBookControl.setTextDrawableIndex(index);
    }

    public void setScreenBrightness() {
        WindowManager.LayoutParams params = (activity).getWindow().getAttributes();
        params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        (activity).getWindow().setAttributes(params);
    }

    public void setScreenBrightness(int value) {
        if (value < 1) value = 1;
        WindowManager.LayoutParams params = (activity).getWindow().getAttributes();
        params.screenBrightness = value * 1.0f / 255f;
        (activity).getWindow().setAttributes(params);
    }

    private void initLight() {
        brightnessSeek.setProgress(readBookControl.getLight());
        brightnessBtn.setChecked(readBookControl.getLightFollowSys());
        if (!readBookControl.getLightFollowSys()) {
            setScreenBrightness(readBookControl.getLight());
        }
    }

    private void initCustomNightLightSwitch() {
        customNightLightSwitch.setChecked(readBookControl.getCustomNightLightOn());
    }

    private void updateNightLightBtns() {
        if (!readBookControl.getCustomNightLightOn()) {
            nightStartBtn.setEnabled(false);
            nightOffBtn.setEnabled(false);
        } else {
            nightStartBtn.setEnabled(true);
            nightOffBtn.setEnabled(true);
        }
        nightStartBtn.setText(String.format(getResources().getString(R.string.tone_night_on_time_fmt),
                StringUtils.numberConvert00(readBookControl.getCustomNightLightOnHour24()),
                StringUtils.numberConvert00(readBookControl.getCustomNightLightOnMin())));
        nightOffBtn.setText(String.format(getResources().getString(R.string.tone_night_off_time_fmt),
                StringUtils.numberConvert00(readBookControl.getCustomNightLightOffHour24()),
                StringUtils.numberConvert00(readBookControl.getCustomNightLightOffMin())));
    }

    private void updateCustomLightIcon() {
        if (readBookControl.getLightFollowSys())
            return;
        int brightness = readBookControl.getLight();
        if (brightness <= ReadBookControl.BrightnessLevel.low.val) {
            //low
            brightnessBtn.setButtonDrawable(R.drawable.ic_brightness_low_24dp);
        } else if (brightness < ReadBookControl.BrightnessLevel.high.val) {
            //middle
            brightnessBtn.setButtonDrawable(R.drawable.ic_brightness_middle_24dp);
        } else if (brightness >= ReadBookControl.BrightnessLevel.high.val) {
            //high
            brightnessBtn.setButtonDrawable(R.drawable.ic_brightness_high_24dp);
        }
    }

    private void updateFontV() {
        File fontFolder = new File(FileUtils.getSdCardPath() + "/Fonts");
        List<FontRvAdapter.FontFile> fontFileList = new ArrayList<>();
        File[] fonts;
        if (!fontFolder.exists() || !fontFolder.isDirectory()) {
            fonts = new File[0];
        } else {
            FontSelector fs = new FontSelector(activity, readBookControl.getFontPath());
            fonts = fs.getFontFiles();
        }
        fontFileList.add(new FontRvAdapter.FontFile(FontRvAdapter.FontAction.resetFont, null));
        for (File font : fonts)
            fontFileList.add(new FontRvAdapter.FontFile(FontRvAdapter.FontAction.setFont, font));
        fontFileList.add(new FontRvAdapter.FontFile(FontRvAdapter.FontAction.addFont, null));
        FontRvAdapter.FontFile[] fontFiles = new FontRvAdapter.FontFile[fontFileList.size()];
        for (int i = 0; i < fontFileList.size(); i++)
            fontFiles[i] = fontFileList.get(i);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        fontListV.setLayoutManager(layoutManager);
        FontRvAdapter[] fontAdapter = new FontRvAdapter[1];
        fontAdapter[0] = new FontRvAdapter(activity, fontFiles).setOnFontActionItemClick((fontFile, posi) -> {
            switch (fontFile.fontAction) {
                case resetFont:
                    clearFontPath();
                    fontAdapter[0].notifyDataSetChanged();
                    break;
                case setFont:
                    setReadFonts(fontFile.font.getAbsolutePath());
                    fontAdapter[0].notifyDataSetChanged();
                    break;
                case addFont:
                    showAddFontDlg();
                    break;
                default:
                    break;
            }
            fontListV.scrollToPosition(posi);
        });
        fontListV.setAdapter(fontAdapter[0]);
        String path = readBookControl.getFontPath();
        int scrollTo = -1;
        for (int i = 0; i < fontFiles.length; i++) {
            FontRvAdapter.FontFile ffile = fontFiles[i];
            if (ffile != null) {
                File file = ffile.font;
                if (file != null && path != null && file.getAbsolutePath().compareTo(new File(path).getAbsolutePath()) == 0) {
                    scrollTo = i;
                    break;
                }
            }
        }
        if (scrollTo != -1)
            fontListV.scrollToPosition(scrollTo);
    }

    public void updateBgV() {
        //  背景颜色：       白   -   黄   -   黑   -   自选颜色   -   自选图片   -   默认
        //                  0   -    1   -   2    -     3      -      4      -    5
        BgRvAdapter.BgEle[] bgEles = new BgRvAdapter.BgEle[4];
        bgEles[0] = new BgRvAdapter.BgEle(BgRvAdapter.BgAction.setBgClr0, "白", R.drawable.bg_tone_bg_white, getResources().getColor(R.color.tone_text_white), false);
        bgEles[1] = new BgRvAdapter.BgEle(BgRvAdapter.BgAction.setBgClr1, "黄", R.drawable.bg_tone_bg_yellow, getResources().getColor(R.color.tone_text_yellow), false);
        bgEles[2] = new BgRvAdapter.BgEle(BgRvAdapter.BgAction.setBgClr2, "黑", R.drawable.bg_tone_bg_black, getResources().getColor(R.color.tone_text_black), false);
        int txtClrCustom = readBookControl.getTextColor(3);
        bgEles[3] = new BgRvAdapter.BgEle(BgRvAdapter.BgAction.setCustom, "自定义", null, txtClrCustom, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        bgListV.setLayoutManager(layoutManager);
        BgRvAdapter[] bgAdapter = new BgRvAdapter[1];
        int textDrawableIndex = ReadBookControl.getInstance().getTextDrawableIndex();
        for (int i = 0; i < bgEles.length; i++) {
            if (i == textDrawableIndex)
                bgEles[i].isChecked = true;
        }
        bgAdapter[0] = new BgRvAdapter(activity, bgEles).setOnBgActionItemClick((bgEle, posi) -> {
            if (bgEle.bgAction != BgRvAdapter.BgAction.setCustom || readBookControl.getCustomToneSetted() && !bgEle.isChecked) {
                updateBg(posi);
                changeProListener.bgChange();
                for (int i = 0; i < bgEles.length; i++) {
                    if (i != posi && bgEles[i].isChecked) {
                        bgEles[i].isChecked = false;
                        bgAdapter[0].notifyItemChanged(i);
                    }
                }
                bgEles[posi].isChecked = true;
                bgAdapter[0].notifyItemChanged(posi);
            } else if (bgEle.bgAction == BgRvAdapter.BgAction.setCustom && ((readBookControl.getCustomToneSetted() && bgEle.isChecked) || !readBookControl.getCustomToneSetted())) {
                Intent intent = new Intent(activity, ReadStyleActivity.class);
                intent.putExtra("index", posi);
                activity.startActivityByAnim(intent, 0, 0);
            }
            bgListV.scrollToPosition(posi);
        });
        bgListV.setAdapter(bgAdapter[0]);
        if (textDrawableIndex != -1)
            bgListV.scrollToPosition(textDrawableIndex);
    }

    private void showAddFontDlg() {
        //放入书架
        Dialog[] dialog = new Dialog[1];
        View pop = LayoutInflater.from(activity).inflate(R.layout.dialog_normal_confirm, null, false);
        TextView title = pop.findViewById(R.id.title);
        TextView msg = pop.findViewById(R.id.msg_tv);
        TextView cancel = pop.findViewById(R.id.cancel);
        TextView confirm = pop.findViewById(R.id.confirm);
        title.setText(R.string.add_fonts);
        msg.setText(R.string.add_font_tip);
        cancel.setVisibility(INVISIBLE);
        confirm.setText(R.string.know_it);

        cancel.setOnClickListener(view -> dialog[0].dismiss());
        confirm.setOnClickListener(view -> dialog[0].dismiss());
        dialog[0] = new AlertDialog.Builder(activity, R.style.alertDialogTheme)
                .setView(pop)
                .show();
    }

    public interface OnChangeProListener {
        void upTextSize();

        void upMargin();

        void bgChange();

        void refresh();

        void turnOnNightAtSwitchOn();

        void turnOffNightAtSwitchOff();
    }
}