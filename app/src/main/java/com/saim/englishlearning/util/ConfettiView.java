package com.saim.englishlearning.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A lightweight confetti burst. It draws its own particles, so there is no
 * external library and nothing to download.
 */
public class ConfettiView extends View {

    private static final int[] COLOURS = {
            Color.parseColor("#FF6B6B"), Color.parseColor("#FFD166"),
            Color.parseColor("#06D6A0"), Color.parseColor("#4D96FF"),
            Color.parseColor("#C77DFF"), Color.parseColor("#FF9F1C")
    };

    private static class Particle {
        float x, y, vx, vy, size, rotation, spin;
        int colour;
        boolean circle;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Random random = new Random();
    private ValueAnimator animator;

    public ConfettiView(Context context) {
        super(context);
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfettiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** Fires a burst from the top of the view. Safe to call repeatedly. */
    public void burst() {
        burst(90);
    }

    public void burst(int count) {
        if (getWidth() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    burst();
                }
            });
            return;
        }
        particles.clear();
        float width = getWidth();
        for (int i = 0; i < count; i++) {
            Particle p = new Particle();
            p.x = width * random.nextFloat();
            p.y = -random.nextFloat() * getHeight() * 0.4f;
            p.vx = (random.nextFloat() - 0.5f) * 7f;
            p.vy = 5f + random.nextFloat() * 9f;
            p.size = 12f + random.nextFloat() * 16f;
            p.rotation = random.nextFloat() * 360f;
            p.spin = (random.nextFloat() - 0.5f) * 16f;
            p.colour = COLOURS[random.nextInt(COLOURS.length)];
            p.circle = random.nextBoolean();
            particles.add(p);
        }
        setVisibility(VISIBLE);
        start();
    }

    private void start() {
        if (animator != null) animator.cancel();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                step();
                float t = (float) animation.getAnimatedValue();
                setAlpha(t > 0.75f ? (1f - t) * 4f : 1f);
                invalidate();
            }
        });
        animator.start();
    }

    private void step() {
        float height = getHeight();
        for (Particle p : particles) {
            p.x += p.vx;
            p.y += p.vy;
            p.vy += 0.28f;
            p.rotation += p.spin;
            if (p.y > height + 60) {
                p.y = height + 60;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Particle p : particles) {
            paint.setColor(p.colour);
            canvas.save();
            canvas.rotate(p.rotation, p.x, p.y);
            if (p.circle) {
                canvas.drawCircle(p.x, p.y, p.size / 2.4f, paint);
            } else {
                rect.set(p.x - p.size / 2f, p.y - p.size / 3f,
                        p.x + p.size / 2f, p.y + p.size / 3f);
                canvas.drawRoundRect(rect, 4f, 4f, paint);
            }
            canvas.restore();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }
}
