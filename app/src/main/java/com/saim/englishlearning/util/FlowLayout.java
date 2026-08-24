package com.saim.englishlearning.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A minimal wrapping layout: children are placed left to right and moved to a new
 * line when they no longer fit. Used for the word chips in the scramble game, so
 * the project does not need an extra layout library.
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int available = widthSize - getPaddingLeft() - getPaddingRight();

        int lineWidth = 0;
        int lineHeight = 0;
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int maxLineWidth = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (lineWidth + childWidth > available && lineWidth > 0) {
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
                totalHeight += lineHeight;
                lineWidth = 0;
                lineHeight = 0;
            }
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
        }
        maxLineWidth = Math.max(maxLineWidth, lineWidth);
        totalHeight += lineHeight;

        int resolvedWidth = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                ? widthSize
                : Math.min(widthSize, maxLineWidth + getPaddingLeft() + getPaddingRight());
        int resolvedHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                ? MeasureSpec.getSize(heightMeasureSpec)
                : totalHeight;

        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int available = getWidth() - getPaddingRight();
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            if (x + childWidth > available && x > getPaddingLeft()) {
                x = getPaddingLeft();
                y += lineHeight;
                lineHeight = 0;
            }

            int left = x + params.leftMargin;
            int top = y + params.topMargin;
            child.layout(left, top,
                    left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());

            x += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
        }
    }
}
