package com.rayse;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by colby on 4/16/16.
 */
public class SunProgressView extends View {
    private final int SIDES = 10;
    private final int SHADOW_RADIUS = 24;

    Paint trianglePaint, circlePaint, glowPaint;
    Path path, sunPath, glow;
    ProgressFillView progressFill;
    int radius, centerX, centerY, circleRadius, triangleRadiusSmall, triangleRadiusLarge, apart;
    double percent = 0.0;

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public SunProgressView(Context context) {
        super(context);
        create();
        constructPolygon();
    }

    public SunProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SunProgressView,
                0, 0);

        try {
            centerX = a.getInt(R.styleable.SunProgressView_centerX, 300);
            centerY = a.getInt(R.styleable.SunProgressView_centerY, 300);
            radius = a.getInt(R.styleable.SunProgressView_radius, 200);
        } finally {
            a.recycle();
        }
        constructPolygon();
    }

    void constructPolygon() {
        circleRadius = (int) (2/3.0 * radius);
        int difference = radius - circleRadius;
        triangleRadiusLarge = (int) (3/4.0 * difference);
        triangleRadiusSmall= (int) (1/2.0 * difference);
        apart = (int) (circleRadius + 1/4.0 * difference);

        int[] polygonX = new int[SIDES];
        int[] polygonY = new int[SIDES];

        for (int side = 0; side < SIDES; side++) {
            double idk = 2 * Math.PI * side / SIDES + Math.toRadians(360.0/SIDES * side);
            // I don't know either
            idk /= 2;
            polygonX[side] = (int) (circleRadius * Math.cos(idk) + centerX);
            polygonY[side] = (int) (circleRadius * Math.sin(idk) + centerY);
        }

        Path polygonPath = new Path();
        polygonPath.moveTo(polygonX[0], polygonY[0]);
        for (int i = 1; i < polygonX.length; i++) {
            polygonPath.lineTo(polygonX[i], polygonY[i]);
        }
        this.sunPath = polygonPath;
    }

    public void redraw(int centerX, int centerY, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        constructPolygon();
        invalidate();
    }

    private void create() {
        trianglePaint= new Paint();
        trianglePaint.setStyle(Paint.Style.FILL);
        trianglePaint.setColor(ContextCompat.getColor(getContext(), R.color.orange));
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(ContextCompat.getColor(getContext(), R.color.amber));

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
        glowPaint.setColor(ContextCompat.getColor(getContext(), R.color.orange));
        glowPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, ContextCompat.getColor(getContext(), R.color.white));
        setLayerType(LAYER_TYPE_SOFTWARE, glowPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Point tipLarge = new Point(centerX - triangleRadiusLarge/2 , centerY - apart);
        Point tipSmall= new Point(centerX - triangleRadiusSmall/2 , centerY - apart);
        Point center = new Point(centerX, centerY);
        int glowIters = (int) Math.round(percent * 10);

        for (int ang = 0, i = 0; ang < 360; ang += 360/SIDES, i++) {
            // Make every other triangle smaller
            if (ang % 72 == 36) {
                path = trianglePath(tipSmall, center, triangleRadiusSmall, ang);
            } else {
                path = trianglePath(tipLarge, center, triangleRadiusLarge, ang);
            }
            if (i < glowIters) {
                canvas.drawPath(path, glowPaint);
            } else {
                canvas.drawPath(path, trianglePaint);
            }
        }
        canvas.drawPath(sunPath, circlePaint);
    }

    public static class ProgressFillView extends View {
        SunProgressView progress;
        Path mask;
        int radius;
        Paint rectPaint;
        Point center;
        double percent = 0.0;
        final int orange = ContextCompat.getColor(getContext(), R.color.orange);
        final int orangeAlpha = ContextCompat.getColor(getContext(), R.color.orangeAlpha);

        public ProgressFillView(Context context, SunProgressView progress, Path mask, Point center, int radius) {
            super(context);
            this.progress = progress;
            this.mask = mask;
            this.radius = radius;
            this.center = center;
            rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.clipPath(mask);
            super.onDraw(canvas);
            int top = center.y + radius;
            top -= (int) (percent * 2 * radius);
            float x1 = center.x - radius;
            float y1 = top;
            float x2 = center.x + radius;
            float y2 = center.y + radius;
            rectPaint.setShader(new LinearGradient(0, y1, 0, y2, orangeAlpha, orange, Shader.TileMode.MIRROR));
            canvas.drawRect(x1, y1, x2, y2, rectPaint);
        }

        // Method causes a redraw of the containing SunProgressView
        public void setPercent(double percent) {
            this.percent = percent;
            progress.percent = this.percent;
            progress.invalidate();
            invalidate();
        }
    }

    private Path trianglePath(Point p1, Point center, int width, int angle) {
        Point p2 = new Point(p1.x + width, p1.y);
        Point p3 = new Point(p1.x + (width / 2), p1.y - width);

        Path path = new Path();
        p1 = rotatePointAbout(p1, center, angle);
        p2 = rotatePointAbout(p2, center, angle);
        p3 = rotatePointAbout(p3, center, angle);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }

    // http://stackoverflow.com/questions/2259476/rotating-a-point-about-another-point-2d
    private Point rotatePointAbout(Point p, Point center,float degrees)
    {
        double angle = Math.toRadians(degrees);
        return new Point((int) (Math.cos(angle) * (p.x - center.x) - Math.sin(angle) * (p.y - center.y) + center.x),
                (int) (Math.sin(angle) * (p.x - center.x) + Math.cos(angle) * (p.y - center.y) + center.y));
    }
}
