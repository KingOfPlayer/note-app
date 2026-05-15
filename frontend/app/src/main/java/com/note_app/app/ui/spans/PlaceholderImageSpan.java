package com.note_app.app.ui.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class PlaceholderImageSpan extends ImageSpan {
    public PlaceholderImageSpan(Drawable d, int verticalAlignment) { super(d, verticalAlignment); }
}
