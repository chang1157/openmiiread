package com.moses.miiread.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;
import com.moses.miiread.R;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * @create by origin at 2019/5/6
 */

public class MenuButton extends AppCompatButton {
    public MenuButton(Context context) {
        this(context, null);
    }

    public MenuButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.MenuButton);
        tintColor = ta.getColor(R.styleable.MenuButton_tintColor, getResources().getColor(R.color.menu_button));
        int[] argb = new int[4];
        argb[0] = Color.alpha(tintColor);
        argb[1] = Color.red(tintColor);
        argb[2] = Color.green(tintColor);
        argb[3] = Color.blue(tintColor);
        tintColor = Color.argb(255, argb[0], argb[1], argb[2]);
        eleHeight = ta.getDimensionPixelSize(R.styleable.MenuButton_eleHeight, 12);
        gapRate = ta.getFloat(R.styleable.MenuButton_gapRate, 2);
        duration = ta.getInteger(R.styleable.MenuButton_aniDuration, 400);
        ta.recycle();
        init();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(tintColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(eleHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        gap = (int) ((height - 2 * eleHeight) / (2f + gapRate));
        icWidth = (int) (width * icWidthRate);

        setOnClickListener(v -> {
            if (isAnimating.get())
                return;
            if (isOpened.get()) {
                close();
                if (onMenuButtonClickListener != null)
                    onMenuButtonClickListener.onClose();
            } else {
                open();
                if (onMenuButtonClickListener != null)
                    onMenuButtonClickListener.onOpen();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float transY = (int) (gapRate * gap * 0.5f * openRate);
        float angel = (int) (45 * openRate);
        canvas.save();
        matrix.reset();
        matrix.postTranslate((width - icWidth) / 2f, gap + eleHeight + transY);
        matrix.postRotate(angel, width / 2f, gap + eleHeight + transY);
        canvas.concat(matrix);
        canvas.drawLine(0, 0, icWidth, 0, paint);
        canvas.restore();
        canvas.save();
        matrix.reset();
        matrix.postTranslate((width - icWidth) / 2f, (gapRate + 1) * gap + eleHeight - transY);
        matrix.postRotate(-angel, width / 2f, (1 + gapRate) * gap + eleHeight - transY);
        canvas.concat(matrix);
        canvas.drawLine(0, 0, icWidth, 0, paint);
        canvas.restore();
    }

    public void open() {
        if(isAnimating.get())
            return;
        if (openAnimator == null) {
            openAnimator = ValueAnimator.ofFloat(0, 1);
            openAnimator.setDuration(400);
            openAnimator.addUpdateListener(animation -> {
                openRate = animation.getAnimatedFraction();
                if (openRate <= 0) {
                    isOpened.set(false);
                    isAnimating.set(false);
                } else if (openRate >= 1) {
                    isOpened.set(true);
                    isAnimating.set(false);
                } else
                    isAnimating.set(true);
                invalidate();
            });
        }
        openAnimator.start();
    }

    public void close() {
        if(isAnimating.get())
            return;
        if (openAnimator != null)
            openAnimator.reverse();
    }

    public interface OnMenuButtonClickListener {
        void onOpen();

        void onClose();
    }

    public void setOnMenuButtonClickListener(OnMenuButtonClickListener onMenuButtonClickListener) {
        this.onMenuButtonClickListener = onMenuButtonClickListener;
    }

    public OnMenuButtonClickListener getOnMenuButtonClickListener() {
        return onMenuButtonClickListener;
    }

    private OnMenuButtonClickListener onMenuButtonClickListener;
    private AtomicBoolean isOpened = new AtomicBoolean(false);
    private AtomicBoolean isAnimating = new AtomicBoolean(false);

    Paint paint;
    int width, height;
    int tintColor;
    int eleHeight;
    //中间gap间距相对顶（底）部间距的倍数
    final float gapRate;
    //顶部/底部gap间距
    int gap;
    //水平宽度比例
    final float icWidthRate = 0.6f;
    //水平宽度
    int icWidth;
    //当前打开比例 0 ~ 1
    float openRate = 0;
    ValueAnimator openAnimator;
    Matrix matrix = new Matrix();

    long duration;
}