package com.note_app.app.ui.spans;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckboxClickableSpan extends ClickableSpan {
    @Override
    public void onClick(View widget) {
        // Re-query the span's actual current position
        Spannable spannable = (Spannable) ((TextView) widget).getText();
        int currentStart = spannable.getSpanStart(this);
        int currentEnd   = spannable.getSpanEnd(this);

        if (currentStart < 0 || currentEnd < 0) return; // span already removed

        // Read the LIVE state from the actual text, not the captured variable
        String liveText  = spannable.subSequence(currentStart, currentEnd).toString();
        Matcher m        = CheckboxSpanApplier.pattern.matcher(liveText);

        if (m.find()) {
            String liveState = m.group(1);
            String newState  = liveState.equals("0") ? "1" : "0";
            ((Editable) spannable).replace(currentStart, currentEnd,
                "[checkbox," + newState + "]");
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
