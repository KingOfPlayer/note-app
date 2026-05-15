package com.note_app.app.ui.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class PlaceholderImageSpan extends ImageSpan {
    private final Drawable drawable;

    public PlaceholderImageSpan(Drawable drawable) { super(drawable); this.drawable = drawable; }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Rect bounds = drawable.getBounds();

        if (fm != null) {
            fm.ascent  = -bounds.height();
            fm.descent = 0;
            fm.top     = fm.ascent;
            fm.bottom  = 0;
        }

        return bounds.width();
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int baseline, int bottom, Paint paint) {
        canvas.save();
        canvas.translate(x, top);
        drawable.draw(canvas);
        canvas.restore();
    }
}
