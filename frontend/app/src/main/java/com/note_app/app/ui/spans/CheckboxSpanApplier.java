package com.note_app.app.ui.spans;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.note_app.app.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckboxSpanApplier {

    static final Pattern pattern = Pattern.compile("\\[checkbox,([01])\\]");
    public static void apply(Context context, EditText editText, Editable s) {

        Matcher matcher = pattern.matcher(s.toString());

        Paint.FontMetricsInt metrics = editText.getPaint().getFontMetricsInt();
        int size = Math.abs(metrics.descent - metrics.ascent);

        while (matcher.find()) {
            int start = matcher.start();
            int end   = matcher.end();
            String state = matcher.group(1);

            int resId = state.equals("1") ? R.drawable.ic_checked : R.drawable.ic_unchecked;
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), resId, null);

            d.setBounds(0, 0, size - 4, size - 4);

            d = new InsetDrawable(d, 8, 4, 8, 4);

            d.setBounds(0, 0, size - 4 + 16, size - 4 + 8);

            s.setSpan(new CheckboxImageSpan(d), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new CheckboxClickableSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void clear(Editable s) {
        for (CheckboxImageSpan span : s.getSpans(0, s.length(), CheckboxImageSpan.class))
            s.removeSpan(span);
        for (CheckboxClickableSpan span : s.getSpans(0, s.length(), CheckboxClickableSpan.class))
            s.removeSpan(span);
    }

    public static String convertNormalString(String input) {
        if (input == null) return null;

        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String state = matcher.group(1);

            String replacement = "1".equals(state) ? "[x]" : "[ ]";

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);

        return sb.toString();
    }
}
