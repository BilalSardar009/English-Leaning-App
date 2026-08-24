package com.saim.englishlearning.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** An animated circular progress ring with a label in the middle. */
public class RingProgressView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint captionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bounds = new RectF();

    private float sweep = 0f;
    private String label = "0%";
    private String caption = "";
    private ValueAnimator animator;

    public RingProgressView(Context context) {
        super(context);
        setup();
    }

    public RingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public RingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        float density = getResources().getDisplayMetrics().density;
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(10 * density);
        trackPaint.setColor(Color.parseColor("#22FFFFFF"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(10 * density);
        progressPaint.setColor(Color.parseColor("#FFD166"));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(22 * density);
        textPaint.setFakeBoldText(true);

        captionPaint.setColor(Color.parseColor("#CCFFFFFF"));
        captionPaint.setTextAlign(Paint.Align.CENTER);
        captionPaint.setTextSize(11 * density);
    }

    public void setRingColour(int colour) {
        progressPaint.setColor(colour);
        invalidate();
    }

    public void setCaption(String caption) {
        this.caption = caption == null ? "" : caption;
        invalidate();
    }

    /** Animates from wherever the ring is now to the given percentage. */
    public void setPercent(int percent, String label) {
        final int clamped = Math.max(0, Math.min(100, percent));
        this.label = label == null ? clamped + "%" : label;
        if (animator != null) animator.cancel();
        animator = ValueAnimator.ofFloat(sweep, clamped * 3.6f);
        animator.setDuration(900);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweep = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float inset = progressPaint.getStrokeWidth() / 2f + 2f;
        bounds.set(inset, inset, getWidth() - inset, getHeight() - inset);
        canvas.drawArc(bounds, 0, 360, false, trackPaint);
        canvas.drawArc(bounds, -90, sweep, false, progressPaint);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        if (caption.isEmpty()) {
            canvas.drawText(label, cx, cy + textPaint.getTextSize() / 3f, textPaint);
        } else {
            canvas.drawText(label, cx, cy, textPaint);
            canvas.drawText(caption, cx, cy + captionPaint.getTextSize() * 1.6f, captionPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }
}
