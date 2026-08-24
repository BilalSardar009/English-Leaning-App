package com.saim.englishlearning.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

/** Small shared animation helpers so every screen feels alive. */
public final class Anim {

    private Anim() {
    }

    /** Card style entrance: fades and lifts a view into place. */
    public static void enter(View view, long delayMs) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setTranslationY(60f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(420)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /** Staggers {@link #enter(View, long)} across a row of views. */
    public static void enterAll(long step, View... views) {
        if (views == null) return;
        long delay = 0;
        for (View v : views) {
            enter(v, delay);
            delay += step;
        }
    }

    /** A quick springy pop, used for correct answers and taps. */
    public static void pop(View view) {
        if (view == null) return;
        view.animate().cancel();
        view.setScaleX(0.85f);
        view.setScaleY(0.85f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(320)
                .setInterpolator(new OvershootInterpolator(3.2f))
                .start();
    }

    /** A short horizontal shake, used for wrong answers. */
    public static void shake(final View view) {
        if (view == null) return;
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(380);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                float offset = (float) (Math.sin(t * Math.PI * 4) * 18 * (1 - t));
                view.setTranslationX(offset);
            }
        });
        animator.start();
    }

    /** Gentle continuous pulse for a call-to-action. */
    public static void pulse(final View view) {
        if (view == null) return;
        view.animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setDuration(700)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(700)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        pulse(view);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    /** Counts a number up so scores land with a bit of drama. */
    public static void countUp(final TextView view, final int from, final int to, final String suffix) {
        if (view == null) return;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(700);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setText(animation.getAnimatedValue() + (suffix == null ? "" : suffix));
            }
        });
        animator.start();
    }

    /** Flips a card around its vertical axis, swapping content halfway. */
    public static void flip(final View view, final Runnable swapContent) {
        if (view == null) return;
        view.animate()
                .rotationY(90f)
                .setDuration(180)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (swapContent != null) swapContent.run();
                        view.setRotationY(-90f);
                        view.animate()
                                .rotationY(0f)
                                .setDuration(180)
                                .start();
                    }
                })
                .start();
    }

    /** Fades a view out and hides it. */
    public static void fadeOut(final View view) {
        if (view == null) return;
        view.animate()
                .alpha(0f)
                .setDuration(220)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /** Shows a view with a fade in. */
    public static void fadeIn(View view) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(260).start();
    }
}
