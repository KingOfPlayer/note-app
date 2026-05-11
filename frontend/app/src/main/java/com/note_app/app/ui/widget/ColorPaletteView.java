package com.note_app.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPaletteView extends View {

    public interface OnColorSelectedListener {
        void onColorSelected(String hex);
    }

    private static final String[] PALETTE = new String[]{
            "#FFF59D", "#FFCC80", "#EF9A9A", "#CE93D8",
            "#90CAF9", "#A5D6A7", "#BCAAA4", "#E0E0E0"
    };

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;

    private int selectedIndex = 0;
    private OnColorSelectedListener listener;

    public ColorPaletteView(Context context) {
        this(context, null);
    }

    public ColorPaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getResources().getDisplayMetrics().density;
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(3 * density);
        ringPaint.setColor(Color.parseColor("#1A1A1A"));
        shadowPaint.setColor(Color.parseColor("#22000000"));
    }

    public void setOnColorSelectedListener(OnColorSelectedListener l) {
        this.listener = l;
    }

    public String getSelectedColor() {
        return PALETTE[selectedIndex];
    }

    public void setSelectedColor(String hex) {
        for (int i = 0; i < PALETTE.length; i++) {
            if (PALETTE[i].equalsIgnoreCase(hex)) {
                selectedIndex = i;
                invalidate();
                return;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = (int) (60 * density);
        setMeasuredDimension(width, resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = 8 * density;
        float available = getWidth() - 2 * padding;
        float step = available / PALETTE.length;
        float radius = Math.min(step, getHeight()) / 2 - 4 * density;
        float cy = getHeight() / 2f;

        for (int i = 0; i < PALETTE.length; i++) {
            float cx = padding + step * (i + 0.5f);
            canvas.drawCircle(cx, cy + 2 * density, radius, shadowPaint);
            fillPaint.setColor(Color.parseColor(PALETTE[i]));
            canvas.drawCircle(cx, cy, radius, fillPaint);
            if (i == selectedIndex) {
                canvas.drawCircle(cx, cy, radius + 4 * density, ringPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float padding = 8 * density;
            float available = getWidth() - 2 * padding;
            float step = available / PALETTE.length;
            int index = (int) ((event.getX() - padding) / step);
            if (index >= 0 && index < PALETTE.length) {
                selectedIndex = index;
                invalidate();
                if (listener != null) listener.onColorSelected(PALETTE[index]);
                performClick();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
