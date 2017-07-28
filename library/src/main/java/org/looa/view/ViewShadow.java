package org.looa.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.Field;

/**
 * add shadow for a view.
 * Created by ran on 2017/7/24.
 */

public class ViewShadow {

    private static final int SHADOW_RADIUS = 4;
    private static final int OFFSET_X = 0;
    private static final int TOP_DY = 3;
    private static final int DEFAULT_SHADOW_COLOR = Color.parseColor("#4a000000");

    /**
     * 设置阴影，默认颜色为#4a000000
     *
     * @param view      需要添加阴影的控件
     * @param elevation 高度
     */
    public static void setElevation(View view, float elevation) {
        setElevation(view, elevation, DEFAULT_SHADOW_COLOR);
    }

    /**
     * 设置带颜色的阴影
     *
     * @param view      需要添加阴影的控件
     * @param elevation 高度
     * @param color     控件颜色（要有透明度，例如：#40000000）
     */
    public static void setElevation(View view, float elevation, int color) {
        if (view == null) return;
        ShadowData data = new ShadowData();
        Context context = view.getContext().getApplicationContext();
        data.shadowRadius = elevation > 0 ? (int) elevation : SHADOW_RADIUS;
        data.dx = Math.abs(dip2px(context, OFFSET_X));
        data.dy = dip2px(context, data.shadowRadius / 6f);
        data.inner = dip2px(context, data.shadowRadius / 10f);
        data.top = dip2px(context, TOP_DY);
        data.top = data.top < data.dy ? data.top : 0;
        data.color = color;
        AddShadowRunnable runnable = new AddShadowRunnable(view, data);
        view.post(runnable);
        ViewTreeObserver vto = view.getViewTreeObserver();
        GlobalLayoutListener layoutListener = new GlobalLayoutListener(view, data);
        vto.addOnGlobalLayoutListener(layoutListener);
    }

    private static class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private View view;
        private ShadowData data;

        GlobalLayoutListener(View view, ShadowData data) {
            this.view = view;
            this.data = data;
        }

        @Override
        public void onGlobalLayout() {
            if (data.shadow == null) return;
            int top = view.getTop() - data.shadowRadius - data.dy;
            int left = view.getLeft() - data.shadowRadius - data.dx;
            data.shadow.setTranslationX(left);
            data.shadow.setTranslationY(top);
        }
    }


    private static class ShadowData {
        float[] cornerRadius;
        int shadowRadius;
        int dx;
        int dy;
        int inner;
        int color;
        int top;

        View shadow;
    }

    private static class AddShadowRunnable implements Runnable {

        private View view;
        private ShadowData data;

        AddShadowRunnable(View view, ShadowData data) {
            this.view = view;
            this.data = data;
        }

        @Override
        public void run() {
            int width = view.getWidth() + data.shadowRadius * 2 + data.dx * 2;
            int height = view.getHeight() + data.shadowRadius * 2 + data.dy * 2;

            int top = view.getTop() - data.shadowRadius - data.dy;
            int left = view.getLeft() - data.shadowRadius - data.dx;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            View vShadow = new View(view.getContext());
            data.shadow = vShadow;
            vShadow.setLayoutParams(layoutParams);
            vShadow.setTranslationX(left);
            vShadow.setTranslationY(top);

            Drawable drawable = view.getBackground();
            data.cornerRadius = obtainRadius(drawable);

            Bitmap bitmap = createShadowBitmap(
                    width, height,
                    data.cornerRadius,
                    data.shadowRadius,
                    data.dx,
                    data.dy,
                    data.top,
                    data.inner,
                    data.color,
                    Color.TRANSPARENT);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(view.getResources(), bitmap);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                vShadow.setBackgroundDrawable(bitmapDrawable);
            } else {
                vShadow.setBackground(bitmapDrawable);
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.addView(vShadow, parent.indexOfChild(view));
        }
    }

    private static float[] obtainRadius(Drawable background) {
        Drawable currentDrawable = background.getCurrent();
        if (currentDrawable instanceof GradientDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    float[] radii = ((GradientDrawable) currentDrawable).getCornerRadii();
                    if (radii == null) {
                        float radius = ((GradientDrawable) currentDrawable).getCornerRadius();
                        return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                    } else {
                        return radii;
                    }
                } catch (Exception e) {
                    float radius = ((GradientDrawable) currentDrawable).getCornerRadius();
                    return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                }
            } else {
                try {
                    Class c = Class.forName("android.graphics.drawable.GradientDrawable$GradientState");
                    Field mRadiusArray = c.getDeclaredField("mRadiusArray");
                    mRadiusArray.setAccessible(true);
                    float[] radii = (float[]) mRadiusArray.get(currentDrawable.getConstantState());
                    if (radii == null) {
                        Field mRadius = c.getDeclaredField("mRadius");
                        mRadius.setAccessible(true);
                        float radius = (float) mRadius.get(currentDrawable.getConstantState());
                        return new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
                    } else {
                        return radii;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private static Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float[] cornerRadius, float shadowRadius,
                                             float dx, float dy, int top, int inner, int shadowColor, int fillColor) {
        Bitmap.Config config = shadowColor == DEFAULT_SHADOW_COLOR ? Bitmap.Config.ALPHA_8 : Bitmap.Config.ARGB_8888;
        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, config);
        Canvas canvas = new Canvas(output);
        RectF shadowRect = new RectF(
                shadowRadius + inner,
                shadowRadius + Math.abs(dy) - top,
                shadowWidth - shadowRadius - inner,
                shadowHeight - shadowRadius);

        shadowRect.top += dy > 0 ? dy : -dy;
        shadowRect.bottom -= dy > 0 ? dy : -dy;

        shadowRect.left += dx > 0 ? dx : -dx;
        shadowRect.right -= dx > 0 ? dx : -dx;

        Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        if (cornerRadius == null) {
            canvas.drawRect(shadowRect, shadowPaint);
        } else {
            Path path = new Path();
            path.addRoundRect(shadowRect, cornerRadius, Path.Direction.CW);
            canvas.drawPath(path, shadowPaint);
        }
        return output;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
