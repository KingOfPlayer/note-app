package com.note_app.app.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.note_app.app.R;
import com.note_app.app.model.Note;
import com.note_app.app.ui.widget.ColorPaletteView;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

public class NoteEditActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "noteId";

    private AppContext app;
    private EditText titleField;
    private EditText contentField;
    private ColorPaletteView paletteView;
    private CheckBox pinnedCheck;
    private Long noteId;
    private String selectedColor = "#FFF59D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        app = new AppContext(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleField = findViewById(R.id.field_title);
        contentField = findViewById(R.id.field_content);
        paletteView = findViewById(R.id.palette);
        pinnedCheck = findViewById(R.id.check_pinned);

        paletteView.setOnColorSelectedListener(hex -> selectedColor = hex);
        paletteView.setSelectedColor(selectedColor);

        if (getIntent().hasExtra(EXTRA_NOTE_ID)) {
            noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1L);
            if (noteId <= 0) noteId = null;
        }
        if (noteId != null) {
            loadExisting();
        }
    }

    private void loadExisting() {
        BackgroundExecutor.run(
                () -> app.notes().list(0, 200),
                new BackgroundExecutor.Callback<java.util.List<Note>>() {
                    @Override
                    public void onSuccess(java.util.List<Note> result) {
                        for (Note n : result) {
                            if (n.getId().equals(noteId)) {
                                titleField.setText(n.getTitle());
                                contentField.setText(n.getContent());
                                pinnedCheck.setChecked(n.isPinned());
                                if (n.getColor() != null) {
                                    selectedColor = n.getColor();
                                    paletteView.setSelectedColor(selectedColor);
                                }
                                return;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteEditActivity.this,
                                "Not yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        if (noteId == null) menu.findItem(R.id.action_delete).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_save) {
            save();
            return true;
        }
        if (id == R.id.action_delete) {
            deleteNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String title = titleField.getText().toString().trim();
        String content = contentField.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "Baslik bos olamaz", Toast.LENGTH_SHORT).show();
            return;
        }
        Note n = new Note();
        n.setTitle(title);
        n.setContent(content);
        n.setColor(selectedColor);
        n.setPinned(pinnedCheck.isChecked());

        BackgroundExecutor.run(
                () -> noteId == null ? app.notes().create(n) : app.notes().update(noteId, n),
                new BackgroundExecutor.Callback<Note>() {
                    @Override
                    public void onSuccess(Note result) {
                        Toast.makeText(NoteEditActivity.this,
                                noteId == null ? "Not olusturuldu" : "Not guncellendi",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteEditActivity.this,
                                "Kaydedilemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteNote() {
        if (noteId == null) return;
        BackgroundExecutor.run(
                () -> { app.notes().delete(noteId); return true; },
                new BackgroundExecutor.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(NoteEditActivity.this,
                                "Not silindi", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteEditActivity.this,
                                "Silinemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
