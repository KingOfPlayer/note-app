package com.note_app.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.note_app.app.model.Note;
import com.note_app.app.model.NoteContent;

public class NoteCardView extends View {

    private static final int DEFAULT_COLOR = Color.parseColor("#FFF59D");
    private static final int SHADOW_COLOR = Color.parseColor("#22000000");

    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint bodyPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint datePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private final RectF cardRect = new RectF();
    private final float density;

    private Note note;
    private String contentSummary = "";

    public NoteCardView(Context context) {
        this(context, null);
    }

    public NoteCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getResources().getDisplayMetrics().density;
        initPaints();
    }

    private void initPaints() {
        shadowPaint.setColor(SHADOW_COLOR);
        shadowPaint.setStyle(Paint.Style.FILL);

        cardPaint.setStyle(Paint.Style.FILL);

        pinPaint.setStyle(Paint.Style.FILL);
        pinPaint.setColor(Color.parseColor("#D32F2F"));

        titlePaint.setColor(Color.parseColor("#212121"));
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(18 * density);

        bodyPaint.setColor(Color.parseColor("#424242"));
        bodyPaint.setTextSize(14 * density);

        datePaint.setColor(Color.parseColor("#757575"));
        datePaint.setTextSize(11 * density);
    }

    public void setNote(Note note) {
        this.note = note;
        if (note != null && note.getColor() != null) {
            try {
                cardPaint.setColor(Color.parseColor(note.getColor()));
            } catch (IllegalArgumentException ex) {
                cardPaint.setColor(DEFAULT_COLOR);
            }
        } else {
            cardPaint.setColor(DEFAULT_COLOR);
        }
        if (note != null && note.getContent() != null) {
            NoteContent parsed = NoteContent.parse(note.getContent());
            contentSummary = parsed.summary(160).replace('\n', ' ');
        } else {
            contentSummary = "";
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = (int) (140 * density);
        setMeasuredDimension(width, resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float padding = 8 * density;
        float radius = 16 * density;

        cardRect.set(padding, padding,
                getWidth() - padding,
                getHeight() - padding);

        cardRect.offset(0, 4 * density);
        canvas.drawRoundRect(cardRect, radius, radius, shadowPaint);
        cardRect.offset(0, -4 * density);

        canvas.drawRoundRect(cardRect, radius, radius, cardPaint);

        if (note == null) {
            return;
        }

        float innerLeft = cardRect.left + 16 * density;
        float innerRight = cardRect.right - 16 * density;
        float innerTop = cardRect.top + 18 * density;

        if (note.isPinned()) {
            drawPinIcon(canvas, cardRect.right - 24 * density, cardRect.top + 24 * density);
            innerRight -= 24 * density;
        }

        String title = note.getTitle() == null ? "" : note.getTitle();
        CharSequence titleEll = TextUtils.ellipsize(title, titlePaint,
                innerRight - innerLeft, TextUtils.TruncateAt.END);
        canvas.drawText(titleEll, 0, titleEll.length(), innerLeft,
                innerTop + titlePaint.getTextSize(), titlePaint);

        float bodyTop = innerTop + titlePaint.getTextSize() + 12 * density;

        String content = contentSummary;
        float bodyMaxWidth = innerRight - innerLeft;
        CharSequence line1 = TextUtils.ellipsize(content, bodyPaint, bodyMaxWidth, TextUtils.TruncateAt.END);
        canvas.drawText(line1, 0, line1.length(), innerLeft, bodyTop + bodyPaint.getTextSize(), bodyPaint);

        if (line1.length() < content.length()) {
            String rest = content.substring(line1.length()).trim();
            CharSequence line2 = TextUtils.ellipsize(rest, bodyPaint, bodyMaxWidth, TextUtils.TruncateAt.END);
            canvas.drawText(line2, 0, line2.length(), innerLeft,
                    bodyTop + bodyPaint.getTextSize() * 2 + 4 * density, bodyPaint);
        }

        String date = formatDate(note.getUpdatedAt());
        if (!TextUtils.isEmpty(date)) {
            float dateWidth = datePaint.measureText(date);
            canvas.drawText(date,
                    innerRight - dateWidth,
                    cardRect.bottom - 14 * density,
                    datePaint);
        }
    }

    private void drawPinIcon(Canvas canvas, float cx, float cy) {
        Path path = new Path();
        float size = 8 * density;
        path.moveTo(cx, cy - size);
        path.lineTo(cx - size * 0.7f, cy);
        path.lineTo(cx - size * 0.3f, cy);
        path.lineTo(cx - size * 0.3f, cy + size);
        path.lineTo(cx + size * 0.3f, cy + size);
        path.lineTo(cx + size * 0.3f, cy);
        path.lineTo(cx + size * 0.7f, cy);
        path.close();
        canvas.drawPath(path, pinPaint);
    }

    private String formatDate(String iso) {
        if (iso == null || iso.length() < 10) return "";
        return iso.substring(0, 10);
    }
}
