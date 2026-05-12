package com.note_app.app.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.note_app.app.R;
import com.note_app.app.model.FileMeta;
import com.note_app.app.model.Note;
import com.note_app.app.ui.widget.ColorPaletteView;
import com.note_app.app.util.AppContext;
import com.note_app.app.util.BackgroundExecutor;

import java.io.InputStream;
import java.util.List;

public class NoteEditActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "noteId";

    private AppContext app;
    private EditText titleField;
    private EditText contentField;
    private ColorPaletteView paletteView;
    private CheckBox pinnedCheck;
    private View filesSection;
    private LinearLayout filesContainer;

    private Long noteId;
    private String selectedColor = "#FFF59D";
    private ActivityResultLauncher<String> filePickerLauncher;

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
        filesSection = findViewById(R.id.files_section);
        filesContainer = findViewById(R.id.files_container);

        paletteView.setOnColorSelectedListener(hex -> selectedColor = hex);
        paletteView.setSelectedColor(selectedColor);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::onFileSelected);

        findViewById(R.id.btn_add_file).setOnClickListener(v -> {
            if (noteId == null) {
                Toast.makeText(this,
                        "Once notu kaydedin sonra dosya ekleyebilirsiniz",
                        Toast.LENGTH_LONG).show();
                return;
            }
            filePickerLauncher.launch("*/*");
        });

        if (getIntent().hasExtra(EXTRA_NOTE_ID)) {
            noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1L);
            if (noteId <= 0) noteId = null;
        }
        updateFilesSectionVisibility();
        if (noteId != null) {
            loadExisting();
            refreshFiles();
        }
    }

    private void updateFilesSectionVisibility() {
        filesSection.setVisibility(noteId != null ? View.VISIBLE : View.GONE);
    }

    private void loadExisting() {
        BackgroundExecutor.run(
                () -> app.notes().list(0, 200),
                new BackgroundExecutor.Callback<List<Note>>() {
                    @Override
                    public void onSuccess(List<Note> result) {
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

    private void refreshFiles() {
        if (noteId == null) return;
        BackgroundExecutor.run(
                () -> app.files().listForNote(noteId),
                new BackgroundExecutor.Callback<List<FileMeta>>() {
                    @Override
                    public void onSuccess(List<FileMeta> result) {
                        renderFiles(result);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteEditActivity.this,
                                "Dosyalar yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderFiles(List<FileMeta> files) {
        filesContainer.removeAllViews();
        if (files.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Henuz ek yok");
            empty.setTextColor(0xFF9E9E9E);
            empty.setPadding(0, 8, 0, 8);
            filesContainer.addView(empty);
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        for (FileMeta f : files) {
            View row = inflater.inflate(R.layout.item_file, filesContainer, false);
            TextView name = row.findViewById(R.id.text_filename);
            TextView size = row.findViewById(R.id.text_size);
            ImageButton del = row.findViewById(R.id.btn_delete_file);
            name.setText(f.getFilename());
            size.setText(humanSize(f.getSize()));
            del.setOnClickListener(v -> deleteFile(f));
            filesContainer.addView(row);
        }
    }

    private String humanSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    private void onFileSelected(Uri uri) {
        if (uri == null || noteId == null) return;
        String[] meta = queryFileMeta(uri);
        String filename = meta[0];
        String mimeType = meta[1];

        BackgroundExecutor.run(
                () -> {
                    try (InputStream is = getContentResolver().openInputStream(uri)) {
                        if (is == null) throw new IllegalStateException("Dosya okunamadi");
                        byte[] bytes = is.readAllBytes();
                        return app.files().upload(noteId, filename, bytes, mimeType);
                    }
                },
                new BackgroundExecutor.Callback<FileMeta>() {
                    @Override
                    public void onSuccess(FileMeta result) {
                        Toast.makeText(NoteEditActivity.this,
                                "Dosya yuklendi", Toast.LENGTH_SHORT).show();
                        refreshFiles();
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(NoteEditActivity.this,
                                "Yuklenemedi: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String[] queryFileMeta(Uri uri) {
        String filename = "dosya";
        String mime = getContentResolver().getType(uri);
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) filename = cursor.getString(idx);
            }
        }
        return new String[]{filename, mime};
    }

    private void deleteFile(FileMeta file) {
        new AlertDialog.Builder(this)
                .setTitle("Dosyayi sil")
                .setMessage(file.getFilename() + " silinsin mi?")
                .setPositiveButton("Sil", (d, w) ->
                        BackgroundExecutor.run(
                                () -> { app.files().delete(file.getId()); return true; },
                                new BackgroundExecutor.Callback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        Toast.makeText(NoteEditActivity.this,
                                                "Silindi", Toast.LENGTH_SHORT).show();
                                        refreshFiles();
                                    }

                                    @Override
                                    public void onError(Throwable error) {
                                        Toast.makeText(NoteEditActivity.this,
                                                "Silinemedi: " + error.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }))
                .setNegativeButton("Vazgec", null)
                .show();
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

        boolean isCreate = (noteId == null);
        BackgroundExecutor.run(
                () -> isCreate ? app.notes().create(n) : app.notes().update(noteId, n),
                new BackgroundExecutor.Callback<Note>() {
                    @Override
                    public void onSuccess(Note result) {
                        Toast.makeText(NoteEditActivity.this,
                                isCreate ? "Not olusturuldu, simdi dosya ekleyebilirsiniz"
                                        : "Not guncellendi",
                                Toast.LENGTH_SHORT).show();
                        if (isCreate) {
                            noteId = result.getId();
                            updateFilesSectionVisibility();
                            invalidateOptionsMenu();
                            refreshFiles();
                        } else {
                            finish();
                        }
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
