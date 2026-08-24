package com.saim.englishlearning.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Draws a line of text where each letter bounces in turn, like a row of balls.
 * Used on the splash screen, so the app needs no image asset at all.
 */
public class BouncingTextView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Interpolator easing = new AccelerateDecelerateInterpolator();

    private String text = "";
    private float[] widths = new float[0];
    private float totalWidth;

    /** How far a letter lifts, and how long one full cycle takes. */
    private float hopHeight;
    private long cycleMillis = 1500L;
    private long letterOffset = 90L;

    private long startTime;
    private boolean running;

    public BouncingTextView(Context context) {
        super(context);
        setup(null);
    }

    public BouncingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public BouncingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        paint.setColor(Color.WHITE);
        paint.setTextSize(34 * density);
        paint.setFakeBoldText(true);
        hopHeight = 18 * density;
    }

    public void setText(String value) {
        text = value == null ? "" : value;
        measureLetters();
        requestLayout();
        invalidate();
    }

    public void setTextColour(int colour) {
        paint.setColor(colour);
        invalidate();
    }

    public void setTextSizePx(float px) {
        paint.setTextSize(px);
        measureLetters();
        requestLayout();
        invalidate();
    }

    private void measureLetters() {
        widths = new float[text.length()];
        totalWidth = 0f;
        for (int i = 0; i < text.length(); i++) {
            widths[i] = paint.measureText(text, i, i + 1);
            totalWidth += widths[i];
        }
    }

    public void start() {
        if (running) return;
        running = true;
        startTime = android.os.SystemClock.uptimeMillis();
        invalidate();
    }

    public void stop() {
        running = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize((int) Math.ceil(totalWidth) + getPaddingLeft() + getPaddingRight(),
                widthMeasureSpec);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int height = resolveSize(
                (int) Math.ceil(fm.bottom - fm.top + hopHeight * 2) + getPaddingTop() + getPaddingBottom(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text.isEmpty()) return;
        if (totalWidth == 0f) measureLetters();

        // Shrink to fit rather than spilling off a narrow screen.
        float usable = getWidth() - getPaddingLeft() - getPaddingRight();
        float scale = (totalWidth > usable && totalWidth > 0) ? usable / totalWidth : 1f;

        long now = android.os.SystemClock.uptimeMillis();
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseline = getHeight() / 2f - (fm.ascent + fm.descent) / 2f + hopHeight / 2f;
        float x = (getWidth() - totalWidth * scale) / 2f;

        for (int i = 0; i < text.length(); i++) {
            float lift = 0f;
            if (running && text.charAt(i) != ' ') {
                long elapsed = now - startTime - i * letterOffset;
                if (elapsed > 0) {
                    float phase = (elapsed % cycleMillis) / (float) cycleMillis;
                    // One hop occupies the first third of the cycle; then it rests.
                    if (phase < 0.34f) {
                        float t = phase / 0.34f;
                        float curve = 1f - Math.abs(2f * t - 1f);
                        lift = easing.getInterpolation(curve) * hopHeight;
                    }
                }
            }
            canvas.save();
            canvas.scale(scale, scale, x, baseline);
            canvas.drawText(text, i, i + 1, x, baseline - lift, paint);
            canvas.restore();
            x += widths[i] * scale;
        }

        if (running) invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        running = false;
        super.onDetachedFromWindow();
    }
}
