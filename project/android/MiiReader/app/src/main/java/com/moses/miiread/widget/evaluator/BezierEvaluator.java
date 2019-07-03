package com.moses.miiread.widget.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Point;

public class BezierEvaluator implements TypeEvaluator<Point> {

    private Point controlPoint;

    public BezierEvaluator(Point controlPoint) {
        this.controlPoint = controlPoint;
    }

    @Override
    public Point evaluate(float t, Point startValue, Point endValue) {
        int x = (int) ((1 - t) * (1 - t) * startValue.x + 2 * t * (1 - t) * controlPoint.x + t * t * endValue.x);
        int y = (int) ((1 - t) * (1 - t) * startValue.y + 2 * t * (1 - t) * controlPoint.y + t * t * endValue.y);
        return new Point(x, y);
    }
}