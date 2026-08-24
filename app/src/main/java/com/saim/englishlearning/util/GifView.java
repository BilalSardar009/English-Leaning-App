package com.saim.englishlearning.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Movie;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * Plays an animated GIF from res/raw without any image library.
 *
 * <p>On Android 9 and newer it uses {@link AnimatedImageDrawable}, which is the
 * modern decoder. On Android 8 it falls back to {@link Movie}, which is older but
 * still works there. If the GIF cannot be decoded at all the view simply draws
 * nothing, so a bad or missing file never crashes the splash screen.
 */
public class GifView extends View {

    private Drawable animated;      // API 28+
    private Movie movie;            // API 26-27 fallback
    private long movieStart;
    private boolean failed;

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** Loads and starts a GIF from a raw resource. */
    public void setGifResource(int rawResId) {
        animated = null;
        movie = null;
        failed = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(getResources(), rawResId);
                Drawable drawable = ImageDecoder.decodeDrawable(source);
                drawable.setCallback(this);
                animated = drawable;
                if (drawable instanceof AnimatedImageDrawable) {
                    AnimatedImageDrawable aid = (AnimatedImageDrawable) drawable;
                    aid.setRepeatCount(AnimatedImageDrawable.REPEAT_INFINITE);
                    aid.start();
                }
                invalidate();
                return;
            } catch (Exception ignored) {
                animated = null;
            }
        }

        try {
            // Movie cannot draw on a hardware accelerated canvas.
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            InputStream stream = getResources().openRawResource(rawResId);
            movie = Movie.decodeStream(stream);
            stream.close();
            movieStart = 0;
            if (movie == null) failed = true;
            invalidate();
        } catch (Exception e) {
            failed = true;
        }
    }

    /** True when nothing could be decoded, so callers can show a fallback. */
    public boolean hasFailed() {
        return failed || (animated == null && movie == null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;

        if (animated != null) {
            drawScaled(canvas, animated.getIntrinsicWidth(), animated.getIntrinsicHeight());
            return;
        }
        if (movie == null) return;

        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) movieStart = now;
        int duration = movie.duration();
        if (duration <= 0) duration = 1000;
        movie.setTime((int) ((now - movieStart) % duration));

        float scale = fitScale(movie.width(), movie.height());
        canvas.save();
        canvas.translate((getWidth() - movie.width() * scale) / 2f,
                (getHeight() - movie.height() * scale) / 2f);
        canvas.scale(scale, scale);
        movie.draw(canvas, 0, 0);
        canvas.restore();
        invalidate();
    }

    private void drawScaled(Canvas canvas, int width, int height) {
        if (width <= 0 || height <= 0) {
            animated.setBounds(0, 0, getWidth(), getHeight());
            animated.draw(canvas);
            return;
        }
        float scale = fitScale(width, height);
        int w = Math.round(width * scale);
        int h = Math.round(height * scale);
        int left = (getWidth() - w) / 2;
        int top = (getHeight() - h) / 2;
        animated.setBounds(left, top, left + w, top + h);
        animated.draw(canvas);
    }

    /** Scales the image to fit inside the view without distorting it. */
    private float fitScale(int width, int height) {
        if (width <= 0 || height <= 0) return 1f;
        return Math.min(getWidth() / (float) width, getHeight() / (float) height);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == animated || super.verifyDrawable(who);
    }

    @Override
    protected void onDetachedFromWindow() {
        // The version check keeps the API 28 class off the verifier's path on
        // Android 8, where AnimatedImageDrawable does not exist.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                && animated instanceof AnimatedImageDrawable) {
            ((AnimatedImageDrawable) animated).stop();
        }
        super.onDetachedFromWindow();
    }
}
