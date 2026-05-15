package com.note_app.app.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.note_app.app.ui.spans.CheckboxSpanApplier;
import com.note_app.app.ui.spans.ImageSpanApplier;
import com.note_app.app.util.AppContext;

import java.util.HashSet;
import java.util.Set;

public class NoteContentTextWatcher implements TextWatcher {

    private final Context context;
    private final AppContext app;
    private final EditText editText;

    private final Set<String> imageTracker = new HashSet<>();

    public NoteContentTextWatcher(EditText editText, Context context, AppContext app){
        this.context = context;
        this.app = app;
        this.editText = editText;
    }

    @Override
    public void afterTextChanged(Editable s) {
        CheckboxSpanApplier.clear(s);
        ImageSpanApplier.clear(s);

        CheckboxSpanApplier.apply(context, editText, s);
        ImageSpanApplier.apply(context, editText, s, app, imageTracker);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
