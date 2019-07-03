//Copyright (c) 2017. origin. All rights reserved.
package com.moses.miiread.help;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import com.moses.miiread.MApplication;
import com.moses.miiread.R;
import com.moses.miiread.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadBookControl {
    private static final int DEFAULT_BG = 1;
    private int textDrawableIndex = DEFAULT_BG;
    private List<Map<String, Integer>> textDrawable;
    private Bitmap bgBitmap;
    private int screenDirection;
    private int speechRate;
    private boolean speechRateFollowSys;
    private SizePercent textSizePercent = SizePercent.p100;
    private SizePercent lineSpacingPercent = SizePercent.p100;
    private SizePercent paddingTopPercent = SizePercent.p13;
    private SizePercent paddingRightPercent = SizePercent.p13;
    private SizePercent paddingBottomPercent = SizePercent.p13;
    private SizePercent paddingLeftPercent = SizePercent.p13;
    private int textColor;
    private boolean bgIsColor;
    private int bgColor;
    private float paragraphSize;
    private int pageMode;
    private Boolean hideStatusBar;
    private Boolean hideNavigationBar;
    private String fontPath;
    private int textConvert;
    private int navBarColor;
    private Boolean textBold;
    private Boolean textItalic;
    private Boolean canClickTurn;
    private int clickSensitivity;
    private Boolean showTitle;
    private Boolean showTimeBattery;
    private Boolean showLine;
    private Boolean darkStatusIcon;
    private int indent;
    private int screenTimeOut;
    private Boolean tipMarginChange;
    private Boolean downloadWifiOnly;
    private Boolean localPdfTxtEnable;
    private Boolean fixBottomNav;

    private int clickMode;
    private int volumeKeyTurnPageMode;

    private SharedPreferences preferences;

    private static ReadBookControl readBookControl;
    private static final int DEFAULT_TEXT_SIZE_PX = 20;
    private static final int DEFAULT_LINE_SPACING = 1;
    private static final int DEFAULT_HORIZONTAL_PADDING = 40;
    private static final int DEFAULT_VERTICAL_PADDING = 40;

    private static final int DEFAULT_CUSTOM_NIGHT_LIGHT_ON_HOUR24 = 22;
    private static final int DEFAULT_CUSTOM_NIGHT_LIGHT_OFF_HOUR24 = 6;
    private static final int DEFAULT_CUSTOM_NIGHT_LIGHT_ON_MIN = 0;
    private static final int DEFAULT_CUSTOM_NIGHT_LIGHT_OFF_MIN = 0;

    public enum BrightnessLevel {
        low(30),
        high(70);
        public int val;

        BrightnessLevel(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }
    }

    public enum SizePercent {
        p0(0 / 100f, 0 / 100f, 13 / 100f),
        p13(13 / 100f, 0 / 100f, 25 / 100f),
        p25(25 / 100f, 13 / 100f, 38 / 100f),
        p38(38 / 100f, 25 / 100f, 50 / 100f),
        p50(50 / 100f, 38 / 100f, 63 / 100f),
        p63(63 / 100f, 50 / 100f, 75 / 100f),
        p75(75 / 100f, 63 / 100f, 88 / 100f),
        p88(88 / 100f, 75 / 100f, 100 / 100f),
        p100(100 / 100f, 88 / 100f, 113 / 100f),
        p113(113 / 100f, 100 / 100f, 125 / 100f),
        p125(125 / 100f, 113 / 100f, 138 / 100f),
        p138(138 / 100f, 125 / 100f, 150 / 100f),
        p150(150 / 100f, 138 / 100f, 163 / 100f),
        p163(163 / 100f, 150 / 100f, 175 / 100f),
        p175(175 / 100f, 163 / 100f, 188 / 100f),
        p188(188 / 100f, 175 / 100f, 200 / 100f),
        p200(200 / 100f, 188 / 100f, 213 / 100f),
        p213(213 / 100f, 200 / 100f, 225 / 100f),
        p225(225 / 100f, 213 / 100f, 238 / 100f),
        p238(238 / 100f, 225 / 100f, 250 / 100f),
        p250(250 / 100f, 238 / 100f, 263 / 100f),
        p263(263 / 100f, 250 / 100f, 275 / 100f),
        p275(275 / 100f, 263 / 100f, 288 / 100f),
        p288(288 / 100f, 275 / 100f, 300 / 100f),
        p300(300 / 100f, 288 / 100f, 313 / 100f),
        p313(313 / 100f, 300 / 100f, 325 / 100f),
        p325(325 / 100f, 313 / 100f, 338 / 100f),
        p338(338 / 100f, 325 / 100f, 350 / 100f),
        p350(350 / 100f, 338 / 100f, 363 / 100f),
        p363(363 / 100f, 350 / 100f, 375 / 100f),
        p375(375 / 100f, 363 / 100f, 388 / 100f),
        p388(388 / 100f, 375 / 100f, 400 / 100f),
        p400(400 / 100f, 388 / 100f, 400 / 100f),
        invalid(-1, -1, -1);
        float percent, prevPercent, nextPercent;

        SizePercent(float percent, float prevPercent, float nextPercent) {
            this.percent = percent;
            this.prevPercent = prevPercent;
            this.nextPercent = nextPercent;
        }

        public float getPercent() {
            return percent;
        }

        public static SizePercent getSizePercentByPercent(float percent) {
            for (SizePercent value : values()) {
                if (value.percent - percent == 0)
                    return value;
            }
            return p100;
        }

        public SizePercent getSmaller() {
            if (this.prevPercent - this.percent == 0)
                return invalid;
            return getSizePercentByPercent(this.prevPercent);
        }

        public SizePercent getBigger() {
            if (this.nextPercent - this.percent == 0)
                return invalid;
            return getSizePercentByPercent(this.nextPercent);
        }
    }

    public static ReadBookControl getInstance() {
        if (readBookControl == null) {
            synchronized (ReadBookControl.class) {
                if (readBookControl == null) {
                    readBookControl = new ReadBookControl();
                }
            }
        }
        return readBookControl;
    }

    private ReadBookControl() {
        preferences = MApplication.getConfigPreferences();
        initTextDrawable();
        updateReaderSettings();
    }

    public void updateReaderSettings() {
        this.hideStatusBar = preferences.getBoolean("hide_status_bar", false);
        this.hideNavigationBar = preferences.getBoolean("hide_navigation_bar", true);
        this.indent = preferences.getInt("indent", 2);
        this.textSizePercent = SizePercent.getSizePercentByPercent(preferences.getFloat("textSizePercent", SizePercent.p100.percent));
        this.canClickTurn = preferences.getBoolean("canClickTurn", true);
        this.lineSpacingPercent = SizePercent.getSizePercentByPercent(preferences.getFloat("lineSpacingPercent", SizePercent.p100.percent));
        this.paragraphSize = preferences.getFloat("paragraphSize", 2);
        this.clickSensitivity = preferences.getInt("clickSensitivity", 50) > 100
                ? 50 : preferences.getInt("clickSensitivity", 50);
        this.fontPath = preferences.getString("fontPath", null);
        this.textConvert = preferences.getInt("textConvertInt", 0);
        this.textBold = preferences.getBoolean("textBold", false);
        this.textItalic = preferences.getBoolean("textItalic", false);
        this.speechRate = preferences.getInt("speechRate", 10);
        this.speechRateFollowSys = preferences.getBoolean("speechRateFollowSys", true);
        this.showTitle = preferences.getBoolean("showTitle", true);
        this.showTimeBattery = preferences.getBoolean("showTimeBattery", true);
        this.showLine = preferences.getBoolean("showLine", false);
        this.fixBottomNav = preferences.getBoolean("fixBottomNav", false);
        this.screenTimeOut = preferences.getInt("screenTimeOut", 0);
        this.paddingTopPercent = SizePercent.getSizePercentByPercent(preferences.getFloat("paddingTopPercent", SizePercent.p25.percent));
        this.paddingLeftPercent = SizePercent.getSizePercentByPercent(preferences.getFloat("paddingLeftPercent", SizePercent.p25.percent));
        this.paddingRightPercent = SizePercent.getSizePercentByPercent(preferences.getFloat("paddingRightPercent", SizePercent.p25.percent));
        this.paddingBottomPercent = SizePercent.getSizePercentByPercent(preferences.getFloat("paddingBottomPercent", SizePercent.p25.percent));
        this.pageMode = preferences.getInt("pageMode", 0);
        this.screenDirection = preferences.getInt("screenDirection", 0);
        this.tipMarginChange = preferences.getBoolean("tipMarginChange", false);
        this.navBarColor = preferences.getInt("navBarColorInt", 0);
        this.downloadWifiOnly = preferences.getBoolean("downloadWifiOnly", false);
        this.localPdfTxtEnable = preferences.getBoolean("localPdfTxtEnable", true);
        this.clickMode = preferences.getInt("clickMode", 0);
        this.volumeKeyTurnPageMode = preferences.getInt("volumeKeyTurnPageMode", 1);

        initTextDrawableIndex();
    }

    //阅读背景
    private void initTextDrawable() {
        if (null == textDrawable) {
            textDrawable = new ArrayList<>();
            Map<String, Integer> temp1 = new HashMap<>();
            temp1.put("textColor", MApplication.getInstance().getResources().getColor(R.color.tone_text_white));
            temp1.put("bgIsColor", 1);
            temp1.put("textBackground", MApplication.getInstance().getResources().getColor(R.color.tone_bg_white_unpressed));
            temp1.put("darkStatusIcon", 1);
            textDrawable.add(temp1);//

            Map<String, Integer> temp2 = new HashMap<>();
            temp2.put("textColor", MApplication.getInstance().getResources().getColor(R.color.tone_text_yellow));
            temp2.put("bgIsColor", 1);
            temp2.put("textBackground", MApplication.getInstance().getResources().getColor(R.color.tone_bg_yellow_unpressed));
            temp2.put("darkStatusIcon", 1);
            textDrawable.add(temp2);//

            Map<String, Integer> temp3 = new HashMap<>();
            temp3.put("textColor", MApplication.getInstance().getResources().getColor(R.color.tone_text_black));
            temp3.put("bgIsColor", 1);
            temp3.put("textBackground", MApplication.getInstance().getResources().getColor(R.color.tone_bg_black_unpressed));
            temp3.put("darkStatusIcon", 0);
            textDrawable.add(temp3);//

            Map<String, Integer> temp4 = new HashMap<>();
            temp4.put("textColor", MApplication.getInstance().getResources().getColor(R.color.tone_text_yellow));
            temp4.put("bgIsColor", 1);
            temp4.put("textBackground", getBgColor(3));
            temp4.put("darkStatusIcon", 1);
            textDrawable.add(temp4);//
        }
    }

    public void initTextDrawableIndex() {
        if (getIsNightTheme()) {
            textDrawableIndex = preferences.getInt("textDrawableIndexNight", 2);
        } else {
            textDrawableIndex = preferences.getInt("textDrawableIndex", DEFAULT_BG);
        }
        if (textDrawableIndex > 3)
            textDrawableIndex = DEFAULT_BG;
        if (textDrawableIndex == -1) {
            textDrawableIndex = DEFAULT_BG;
        }
        initPageStyle();
        setTextDrawable();
    }

    @SuppressWarnings("ConstantConditions")
    private void initPageStyle() {
        if (getBgCustom(textDrawableIndex) == 2 && getBgPath(textDrawableIndex) != null) {
            bgIsColor = false;
            String bgPath = getBgPath(textDrawableIndex);
            Resources resources = MApplication.getInstance().getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            bgBitmap = BitmapUtil.getFitSampleBitmap(bgPath, width, height);
            if (bgBitmap != null) {
                return;
            }
        } else if (getBgCustom(textDrawableIndex) == 1) {
            bgIsColor = true;
            bgColor = getBgColor(textDrawableIndex);
            return;
        }
        bgIsColor = true;
        bgColor = textDrawable.get(textDrawableIndex).get("textBackground");
    }

    public void setFixBottomNav(Boolean fixBottomNav) {
        preferences.edit()
                .putBoolean("fixBottomNav", fixBottomNav)
                .apply();
    }

    public Boolean getFixBottomNav() {
        return preferences.getBoolean("fixBottomNav", false);
    }

    private void setTextDrawable() {
        darkStatusIcon = getDarkStatusIcon(textDrawableIndex);
        textColor = getTextColor(textDrawableIndex);
    }

    public int getTextColor(int textDrawableIndex) {
        if (preferences.getInt("textColor" + textDrawableIndex, 0) != 0) {
            return preferences.getInt("textColor" + textDrawableIndex, 0);
        } else {
            return getDefaultTextColor(textDrawableIndex);
        }
    }

    public Boolean getCustomToneSetted() {
        return preferences.getBoolean("customToneSetted", false);
    }

    public void setCustomToneSetted(Boolean customToneSetted) {
        preferences.edit()
                .putBoolean("customToneSetted", customToneSetted)
                .apply();
    }

    public void setTextColor(int textDrawableIndex, int textColor) {
        preferences.edit()
                .putInt("textColor" + textDrawableIndex, textColor)
                .apply();
    }

    @SuppressWarnings("ConstantConditions")
    public Drawable getBgDrawable(int textDrawableIndex, Context context, int width, int height) {
        int color;
        try {
            switch (getBgCustom(textDrawableIndex)) {
                case 2:
                    Bitmap bitmap = BitmapUtil.getFitSampleBitmap(getBgPath(textDrawableIndex), width, height);
                    if (bitmap != null) {
                        return new BitmapDrawable(context.getResources(), bitmap);
                    }
                    break;
                case 1:
                    color = getBgColor(textDrawableIndex);
                    return new ColorDrawable(color);
                default:
                    return new ColorDrawable();
            }
            if (textDrawable.get(textDrawableIndex).get("bgIsColor") != 0) {
                color = textDrawable.get(textDrawableIndex).get("textBackground");
                return new ColorDrawable(color);
            } else {
                return getDefaultBgDrawable(textDrawableIndex, context);
            }
        } catch (Exception e) {
            if (textDrawable.get(textDrawableIndex).get("bgIsColor") != 0) {
                color = textDrawable.get(textDrawableIndex).get("textBackground");
                return new ColorDrawable(color);
            } else {
                return getDefaultBgDrawable(textDrawableIndex, context);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Drawable getDefaultBgDrawable(int textDrawableIndex, Context context) {
        if (textDrawable.get(textDrawableIndex).get("bgIsColor") != 0) {
            return new ColorDrawable(textDrawable.get(textDrawableIndex).get("textBackground"));
        } else {
            return context.getResources().getDrawable(getDefaultBg(textDrawableIndex));
        }
    }

    public int getBgCustom(int textDrawableIndex) {
        return preferences.getInt("bgCustom" + textDrawableIndex, 0);
    }

    public void setBgCustom(int textDrawableIndex, int bgCustom) {
        preferences.edit()
                .putInt("bgCustom" + textDrawableIndex, bgCustom)
                .apply();
    }

    public String getBgPath(int textDrawableIndex) {
        return preferences.getString("bgPath" + textDrawableIndex, null);
    }

    public void setBgPath(int textDrawableIndex, String bgUri) {
        preferences.edit()
                .putString("bgPath" + textDrawableIndex, bgUri)
                .apply();
    }

    @SuppressWarnings("ConstantConditions")
    public int getDefaultTextColor(int textDrawableIndex) {
        return textDrawable.get(textDrawableIndex).get("textColor");
    }

    @SuppressWarnings("ConstantConditions")
    private int getDefaultBg(int textDrawableIndex) {
        return textDrawable.get(textDrawableIndex).get("textBackground");
    }

    public int getBgColor(int index) {
        return preferences.getInt("bgColor" + index, Color.WHITE);
    }

    public void setBgColor(int index, int bgColor) {
        preferences.edit()
                .putInt("bgColor" + index, bgColor)
                .apply();
    }

    public boolean getIsNightTheme() {
        return preferences.getBoolean("nightTheme", false);
    }

    public boolean getImmersionStatusBar() {
        return preferences.getBoolean("immersionStatusBar", true);
    }

    public void setImmersionStatusBar(boolean immersionStatusBar) {
        preferences.edit()
                .putBoolean("immersionStatusBar", immersionStatusBar)
                .apply();
    }

    public int getTextSize() {
        return (int) (DEFAULT_TEXT_SIZE_PX * textSizePercent.percent);
    }

    public void setTextSizePercent(SizePercent sizePercent) {
        this.textSizePercent = sizePercent;
        preferences.edit()
                .putFloat("textSizePercent", sizePercent.percent)
                .apply();
    }

    public SizePercent getTextSizePercent() {
        return textSizePercent;
    }

    public float getLineSpacing() {
        return DEFAULT_LINE_SPACING * lineSpacingPercent.percent;
    }

    public void setLineSpacingPercent(SizePercent sizePercent) {
        this.lineSpacingPercent = sizePercent;
        preferences.edit()
                .putFloat("lineSpacingPercent", sizePercent.percent)
                .apply();
    }

    public SizePercent getLineSpacingPercent() {
        return lineSpacingPercent;
    }

    public int getTextColor() {
        return textColor;
    }

    public boolean bgIsColor() {
        return bgIsColor;
    }

    public Drawable getTextBackground(Context context) {
        if (bgIsColor) {
            return new ColorDrawable(bgColor);
        }
        return new BitmapDrawable(context.getResources(), bgBitmap);
    }

    public int getBgColor() {
        return bgColor;
    }

    public boolean bgBitmapIsNull() {
        return bgBitmap == null || bgBitmap.isRecycled();
    }

    public Bitmap getBgBitmap() {
        return bgBitmap.copy(Bitmap.Config.RGB_565, true);
    }

    public int getTextDrawableIndex() {
        return textDrawableIndex;
    }

    public void setTextDrawableIndex(int textDrawableIndex) {
        this.textDrawableIndex = textDrawableIndex;
        if (getIsNightTheme()) {
            preferences.edit()
                    .putInt("textDrawableIndexNight", textDrawableIndex)
                    .apply();
        } else {
            preferences.edit()
                    .putInt("textDrawableIndex", textDrawableIndex)
                    .apply();
        }
        setTextDrawable();
    }

    public void setTextConvert(int textConvert) {
        this.textConvert = textConvert;
        preferences.edit()
                .putInt("textConvertInt", textConvert)
                .apply();
    }

    public void setNavBarColor(int navBarColor) {
        this.navBarColor = navBarColor;
        preferences.edit()
                .putInt("navBarColorInt", navBarColor)
                .apply();
    }

    public int getNavBarColor() {
        return navBarColor;
    }

    public void setTextBold(boolean textBold) {
        this.textBold = textBold;
        preferences.edit()
                .putBoolean("textBold", textBold)
                .apply();
    }

    public void setTextItalic(boolean textItalic) {
        this.textBold = textItalic;
        preferences.edit()
                .putBoolean("textItalic", textItalic)
                .apply();
    }

    public Boolean getTextItalic() {
        return textItalic;
    }

    public void setReadBookFont(String fontPath) {
        this.fontPath = fontPath;
        preferences.edit()
                .putString("fontPath", fontPath)
                .apply();
    }

    public String getFontPath() {
        return fontPath;
    }

    public int getTextConvert() {
        return textConvert;
    }

    public boolean getTextBold() {
        return textBold;
    }

    public Boolean getCanKeyTurn(Boolean isPlay) {
        if (volumeKeyTurnPageMode == 0) {
            return false;
        } else if (volumeKeyTurnPageMode == 1 && !isPlay) {
            return true;
        } else
            return volumeKeyTurnPageMode == 2;
    }

    public Boolean getDownloadWifiOnly() {
        return downloadWifiOnly;
    }

    public void setDownloadWifiOnly(Boolean downloadWifiOnly) {
        this.downloadWifiOnly = downloadWifiOnly;
        preferences.edit()
                .putBoolean("downloadWifiOnly", downloadWifiOnly)
                .apply();
    }

    public Boolean getLocalPdfTxtEnable() {
        return localPdfTxtEnable;
    }

    public void setLocalPdfTxtEnable(Boolean localPdfTxtEnable) {
        this.localPdfTxtEnable = localPdfTxtEnable;
        preferences.edit()
                .putBoolean("localPdfTxtEnable", localPdfTxtEnable)
                .apply();
    }

    public Boolean getCanClickTurn() {
        return canClickTurn;
    }

    public void setCanClickTurn(Boolean canClickTurn) {
        this.canClickTurn = canClickTurn;
        preferences.edit()
                .putBoolean("canClickTurn", canClickTurn)
                .apply();
    }

    public float getParagraphSize() {
        return paragraphSize;
    }

    public void setParagraphSize(float paragraphSize) {
        this.paragraphSize = paragraphSize;
        preferences.edit()
                .putFloat("paragraphSize", paragraphSize)
                .apply();
    }

    public int getClickSensitivity() {
        return clickSensitivity;
    }

    public void setClickSensitivity(int clickSensitivity) {
        this.clickSensitivity = clickSensitivity;
        preferences.edit()
                .putInt("clickSensitivity", clickSensitivity)
                .apply();
    }

    public int getSpeechRate() {
        return speechRate;
    }

    public void setSpeechRate(int speechRate) {
        this.speechRate = speechRate;
        preferences.edit()
                .putInt("speechRate", speechRate)
                .apply();
    }

    public boolean isSpeechRateFollowSys() {
        return speechRateFollowSys;
    }

    public void setSpeechRateFollowSys(boolean speechRateFollowSys) {
        this.speechRateFollowSys = speechRateFollowSys;
        preferences.edit()
                .putBoolean("speechRateFollowSys", speechRateFollowSys)
                .apply();
    }

    public Boolean getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(Boolean showTitle) {
        this.showTitle = showTitle;
        preferences.edit()
                .putBoolean("showTitle", showTitle)
                .apply();
    }

    public Boolean getShowTimeBattery() {
        return showTimeBattery;
    }

    public void setShowTimeBattery(Boolean showTimeBattery) {
        this.showTimeBattery = showTimeBattery;
        preferences.edit()
                .putBoolean("showTimeBattery", showTimeBattery)
                .apply();
    }

    public Boolean getHideStatusBar() {
        return hideStatusBar;
    }

    public void setHideStatusBar(Boolean hideStatusBar) {
        this.hideStatusBar = hideStatusBar;
        preferences.edit()
                .putBoolean("hide_status_bar", hideStatusBar)
                .apply();
    }

    public Boolean getHideNavigationBar() {
        return hideNavigationBar;
    }

    public void setHideNavigationBar(Boolean hideNavigationBar) {
        this.hideNavigationBar = hideNavigationBar;
        preferences.edit()
                .putBoolean("hide_navigation_bar", hideNavigationBar)
                .apply();
    }

    public Boolean getShowLine() {
        return showLine;
    }

    public void setShowLine(Boolean showLine) {
        this.showLine = showLine;
        preferences.edit()
                .putBoolean("showLine", showLine)
                .apply();
    }

    public boolean getDarkStatusIcon() {
        return darkStatusIcon;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean getDarkStatusIcon(int textDrawableIndex) {
        return preferences.getBoolean("darkStatusIcon" + textDrawableIndex, textDrawable.get(textDrawableIndex).get("darkStatusIcon") != 0);
    }

    public void setDarkStatusIcon(int textDrawableIndex, Boolean darkStatusIcon) {
        preferences.edit()
                .putBoolean("darkStatusIcon" + textDrawableIndex, darkStatusIcon)
                .apply();
    }

    public int getScreenTimeOut() {
        return screenTimeOut;
    }

    public void setScreenTimeOut(int screenTimeOut) {
        this.screenTimeOut = screenTimeOut;
        preferences.edit()
                .putInt("screenTimeOut", screenTimeOut)
                .apply();
    }

    public int getPaddingLeft() {
        return (int) (DEFAULT_HORIZONTAL_PADDING * paddingLeftPercent.percent);
    }

    public SizePercent getPaddingLeftPercent() {
        return paddingLeftPercent;
    }

    public void setPaddingLeftPercent(SizePercent paddingLeftPercent) {
        this.paddingLeftPercent = paddingLeftPercent;
        preferences.edit()
                .putFloat("paddingLeftPercent", paddingLeftPercent.percent)
                .apply();
    }

    public int getPaddingTop() {
        return (int) (DEFAULT_VERTICAL_PADDING * paddingTopPercent.percent);
    }

    public SizePercent getPaddingTopPercent() {
        return paddingTopPercent;
    }

    public void setPaddingTopPercent(SizePercent paddingTopPercent) {
        this.paddingTopPercent = paddingTopPercent;
        preferences.edit()
                .putFloat("paddingTopPercent", paddingTopPercent.percent)
                .apply();
    }

    public int getPaddingRight() {
        return (int) (DEFAULT_HORIZONTAL_PADDING * paddingRightPercent.percent);
    }

    public SizePercent getPaddingRightPercent() {
        return paddingRightPercent;
    }

    public void setPaddingRightPercent(SizePercent paddingRightPercent) {
        this.paddingRightPercent = paddingRightPercent;
        preferences.edit()
                .putFloat("paddingRightPercent", paddingRightPercent.percent)
                .apply();
    }

    public int getPaddingBottom() {
        return (int) (DEFAULT_VERTICAL_PADDING * paddingBottomPercent.percent);
    }

    public SizePercent getPaddingBottomPercent() {
        return paddingBottomPercent;
    }

    public void setPaddingBottomPercent(SizePercent paddingBottomPercent) {
        this.paddingBottomPercent = paddingBottomPercent;
        preferences.edit()
                .putFloat("paddingBottomPercent", paddingBottomPercent.percent)
                .apply();
    }

    public int getPageMode() {
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
        preferences.edit()
                .putInt("pageMode", pageMode)
                .apply();
    }

    public int getClickMode() {
        return clickMode;
    }

    public void setClickMode(int clickMode) {
        this.clickMode = clickMode;
        preferences.edit().putInt("clickMode", clickMode).apply();
    }

    public int getVolumeKeyTurnPageMode() {
        return volumeKeyTurnPageMode;
    }

    public void setVolumeKeyTurnPageMode(int volumeKeyTurnPageMode) {
        this.volumeKeyTurnPageMode = volumeKeyTurnPageMode;
        preferences.edit().putInt("volumeKeyTurnPageMode", volumeKeyTurnPageMode).apply();
    }

    public int getScreenDirection() {
        return screenDirection;
    }

    public void setScreenDirection(int screenDirection) {
        this.screenDirection = screenDirection;
        preferences.edit()
                .putInt("screenDirection", screenDirection)
                .apply();
    }

    public Boolean getTipMarginChange() {
        return tipMarginChange;
    }

    public void setTipMarginChange(Boolean tipMarginChange) {
        this.tipMarginChange = tipMarginChange;
        preferences.edit()
                .putBoolean("tipMarginChange", tipMarginChange)
                .apply();
    }

    public void setIndent(int indent) {
        this.indent = indent;
        preferences.edit()
                .putInt("indent", indent)
                .apply();
    }

    public int getIndent() {
        return indent;
    }

    public int getLight() {
        return preferences.getInt("light", getScreenBrightness());
    }

    public void setLight(int light) {
        preferences.edit()
                .putInt("light", light)
                .apply();
    }

    public Boolean getLightFollowSys() {
        return preferences.getBoolean("isfollowsys", true);
    }

    public void setLightFollowSys(boolean isFollowSys) {
        preferences.edit()
                .putBoolean("isfollowsys", isFollowSys)
                .apply();
    }

    public Boolean getNightLightFollowSys() {
        return preferences.getBoolean("isNightLightFollowSys", true);
    }

    public void setNightLightFollowSys(boolean isFollowSys) {
        preferences.edit()
                .putBoolean("isNightLightFollowSys", isFollowSys)
                .apply();
    }

    /**
     * @return 自定义夜间-白天模式开启时间（24h制式）
     */
    public int getCustomNightLightOnHour24() {
        return preferences.getInt("customNightLightOnHour24", DEFAULT_CUSTOM_NIGHT_LIGHT_ON_HOUR24);
    }

    /**
     * 设置夜间-白天模式开启时间（24h制式）
     */
    public void setCustomNightLightOnHour24(int hour24) {
        preferences.edit()
                .putInt("customNightLightOnHour24", hour24)
                .apply();
    }

    /**
     * @return 自定义夜间-白天模式关闭时间（24h制式）
     */
    public int getCustomNightLightOffHour24() {
        return preferences.getInt("customNightLightOffHour24", DEFAULT_CUSTOM_NIGHT_LIGHT_OFF_HOUR24);
    }

    /**
     * 设置夜间-白天模式关闭时间（24h制式）
     */
    public void setCustomNightLightOffHour24(int hour24) {
        preferences.edit()
                .putInt("customNightLightOffHour24", hour24)
                .apply();
    }

    public int getCustomNightLightOnMin() {
        return preferences.getInt("customNightLightOnMin", DEFAULT_CUSTOM_NIGHT_LIGHT_ON_MIN);
    }

    public void setCustomNightLightOnMin(int min) {
        preferences.edit()
                .putInt("customNightLightOnMin", min)
                .apply();
    }

    public int getCustomNightLightOffMin() {
        return preferences.getInt("customNightLightOffMin", DEFAULT_CUSTOM_NIGHT_LIGHT_OFF_MIN);
    }

    public void setCustomNightLightOffMin(int min) {
        preferences.edit()
                .putInt("customNightLightOffMin", min)
                .apply();
    }

    public Boolean getCustomNightLightOn() {
        return preferences.getBoolean("customNightLightOn", false);
    }

    public void setCustomNightLightOn(boolean customNightLightOn) {
        preferences.edit()
                .putBoolean("customNightLightOn", customNightLightOn)
                .apply();
    }

    private int getScreenBrightness() {
        int value = 0;
        ContentResolver cr = MApplication.getInstance().getContentResolver();
        try {
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
